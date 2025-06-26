package com.amartinez.cuentasclaritas.presentation.userassignment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amartinez.cuentasclaritas.data.database.entities.ProductAssignmentEntity
import com.amartinez.cuentasclaritas.data.database.entities.UserEntity
import com.amartinez.cuentasclaritas.domain.repository.ProductAssignmentRepository
import com.amartinez.cuentasclaritas.domain.repository.UserRepository
import com.amartinez.cuentasclaritas.data.database.dao.ProductDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductAssignmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val assignmentRepository: ProductAssignmentRepository,
    private val productDao: ProductDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ticketId: Long = savedStateHandle.get<Long>("ticketId") ?: -1L

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    private val _products = MutableStateFlow<List<String>>(emptyList())
    val products: StateFlow<List<String>> = _products.asStateFlow()

    // Mapa: userId -> lista de nombres de productos asignados
    private val _assignments = MutableStateFlow<Map<Long, MutableList<String>>>(mutableMapOf())
    val assignments: StateFlow<Map<Long, List<String>>> = _assignments

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    init {
        viewModelScope.launch {
            val userList = userRepository.getAllUsers()
            _users.value = userList
            val productEntities = productDao.getProductsByTicketId(ticketId)
            _products.value = productEntities.map { it.name }
            // Inicializa el mapa de asignaciones vacío
            _assignments.value = userList.associate { it.userId to mutableListOf<String>() }
        }
    }

    fun onAssignProduct(userId: Long, productName: String) {
        val map = _assignments.value.toMutableMap()
        val userProducts = map[userId] ?: mutableListOf()
        if (userProducts.contains(productName)) {
            userProducts.remove(productName)
        } else {
            userProducts.add(productName)
        }
        map[userId] = userProducts
        _assignments.value = map
    }

    fun saveAssignments() {
        if (!isAssignmentValid()) {
            // No guardar si la validación falla
            _saveSuccess.value = false
            return
        }
        viewModelScope.launch {
            val productEntities = productDao.getProductsByTicketId(ticketId)
            val nameToId: Map<String, Long> = productEntities.associate { it.name to it.id }
            val assignmentsToSave: List<ProductAssignmentEntity> = _assignments.value.flatMap { (userId: Long, productNames: MutableList<String>) ->
                productNames.mapNotNull { productName: String ->
                    nameToId[productName]?.let { productId: Long ->
                        ProductAssignmentEntity(
                            ticketId = ticketId,
                            productId = productId,
                            userId = userId
                        )
                    }
                }
            }
            assignmentRepository.insertAssignments(assignmentsToSave)
            _saveSuccess.value = true
        }
    }

    // Devuelve true si todos los productos han sido asignados a al menos un usuario
    fun isAssignmentValid(): Boolean {
        val allProducts = _products.value
        val assignedProducts = _assignments.value.values.flatten().toSet()
        return allProducts.isNotEmpty() && allProducts.all { it in assignedProducts }
    }
}
