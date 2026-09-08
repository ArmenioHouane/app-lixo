package com.example.ui.citizen.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.ReportRepository
import com.example.domain.model.ReportOrigin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WizardDraft(
    val selectedNeighborhood: String = "Chamanculo",
    val customNeighborhood: String = "",
    val selectedWasteType: String = "Lixo doméstico",
    val customWasteType: String = "",
    val reference: String = "",
    val phoneNumber: String = "+258 84 ",
    val otpCode: String = "",
    val isOtpVerified: Boolean = false,
    val mediaUri: String? = null,
    val mediaType: String? = null, // "IMAGE" or "VIDEO"
    val generatedCode: String? = null,
    val isSubmitting: Boolean = false
)

class WizardViewModel(private val repository: ReportRepository) : ViewModel() {
    private val _draft = MutableStateFlow(WizardDraft())
    val draft: StateFlow<WizardDraft> = _draft.asStateFlow()

    fun selectNeighborhood(neighborhood: String) {
        _draft.update { it.copy(selectedNeighborhood = neighborhood) }
    }

    fun setCustomNeighborhood(custom: String) {
        _draft.update { it.copy(customNeighborhood = custom) }
    }

    fun selectWasteType(type: String) {
        _draft.update { it.copy(selectedWasteType = type) }
    }

    fun setCustomWasteType(custom: String) {
        _draft.update { it.copy(customWasteType = custom) }
    }

    fun setReference(reference: String) {
        _draft.update { it.copy(reference = reference) }
    }

    fun setPhoneNumber(phone: String) {
        _draft.update { it.copy(phoneNumber = phone) }
    }

    fun setMedia(uri: String?, type: String?) {
        _draft.update { it.copy(mediaUri = uri, mediaType = type) }
    }

    fun clearMedia() {
        _draft.update { it.copy(mediaUri = null, mediaType = null) }
    }

    fun setOtpCode(code: String) {
        val filtered = code.filter { it.isDigit() }.take(8)
        _draft.update { it.copy(otpCode = filtered) }
    }

    fun verifyOtp(): Boolean {
        if (_draft.value.otpCode.length >= 4) {
            _draft.update { it.copy(isOtpVerified = true) }
            return true
        }
        return false
    }

    fun submitReport(onSuccess: (String) -> Unit) {
        val current = _draft.value
        if (current.isSubmitting) return

        _draft.update { it.copy(isSubmitting = true) }

        val finalNeighborhood = if (current.selectedNeighborhood == "Outro bairro") {
            current.customNeighborhood.ifBlank { "Bairro Não Especificado" }
        } else {
            current.selectedNeighborhood
        }

        val finalWasteType = if (current.selectedWasteType == "Outro tipo") {
            current.customWasteType.ifBlank { "Resíduos Diversos" }
        } else {
            current.selectedWasteType
        }

        viewModelScope.launch {
            val code = repository.createReport(
                neighborhood = finalNeighborhood,
                wasteType = finalWasteType,
                reference = current.reference.ifBlank { "Sem referência" },
                phoneNumber = current.phoneNumber.ifBlank { "+258 84 000 0000" },
                otpVerified = current.isOtpVerified,
                origin = ReportOrigin.APP,
                mediaUri = current.mediaUri,
                mediaType = current.mediaType
            )

            _draft.update {
                it.copy(
                    isSubmitting = false,
                    generatedCode = code
                )
            }
            onSuccess(code)
        }
    }

    fun resetWizard() {
        _draft.value = WizardDraft()
    }
}
