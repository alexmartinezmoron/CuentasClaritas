package com.amartinez.cuentasclaritas.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _isAuthenticated.value = firebaseAuth.currentUser != null
        }
    }

    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun loginWithEmail() {
        val email = _email.value.trim()
        val password = _password.value
        if (!isValidEmail(email)) {
            val msg = "AuthViewModel.loginWithEmail: Email no válido ($email)"
            FirebaseCrashlytics.getInstance().log(msg)
            _uiState.value = AuthUiState.Error("Email no válido")
            return
        }
        if (password.length < 6) {
            val msg = "AuthViewModel.loginWithEmail: Contraseña demasiado corta para $email"
            FirebaseCrashlytics.getInstance().log(msg)
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(auth.currentUser)
                    } else {
                        val msg = "AuthViewModel.loginWithEmail: Error autenticando $email: ${task.exception?.localizedMessage}"
                        FirebaseCrashlytics.getInstance().log(msg)
                        task.exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error de autenticación")
                    }
                }
        }
    }

    fun registerWithEmail() {
        val email = _email.value.trim()
        val password = _password.value
        if (!isValidEmail(email)) {
            val msg = "AuthViewModel.registerWithEmail: Email no válido ($email)"
            FirebaseCrashlytics.getInstance().log(msg)
            _uiState.value = AuthUiState.Error("Email no válido")
            return
        }
        if (password.length < 6) {
            val msg = "AuthViewModel.registerWithEmail: Contraseña demasiado corta para $email"
            FirebaseCrashlytics.getInstance().log(msg)
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(auth.currentUser)
                    } else {
                        val msg = "AuthViewModel.registerWithEmail: Error registrando $email: ${task.exception?.localizedMessage}"
                        FirebaseCrashlytics.getInstance().log(msg)
                        task.exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error de registro")
                    }
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _uiState.value = AuthUiState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(auth.currentUser)
                    } else {
                        val msg = "AuthViewModel.loginWithGoogle: Error con Google Sign-In: ${task.exception?.localizedMessage}"
                        FirebaseCrashlytics.getInstance().log(msg)
                        task.exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error con Google Sign-In")
                    }
                }
        }
    }

    fun signInAnonymously() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(auth.currentUser)
                    } else {
                        val msg = "AuthViewModel.signInAnonymously: Error de sesión anónima: ${task.exception?.localizedMessage}"
                        FirebaseCrashlytics.getInstance().log(msg)
                        task.exception?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error de sesión anónima")
                    }
                }
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            val msg = "AuthViewModel.logout: Excepción al cerrar sesión: ${e.localizedMessage}"
            FirebaseCrashlytics.getInstance().log(msg)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
