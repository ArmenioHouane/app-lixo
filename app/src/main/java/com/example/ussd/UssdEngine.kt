package com.example.ussd

import com.example.data.repository.ReportRepository
import com.example.domain.model.ReportOrigin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID

sealed class UssdStep {
    object MainMenu : UssdStep()
    object NewReportNeighborhood : UssdStep()
    object NewReportWasteType : UssdStep()
    object NewReportReference : UssdStep()
    object NewReportConfirm : UssdStep()
    object QueryCode : UssdStep()
    object Finished : UssdStep()
}

data class UssdSessionData(
    val sessionId: String = UUID.randomUUID().toString(),
    var currentStep: UssdStep = UssdStep.MainMenu,
    var neighborhood: String = "",
    var wasteType: String = "",
    var reference: String = "",
    var phoneNumber: String = "+258 84 990 0112"
)

class UssdEngine(private val repository: ReportRepository) {
    private var session = UssdSessionData()

    fun resetSession(): String {
        session = UssdSessionData()
        return getMainMenuMessage()
    }

    private fun getMainMenuMessage(): String {
        return """
            CON Bem-vindo ao Denúncia de Lixo Maputo
            1. Registar denúncia
            2. Consultar denúncia
            3. Informações
            4. Sair
        """.trimIndent()
    }

    suspend fun processInput(input: String): String = withContext(Dispatchers.IO) {
        val trimmed = input.trim()

        when (session.currentStep) {
            is UssdStep.MainMenu -> {
                when (trimmed) {
                    "1" -> {
                        session.currentStep = UssdStep.NewReportNeighborhood
                        """
                            CON Escolha o Bairro:
                            1. Chamanculo
                            2. Maxaquene
                            3. Alto-Maé
                            4. Mavalane
                            5. Outro
                        """.trimIndent()
                    }
                    "2" -> {
                        session.currentStep = UssdStep.QueryCode
                        "CON Digite o código da denúncia (ex: DLX-250515-000123):"
                    }
                    "3" -> {
                        session.currentStep = UssdStep.Finished
                        """
                            END Conselho Municipal de Maputo
                            Serviço de Gestão de Resíduos Sólidos Urbanos.
                            Canal USSD oficial: *384*73407#
                            Serviço Operacional.
                        """.trimIndent()
                    }
                    "4" -> {
                        session.currentStep = UssdStep.Finished
                        "END Obrigado por manter Maputo limpa!"
                    }
                    else -> {
                        """
                            CON Opção inválida.
                            1. Registar denúncia
                            2. Consultar denúncia
                            3. Informações
                            4. Sair
                        """.trimIndent()
                    }
                }
            }

            is UssdStep.NewReportNeighborhood -> {
                session.neighborhood = when (trimmed) {
                    "1" -> "Chamanculo"
                    "2" -> "Maxaquene"
                    "3" -> "Alto-Maé"
                    "4" -> "Mavalane"
                    else -> trimmed.ifEmpty { "Centro da Cidade" }
                }
                session.currentStep = UssdStep.NewReportWasteType
                """
                    CON Tipo de problema:
                    1. Lixo doméstico
                    2. Entulho / Construção
                    3. Lixo hospitalar
                    4. Poda de árvores
                    5. Outro
                """.trimIndent()
            }

            is UssdStep.NewReportWasteType -> {
                session.wasteType = when (trimmed) {
                    "1" -> "Lixo doméstico"
                    "2" -> "Entulho / Construção"
                    "3" -> "Lixo hospitalar"
                    "4" -> "Poda de árvores"
                    else -> "Outro tipo"
                }
                session.currentStep = UssdStep.NewReportReference
                "CON Descreva o ponto de referência (ex: Próximo à Escola Primária):"
            }

            is UssdStep.NewReportReference -> {
                session.reference = trimmed.ifEmpty { "Sem referência especificada" }
                session.currentStep = UssdStep.NewReportConfirm
                """
                    CON Confirmar dados:
                    Bairro: ${session.neighborhood}
                    Tipo: ${session.wasteType}
                    Ref: ${session.reference.take(30)}
                    
                    1. Confirmar e enviar
                    2. Cancelar
                """.trimIndent()
            }

            is UssdStep.NewReportConfirm -> {
                if (trimmed == "1") {
                    val code = repository.createReport(
                        neighborhood = session.neighborhood,
                        wasteType = session.wasteType,
                        reference = session.reference,
                        phoneNumber = session.phoneNumber,
                        otpVerified = true,
                        origin = ReportOrigin.USSD
                    )
                    session.currentStep = UssdStep.Finished
                    """
                        END Denúncia registada com sucesso!
                        Código de acompanhamento:
                        $code
                        
                        Guarde este código para consultas futuras.
                    """.trimIndent()
                } else {
                    session.currentStep = UssdStep.Finished
                    "END Registo de denúncia cancelado."
                }
            }

            is UssdStep.QueryCode -> {
                val report = repository.getReportSync(trimmed)
                session.currentStep = UssdStep.Finished
                if (report != null) {
                    """
                        END Denúncia: ${report.code}
                        Estado: ${report.status.label}
                        Bairro: ${report.neighborhood}
                        Tipo: ${report.wasteType}
                        Data: ${report.createdAt}
                    """.trimIndent()
                } else {
                    "END Código '$trimmed' não encontrado na base de dados."
                }
            }

            is UssdStep.Finished -> {
                resetSession()
            }
        }
    }
}

object UssdHttpClient {
    suspend fun executeUssdRequest(
        serverUrl: String,
        sessionId: String,
        serviceCode: String,
        phoneNumber: String,
        text: String,
        timeoutSeconds: Int = 8
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL(serverUrl)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = timeoutSeconds * 1000
                readTimeout = timeoutSeconds * 1000
                doOutput = true
                doInput = true
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                setRequestProperty("User-Agent", "DenunciaLixoMaputo-USSD-Client/1.0")
            }

            val postData = buildString {
                append("sessionId=").append(URLEncoder.encode(sessionId, "UTF-8"))
                append("&serviceCode=").append(URLEncoder.encode(serviceCode, "UTF-8"))
                append("&phoneNumber=").append(URLEncoder.encode(phoneNumber, "UTF-8"))
                append("&text=").append(URLEncoder.encode(text, "UTF-8"))
            }

            conn.outputStream.use { os: OutputStream ->
                val inputBytes = postData.toByteArray(Charsets.UTF_8)
                os.write(inputBytes, 0, inputBytes.size)
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, Charsets.UTF_8))
                val response = reader.readText()
                reader.close()
                response.ifBlank { "END Sem resposta do servidor USSD PHP." }
            } else {
                val errorStream = conn.errorStream
                val err = errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                "END Erro HTTP $responseCode do servidor USSD PHP: ${err.take(80)}"
            }
        }
    }
}
