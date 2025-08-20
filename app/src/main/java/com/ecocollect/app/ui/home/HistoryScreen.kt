package com.ecocollect.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.widget.ProgressBar
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ecocollect.app.ui.components.PickupScheduleItem
import com.ecocollect.app.ui.viewmodel.ScheduleViewModel

@Composable
fun HistoryScreen(viewModel: ScheduleViewModel, navController: NavController) {
    val historySchedules by viewModel.historySchedules.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pickup History") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historySchedules) { schedule ->
                        PickupScheduleItem(schedule = schedule) {
                            navController.navigate(Screen.ScheduleDetail.createRoute(schedule.id))
                        }
                    }
                }
            }
        }
    }
}
