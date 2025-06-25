package com.amartinez.cuentasclaritas

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.amartinez.cuentasclaritas.presentation.scanticket.ScanTicketScreen
import com.amartinez.cuentasclaritas.ui.theme.CuentasClaritasTheme
import dagger.hilt.android.AndroidEntryPoint // Added import
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@AndroidEntryPoint // Added annotation
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CuentasClaritasTheme {
                ScanTicketScreen(
                    onTicketScanned = { bitmap ->
                        // TODO: Handle the scanned ticket bitmap
                        // For now, let's just log its dimensions
                        Log.d("MainActivity", "Ticket Scanned. Bitmap dimensions: ${bitmap.width}x${bitmap.height}")
                    }
                )
            }
        }
    }
}

// You can decide if you want to keep the Greeting composable and its preview.
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CuentasClaritasTheme {
        Greeting("Android")
    }
}
