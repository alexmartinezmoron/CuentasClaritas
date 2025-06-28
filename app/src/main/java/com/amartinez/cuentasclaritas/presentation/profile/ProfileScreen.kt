package com.amartinez.cuentasclaritas.presentation.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val user = remember { FirebaseAuth.getInstance().currentUser }
    val displayName = user?.displayName ?: if (user?.isAnonymous == true) "Usuario anónimo" else user?.email ?: "Usuario desconocido"
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        try {
                            FirebaseAuth.getInstance().signOut()
                            Log.d("ProfileScreen", "Sesión cerrada correctamente")
                            isLoading = false
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Error al cerrar sesión", e)
                            isLoading = false
                            snackbarHostState.showSnackbar("Error al cerrar sesión. Intenta de nuevo.")
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Cerrar sesión")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
