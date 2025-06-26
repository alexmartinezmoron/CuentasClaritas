package com.amartinez.cuentasclaritas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.amartinez.cuentasclaritas.presentation.main.MainAppScreen
import com.amartinez.cuentasclaritas.ui.theme.CuentasClaritasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CuentasClaritasTheme {
                MainAppScreen()
            }
        }
    }
}
