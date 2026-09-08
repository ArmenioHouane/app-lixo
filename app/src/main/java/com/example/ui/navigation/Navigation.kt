package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.WasteApplication
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminLoginScreen
import com.example.ui.admin.AdminMapScreen
import com.example.ui.admin.AdminReportDetailScreen
import com.example.ui.admin.AdminReportsScreen
import com.example.ui.admin.AdminSettingsScreen
import com.example.ui.admin.AdminUsersScreen
import com.example.ui.auth.ProfileLoginScreen
import com.example.ui.citizen.home.HomeScreen
import com.example.ui.citizen.info.SystemInfoScreen
import com.example.ui.citizen.reports.CitizenHistoryScreen
import com.example.ui.citizen.reports.CitizenReportDetailScreen
import com.example.ui.citizen.reports.CitizenReportListScreen
import com.example.ui.citizen.wizard.Step1NeighborhoodScreen
import com.example.ui.citizen.wizard.Step2WasteTypeScreen
import com.example.ui.citizen.wizard.Step3ReferenceScreen
import com.example.ui.citizen.wizard.Step4OtpScreen
import com.example.ui.citizen.wizard.Step5ConfirmScreen
import com.example.ui.citizen.wizard.SuccessScreen
import com.example.ui.citizen.wizard.WizardViewModel
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

object Routes {
    const val PROFILE_LOGIN = "profile_login"
    const val HOME = "citizen_home"
    const val WIZARD_STEP1 = "wizard_step1"
    const val WIZARD_STEP2 = "wizard_step2"
    const val WIZARD_STEP3 = "wizard_step3"
    const val WIZARD_STEP4 = "wizard_step4"
    const val WIZARD_STEP5 = "wizard_step5"
    const val WIZARD_SUCCESS = "wizard_success/{reportCode}"
    const val REPORTS = "citizen_reports"
    const val REPORT_DETAIL = "citizen_report_detail/{reportCode}"
    const val REPORT_HISTORY = "citizen_report_history/{reportCode}"
    const val CITIZEN_MAP = "citizen_map"
    const val SYSTEM_INFO = "citizen_system_info"

    // Admin routes
    const val ADMIN_LOGIN = "admin_login"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_REPORTS = "admin_reports"
    const val ADMIN_REPORT_DETAIL = "admin_report_detail/{reportCode}"
    const val ADMIN_MAP = "admin_map"
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_SETTINGS = "admin_settings"
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

private val CitizenBottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Início", Icons.Default.Home),
    BottomNavItem(Routes.REPORTS, "Denúncias", Icons.Default.ListAlt),
    BottomNavItem(Routes.CITIZEN_MAP, "Mapa", Icons.Default.Map),
    BottomNavItem(Routes.SYSTEM_INFO, "Info", Icons.Default.Info)
)

private val AdminBottomNavItems = listOf(
    BottomNavItem(Routes.ADMIN_DASHBOARD, "Painel", Icons.Default.Dashboard),
    BottomNavItem(Routes.ADMIN_REPORTS, "Denúncias", Icons.Default.Assignment),
    BottomNavItem(Routes.ADMIN_MAP, "Mapa", Icons.Default.Map),
    BottomNavItem(Routes.ADMIN_USERS, "Equipa", Icons.Default.People),
    BottomNavItem(Routes.ADMIN_SETTINGS, "Definições", Icons.Default.Settings)
)

