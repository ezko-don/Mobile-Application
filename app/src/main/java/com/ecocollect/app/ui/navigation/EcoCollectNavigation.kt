package com.ecocollect.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ecocollect.app.ui.auth.LoginScreen
import com.ecocollect.app.ui.auth.RegisterScreen
import com.ecocollect.app.ui.screens.HistoryScreen
import com.ecocollect.app.ui.viewmodel.AuthViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Text

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    // ...other screens...
}

@Composable
fun EcoCollectNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("register") {
            RegisterScreen(
                onRegistrationSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable(Screen.Home.route) {
            // HomeScreen content
            Button(onClick = { navController.navigate(Screen.History.route) }) {
                Text("View History")
            }
        }
        composable(Screen.History.route) { HistoryScreen() }
        // ...other composables...
    }
}
