package com.ecocollect.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ecocollect.app.ui.viewmodel.AuthViewModel
import com.ecocollect.app.ui.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import io.coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    rootNavController: NavController
) {
    val user = FirebaseAuth.getInstance().currentUser
    val profileState by profileViewModel.profileState.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()
    
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { profileViewModel.toggleEditMode() }) {
                        Icon(
                            if (profileState.editMode) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (profileState.editMode) "Cancel Edit" else "Edit Profile"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            ProfileHeader(
                user = user,
                userName = userProfile?.name ?: "User",
                editMode = profileState.editMode
            )
            
            // Stats Cards
            StatsSection(
                totalPoints = userProfile?.totalPoints ?: 0,
                co2Saved = userProfile?.co2SavedKg ?: 0.0
            )
            
            // Profile Details
            ProfileDetailsSection(
                userName = userProfile?.name ?: "",
                userPhone = userProfile?.phoneNumber ?: "",
                userAddress = userProfile?.address ?: "",
                isEditMode = profileState.editMode,
                onProfileUpdate = { name, phone, address ->
                    profileViewModel.updateProfileFields(name, phone, address)
                }
            )
            
            // Actions
            ActionsSection(
                onSignOut = {
                    authViewModel.signOut()
                    rootNavController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onChangePassword = { profileViewModel.showChangePasswordDialog() }
            )
        }
        
        // Show loading indicator
        if (profileState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        // Show error message if any
        profileState.errorMessage?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                // You could show a snackbar here
            }
        }
    }
    
    // Change Password Dialog
    if (profileState.showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { profileViewModel.hideChangePasswordDialog() },
            onPasswordChange = { oldPassword, newPassword ->
                profileViewModel.changePassword(oldPassword, newPassword)
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    user: FirebaseAuth.FirebaseUser?,
    userName: String,
    editMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatsSection(
    totalPoints: Int,
    co2Saved: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Total Points",
            value = "$totalPoints",
            icon = Icons.Default.Star,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "CO₂ Saved",
            value = "${String.format("%.1f", co2Saved)} kg",
            icon = Icons.Default.Eco,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileDetailsSection(
    userName: String,
    userPhone: String,
    userAddress: String,
    isEditMode: Boolean,
    onProfileUpdate: (String, String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Profile Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (isEditMode) {
                EditableProfileFields(
                    userName = userName,
                    userPhone = userPhone,
                    userAddress = userAddress,
                    onProfileUpdate = onProfileUpdate
                )
            } else {
                ReadOnlyProfileFields(
                    userName = userName,
                    userPhone = userPhone,
                    userAddress = userAddress
                )
            }
        }
    }
}

@Composable
private fun EditableProfileFields(
    userName: String,
    userPhone: String,
    userAddress: String,
    onProfileUpdate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(userName) }
    var phoneNumber by remember { mutableStateOf(userPhone) }
    var address by remember { mutableStateOf(userAddress) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        
        Button(
            onClick = {
                onProfileUpdate(name, phoneNumber, address)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
private fun ReadOnlyProfileFields(
    userName: String,
    userPhone: String,
    userAddress: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProfileField("Full Name", userName)
        ProfileField("Phone Number", userPhone)
        ProfileField("Address", userAddress)
        ProfileField("Member Since", "January 2024")
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ActionsSection(
    onSignOut: () -> Unit,
    onChangePassword: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Account Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedButton(
                onClick = onChangePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password")
            }
            
            OutlinedButton(
                onClick = { /* Navigate to QR Scanner */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan QR Code")
            }
            
            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out")
            }
        }
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onPasswordChange: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                        onPasswordChange(oldPassword, newPassword)
                        onDismiss()
                    }
                }
            ) {
                Text("Change Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
