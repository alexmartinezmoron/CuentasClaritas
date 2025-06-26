package com.amartinez.cuentasclaritas.presentation.ticketlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TicketListScreen(
    // viewModel: TicketListViewModel = hiltViewModel() // Uncomment if you create a ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Listado de Tickets",
            style = MaterialTheme.typography.headlineMedium
        )
        // TODO: Implement ticket list UI
    }
}
