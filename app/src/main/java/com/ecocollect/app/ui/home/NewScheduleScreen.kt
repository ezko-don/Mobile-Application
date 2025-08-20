package com.ecocollect.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ecocollect.app.data.model.EWasteItems
import com.ecocollect.app.ui.viewmodel.ScheduleViewModel
import com.ecocollect.app.ui.viewmodel.ScheduleUiState
import com.ecocollect.app.ui.components.ErrorDialog
import com.ecocollect.app.ui.components.LoadingDialog
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleScreen(navController: NavController, viewModel: ScheduleViewModel) {
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val selectedItems = remember { mutableStateListOf<String>() }
    
    val isFormValid = address.isNotBlank() && selectedItems.isNotEmpty()
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.scheduleAdded.collectLatest {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule a Pickup") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Pickup Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select E-Waste Items:", style = MaterialTheme.typography.titleMedium)
            EWasteChecklist(selectedItems)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.addSchedule(
                        address = address,
                        notes = notes.takeIf { it.isNotBlank() },
                        items = selectedItems.toList()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Confirm Pickup")
            }
            
            if (selectedItems.isEmpty()) {
                Text(
                    text = "Please select at least one e-waste item",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Show loading dialog
        if (uiState is ScheduleUiState.Loading) {
            LoadingDialog()
        }

        // Show error dialog
        errorMessage?.let { message ->
            ErrorDialog(
                message = message,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

@Composable
fun EWasteChecklist(selectedItems: SnapshotStateList<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        items(EWasteItems.ITEMS) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (selectedItems.contains(item)) {
                            selectedItems.remove(item)
                        } else {
                            selectedItems.add(item)
                        }
                    }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = selectedItems.contains(item),
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            selectedItems.add(item)
                        } else {
                            selectedItems.remove(item)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = item)
            }
        }
    }
}
