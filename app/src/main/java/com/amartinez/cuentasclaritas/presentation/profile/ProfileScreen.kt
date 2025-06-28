package com.amartinez.cuentasclaritas.presentation.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.res.stringResource
import com.amartinez.cuentasclaritas.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = remember { FirebaseAuth.getInstance().currentUser }
    val displayName = user?.displayName ?: if (user?.isAnonymous == true) "Usuario anónimo" else user?.email ?: "Usuario desconocido"
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Idioma
    val idiomasDisponibles = listOf(
        "es" to "Español",
        "en" to "English"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(Locale.getDefault().language) }
    val idiomaActual = idiomasDisponibles.find { it.first == selectedLanguage }?.second ?: "Español"

    // Guardar preferencia de idioma en SharedPreferences
    fun guardarIdioma(context: Context, language: String) {
        val prefs = context.getSharedPreferences("prefs_idioma", Context.MODE_PRIVATE)
        prefs.edit().putString("idioma", language).apply()
    }

    // Cambiar el idioma de la app
    fun cambiarIdioma(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        guardarIdioma(context, language)
    }

    // Reiniciar la Activity actual
    fun reiniciarActivity(context: Context) {
        val activity = context as? Activity
        activity?.let {
            val intent = it.intent
            it.finish()
            it.overridePendingTransition(0, 0)
            it.startActivity(intent)
            it.overridePendingTransition(0, 0)
        }
    }

    // Leer idioma guardado al iniciar
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("prefs_idioma", Context.MODE_PRIVATE)
        val idiomaGuardado = prefs.getString("idioma", null)
        if (idiomaGuardado != null && idiomaGuardado != selectedLanguage) {
            selectedLanguage = idiomaGuardado
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.profile_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (user?.isAnonymous == true) stringResource(id = R.string.anonymous_user) else displayName,
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
                            // El estado de autenticación en MainAppScreen controlará la UI, no navegamos manualmente
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Error al cerrar sesión", e)
                            isLoading = false
                            snackbarMessage = context.getString(R.string.logout_error)
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
                    Text(stringResource(id = R.string.logout))
                }
            }
            // Selector de idioma
            Spacer(modifier = Modifier.height(32.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = idiomaActual,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.language)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    idiomasDisponibles.forEach { (codigo, nombre) ->
                        DropdownMenuItem(
                            text = { Text(stringResource(id = if (codigo == "es") R.string.spanish else R.string.english)) },
                            onClick = {
                                expanded = false
                                if (selectedLanguage != codigo) {
                                    cambiarIdioma(context, codigo)
                                    selectedLanguage = codigo
                                    reiniciarActivity(context)
                                }
                            }
                        )
                    }
                }
            }
        }
        // Mostrar el Snackbar si hay un mensaje
        if (snackbarMessage != null) {
            LaunchedEffect(snackbarMessage) {
                snackbarHostState.showSnackbar(snackbarMessage!!)
                snackbarMessage = null
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