@Composable
fun MainAppNavigation(app: WasteApplication) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Scoped ViewModel for Wizard so steps share state seamlessly
    val wizardViewModel = remember { WizardViewModel(app.repository) }

    val isCitizenNavVisible = currentRoute in listOf(
        Routes.HOME,
        Routes.REPORTS,
        Routes.CITIZEN_MAP,
        Routes.SYSTEM_INFO
    )

    val isAdminNavVisible = currentRoute in listOf(
        Routes.ADMIN_DASHBOARD,
        Routes.ADMIN_REPORTS,
        Routes.ADMIN_MAP,
        Routes.ADMIN_USERS,
        Routes.ADMIN_SETTINGS
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isCitizenNavVisible || isAdminNavVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val items = if (isCitizenNavVisible) CitizenBottomNavItems else AdminBottomNavItems

                NavigationBar(
                    containerColor = AppColors.Surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    items.forEach { item ->
                        val selected = currentRoute == item.route

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (selected) AppColors.Primary else AppColors.TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = AppTypography.Label.copy(
                                        fontSize = 11.sp,
                                        color = if (selected) AppColors.Primary else AppColors.TextSecondary
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = AppColors.PrimaryLight
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.PROFILE_LOGIN
            ) {
                // ================= AUTH / PROFILE LOGIN =================
                composable(Routes.PROFILE_LOGIN) {
                    ProfileLoginScreen(
                        onLoginSuccessCitizen = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.PROFILE_LOGIN) { inclusive = true }
                            }
                        },
                        onLoginSuccessAdmin = {
                            navController.navigate(Routes.ADMIN_DASHBOARD) {
                                popUpTo(Routes.PROFILE_LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                // ================= CITIZEN ROUTES =================
                composable(Routes.HOME) {
                    HomeScreen(
                        repository = app.repository,
                        onNavigateNewReport = {
                            wizardViewModel.resetWizard()
                            navController.navigate(Routes.WIZARD_STEP1)
                        },
                        onNavigateReports = { navController.navigate(Routes.REPORTS) },
                        onNavigateReportDetail = { code ->
                            navController.navigate("citizen_report_detail/$code")
                        },
                        onNavigateInfo = { navController.navigate(Routes.SYSTEM_INFO) },
                        onNavigateMap = { navController.navigate(Routes.CITIZEN_MAP) },
                        onNavigateLogin = {
                            navController.navigate(Routes.PROFILE_LOGIN) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.CITIZEN_MAP) {
                    AdminMapScreen(
                        repository = app.repository,
                        onNavigateDetail = { code ->
                            navController.navigate("citizen_report_detail/$code")
                        },
                        onNavigateBack = { navController.popBackStack() },
                        isAdminMode = false
                    )
                }

                // WIZARD STEPS
                composable(Routes.WIZARD_STEP1) {
                    Step1NeighborhoodScreen(
                        viewModel = wizardViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateNext = { navController.navigate(Routes.WIZARD_STEP2) }
                    )
                }

                composable(Routes.WIZARD_STEP2) {
                    Step2WasteTypeScreen(
                        viewModel = wizardViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateNext = { navController.navigate(Routes.WIZARD_STEP3) }
                    )
                }

                composable(Routes.WIZARD_STEP3) {
                    Step3ReferenceScreen(
                        viewModel = wizardViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateNext = { navController.navigate(Routes.WIZARD_STEP4) }
                    )
                }

                composable(Routes.WIZARD_STEP4) {
                    Step4OtpScreen(
                        viewModel = wizardViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateNext = { navController.navigate(Routes.WIZARD_STEP5) }
                    )
                }

                composable(Routes.WIZARD_STEP5) {
                    Step5ConfirmScreen(
                        viewModel = wizardViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateSuccess = { code ->
                            navController.navigate("wizard_success/$code") {
                                popUpTo(Routes.HOME)
                            }
                        }
                    )
                }

                composable(
                    route = Routes.WIZARD_SUCCESS,
                    arguments = listOf(navArgument("reportCode") { type = NavType.StringType })
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("reportCode") ?: ""
                    SuccessScreen(
                        reportCode = code,
                        onViewReport = { reportCode ->
                            navController.navigate("citizen_report_detail/$reportCode")
                        },
                        onBackToMenu = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.REPORTS) {
                    CitizenReportListScreen(
                        repository = app.repository,
                        onNavigateDetail = { code ->
                            navController.navigate("citizen_report_detail/$code")
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.REPORT_DETAIL,
                    arguments = listOf(navArgument("reportCode") { type = NavType.StringType })
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("reportCode") ?: ""
                    CitizenReportDetailScreen(
                        reportCode = code,
                        repository = app.repository,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateHistory = { reportCode ->
                            navController.navigate("citizen_report_history/$reportCode")
                        }
                    )
                }

                composable(
                    route = Routes.REPORT_HISTORY,
                    arguments = listOf(navArgument("reportCode") { type = NavType.StringType })
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("reportCode") ?: ""
                    CitizenHistoryScreen(
                        reportCode = code,
                        repository = app.repository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.SYSTEM_INFO) {
                    SystemInfoScreen(
                        repository = app.repository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // ================= ADMIN ROUTES =================
                composable(Routes.ADMIN_LOGIN) {
                    AdminLoginScreen(
                        onLoginSuccess = {
                            navController.navigate(Routes.ADMIN_DASHBOARD) {
                                popUpTo(Routes.ADMIN_LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.ADMIN_DASHBOARD) {
                    AdminDashboardScreen(
                        repository = app.repository,
                        onNavigateReports = { navController.navigate(Routes.ADMIN_REPORTS) },
                        onNavigateMap = { navController.navigate(Routes.ADMIN_MAP) },
                        onNavigateUsers = { navController.navigate(Routes.ADMIN_USERS) },
                        onNavigateCitizen = {
                            navController.navigate(Routes.PROFILE_LOGIN) {
                                popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.ADMIN_REPORTS) {
                    AdminReportsScreen(
                        repository = app.repository,
                        onNavigateDetail = { code ->
                            navController.navigate("admin_report_detail/$code")
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.ADMIN_REPORT_DETAIL,
                    arguments = listOf(navArgument("reportCode") { type = NavType.StringType })
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("reportCode") ?: ""
                    AdminReportDetailScreen(
                        reportCode = code,
                        repository = app.repository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.ADMIN_MAP) {
                    AdminMapScreen(
                        repository = app.repository,
                        onNavigateDetail = { code ->
                            navController.navigate("admin_report_detail/$code")
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.ADMIN_USERS) {
                    AdminUsersScreen(
                        repository = app.repository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.ADMIN_SETTINGS) {
                    AdminSettingsScreen(
                        repository = app.repository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
