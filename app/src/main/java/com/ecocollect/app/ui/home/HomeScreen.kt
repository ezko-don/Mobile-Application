package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ecocollect.app.ui.viewmodel.ScheduleViewModel
import com.ecocollect.app.ui.screens.QRScannerScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Schedule : Screen("schedule")
    object History : Screen("history")
    object Profile : Screen("profile")
    object NewSchedule : Screen("new_schedule")
    object QRScanner : Screen("qr_scanner")
    object ScheduleDetail : Screen("schedule_detail/{scheduleId}") {
        fun createRoute(scheduleId: String) = "schedule_detail/$scheduleId"
    }

    val icon: ImageVector?
        get() = when (this) {
            is Schedule -> Icons.Default.Home
            is History -> Icons.Default.History
            is Profile -> Icons.Default.Person
            is QRScanner -> Icons.Default.QrCodeScanner
            else -> null
        }

    val title: String?
        get() = when (this) {
            is Schedule -> "Schedule"
            is History -> "History"
            is Profile -> "Profile"
            is QRScanner -> "QR Scanner"
            else -> null
        }
}

@Composable
fun HomeScreen(navController: NavController, scheduleViewModel: ScheduleViewModel = hiltViewModel()) {
    val innerNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scheduleViewModel.errorFlow.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    val screens = listOf(
        Screen.Schedule,
        Screen.History,
        Screen.Profile,
        Screen.NewSchedule
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    screen.icon?.let {
                        NavigationBarItem(
                            icon = { Icon(it, contentDescription = null) },
                            label = { Text(screen.title!!) },
                            selected = currentDestination?.hierarchy?.any { dest -> dest.route == screen.route } == true,
                            onClick = {
                                innerNavController.navigate(screen.route) {
                                    popUpTo(innerNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    innerNavController.navigate(Screen.QRScanner.route)
                }
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Schedule.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Schedule.route) { ScheduleScreen(viewModel = scheduleViewModel, navController = innerNavController) }
            composable(Screen.Profile.route) { ProfileScreen(rootNavController = navController) }
            composable(Screen.NewSchedule.route) { NewScheduleScreen(navController = innerNavController, viewModel = scheduleViewModel) }
            composable(Screen.QRScanner.route) {
                QRScannerScreen(
                    navController = innerNavController,
                    onQRCodeScanned = { qrData ->
                        // Handle QR code scanning result
                        // For now, just show a snackbar
                        scope.launch {
                            snackbarHostState.showSnackbar("QR Code Scanned: $qrData")
                        }
                    }
                )
            }
            composable(
                route = Screen.ScheduleDetail.route,
                arguments = listOf(navArgument("scheduleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val scheduleId = backStackEntry.arguments?.getString("scheduleId")
                requireNotNull(scheduleId) { "scheduleId parameter wasn't found." }
                ScheduleDetailScreen(scheduleId = scheduleId, viewModel = scheduleViewModel)
            }
        }
    }
}

@Composable
fun ProfileScreen(rootNavController: NavController) {
    Text("Profile Screen")
}
