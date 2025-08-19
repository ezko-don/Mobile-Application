package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ecocollect.app.data.model.EWasteItems
import com.ecocollect.app.ui.viewmodel.ScheduleViewModel

@Composable
fun NewScheduleScreen(navController: NavController, viewModel: ScheduleViewModel = hiltViewModel()) {
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    
    val isFormValid = address.isNotBlank() && selectedItems.isNotEmpty()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Pickup Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Pickup Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }

            item {
                Text(
                    text = "Select E-Waste Items",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(EWasteItems.ITEMS) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item in selectedItems,
                        onCheckedChange = { isChecked ->
                            selectedItems = if (isChecked) {
                                selectedItems + item
                            } else {
                                selectedItems - item
                            }
                        }
                    )
                    Text(
                        text = item,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        viewModel.addSchedule(
                            address = address,
                            notes = notes.takeIf { it.isNotBlank() },
                            items = selectedItems.toList()
                        )
                        navController.popBackStack()
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
        }
    }
}
