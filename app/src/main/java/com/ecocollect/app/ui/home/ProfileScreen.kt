package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ecocollect.app.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(rootNavController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val currentUser = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Logged in as: ${currentUser?.email}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            authViewModel.signOut()
            rootNavController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }) {
            Text("Sign Out")
        }
    }
}
