package com.amartinez.cuentasclaritas.presentation.userassignment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amartinez.cuentasclaritas.data.database.entities.UserEntity
import com.amartinez.cuentasclaritas.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.crashlytics.FirebaseCrashlytics

@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _users = MutableStateFlow<List<String>>(listOf(""))
    val users: StateFlow<List<String>> = _users.asStateFlow()

    private val ticketId: Long = savedStateHandle.get<Long>("ticketId") ?: -1L

    fun onUserNameChange(index: Int, name: String) {
        if (index !in _users.value.indices) {
            val msg = "UserRegistrationViewModel.onUserNameChange: Índice fuera de rango ($index) para usuarios (${_users.value.size})"
            FirebaseCrashlytics.getInstance().log(msg)
            return
        }
        _users.value = _users.value.toMutableList().also { it[index] = name }
    }

    fun onAddUser() {
        _users.value = _users.value + ""
    }

    fun onRemoveUser(index: Int) {
        val list = _users.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _users.value = if (list.isEmpty()) listOf("") else list
        } else {
            val msg = "UserRegistrationViewModel.onRemoveUser: Índice fuera de rango ($index) para usuarios (${list.size})"
            FirebaseCrashlytics.getInstance().log(msg)
        }
    }

    fun saveUsers(onSaved: () -> Unit) {
        viewModelScope.launch {
            try {
                val userEntities = _users.value.filter { it.isNotBlank() }.map { UserEntity(name = it) }
                userEntities.forEach { userRepository.insertUser(it) }
                onSaved()
            } catch (e: Exception) {
                val msg = "UserRegistrationViewModel.saveUsers: Error al guardar usuarios: ${e.localizedMessage}"
                FirebaseCrashlytics.getInstance().log(msg)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}
