package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Schedule : Screen("schedule", Icons.Default.Home, "Schedule")
    object History : Screen("history", Icons.Default.List, "History")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
    object NewSchedule : Screen("new_schedule", Icons.Default.Home, "New Schedule")
}

@Composable
fun HomeScreen(navController: NavController) {
    val innerNavController = rememberNavController()

    val screens = listOf(
        Screen.Schedule,
        Screen.History,
        Screen.Profile,
        Screen.NewSchedule
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
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
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Schedule.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Schedule.route) { ScheduleScreen(navController = innerNavController) }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Profile.route) { ProfileScreen(rootNavController = navController) }
            composable(Screen.NewSchedule.route) { NewScheduleScreen(navController = innerNavController) }
        }
    }
}

@Composable
fun HistoryScreen() {
    Text("History Screen")
}

@Composable
fun ProfileScreen(rootNavController: NavController) {
    Text("Profile Screen")
}
