package com.amartinez.cuentasclaritas.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
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
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun checkIfAuthenticated() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            _uiState.value = AuthUiState.Success(user)
        }
    }

    fun loginWithEmail() {
        val email = _email.value.trim()
        val password = _password.value
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Email no válido")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(firebaseAuth.currentUser)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error de autenticación")
                    }
                }
        }
    }

    fun registerWithEmail() {
        val email = _email.value.trim()
        val password = _password.value
        if (!isValidEmail(email)) {
            _uiState.value = AuthUiState.Error("Email no válido")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(firebaseAuth.currentUser)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error de registro")
                    }
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _uiState.value = AuthUiState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(firebaseAuth.currentUser)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error con Google Sign-In")
                    }
                }
        }
    }

    fun loginAnonymously() {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.value = AuthUiState.Success(firebaseAuth.currentUser)
                    } else {
                        _uiState.value = AuthUiState.Error(task.exception?.localizedMessage ?: "Error de sesión anónima")
                    }
                }
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

