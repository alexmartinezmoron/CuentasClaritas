package com.amartinez.cuentasclaritas.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import com.amartinez.cuentasclaritas.R
// import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    // viewModel: SettingsViewModel = hiltViewModel() // Uncomment if you create a ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Button(onClick = { throw RuntimeException("Excepción de prueba") }) {
            Text(stringResource(id = R.string.settings_save))
        }
        // TODO: Implement settings UI
    }
}
