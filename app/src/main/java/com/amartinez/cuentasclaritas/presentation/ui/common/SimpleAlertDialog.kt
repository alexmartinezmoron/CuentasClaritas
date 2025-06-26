package com.amartinez.cuentasclaritas.presentation.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

@Composable
fun SimpleAlertDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            },
            title = null,
            text = { Text(message) }
        )
    }
}

