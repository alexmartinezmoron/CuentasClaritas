package com.amartinez.cuentasclaritas.presentation.tickettable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amartinez.cuentasclaritas.data.database.entities.TicketEntity
import com.amartinez.cuentasclaritas.domain.usecase.SaveProductsUseCase
import com.amartinez.cuentasclaritas.domain.usecase.SaveTicketWithProductsUseCase
import com.amartinez.cuentasclaritas.presentation.tickettable.model.TicketProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.regex.Pattern

@HiltViewModel
class TicketTableViewModel @Inject constructor(
    private val saveProductsUseCase: SaveProductsUseCase,
    private val saveTicketWithProductsUseCase: SaveTicketWithProductsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _products = MutableStateFlow<List<TicketProduct>>(emptyList())
    val products: StateFlow<List<TicketProduct>> = _products.asStateFlow()

    private val _totalExtracted = MutableStateFlow<Double?>(null)
    val totalExtracted: StateFlow<Double?> = _totalExtracted.asStateFlow()

    private val _showSavedAlert = MutableStateFlow(false)
    val showSavedAlert: StateFlow<Boolean> = _showSavedAlert.asStateFlow()

    init {
        val text = savedStateHandle.get<String>("ticketText") ?: ""
        processText(text)
    }

    fun updateProduct(index: Int, product: TicketProduct) {
        _products.value = _products.value.toMutableList().also { it[index] = product }
    }

    fun addProduct() {
        val updated = _products.value.toMutableList().apply {
            add(TicketProduct())
        }
        _products.value = updated
    }

    fun removeProduct(index: Int) {
        val current = _products.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _products.value = if (current.isEmpty()) listOf(TicketProduct()) else current
        }
    }

    private fun processText(text: String) {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val products = mutableListOf<TicketProduct>()
        var total: Double? = null

        // Palabras clave que NO deben considerarse productos
        val excludeKeywords = listOf(
            "TOTAL",
            "NETO",
            "IVA",
            "BRUTO",
            "A PAGAR",
            "PAGO",
            "OPERACION",
            "CONTACTLESS",
            "EUR"
        )

        // Regex para productos tipo "nombre precio"
        val productSimpleRegex = Regex("^([\\w\\s.,\\-]+?)\\s+(\\d+[\\.,]\\d{2})$")

        for (line in lines) {
            // Ignorar líneas que contengan palabras clave
            if (excludeKeywords.any { line.uppercase().contains(it) }) continue

            val simpleMatch = productSimpleRegex.matchEntire(line)
            if (simpleMatch != null) {
                val (name, price) = simpleMatch.destructured
                products.add(
                    TicketProduct(
                        quantity = 1,
                        name = name.trim(),
                        unitPrice = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                    )
                )
                continue
            }

            // Buscar total
            if (line.uppercase().contains("A PAGAR")) {
                val priceRegex = Regex("(\\d+[\\.,]\\d{2})")
                priceRegex.find(line)?.groupValues?.get(1)?.let {
                    total = it.replace(",", ".").toDoubleOrNull()
                }
            }
        }

        if (products.isEmpty()) {
            products.add(TicketProduct())
        }

        _products.value = products
        _totalExtracted.value = total
    }

    fun onSaveProducts() {
        // Esta función ya no debe usarse, pero si la necesitas, debes pasar un ticketId válido.
        // Por ejemplo, podrías lanzar una excepción o dejarla vacía para evitar confusiones.
        throw UnsupportedOperationException("Usa onSaveTicketAndProducts() para guardar productos asociados a un ticket")
    }

    fun onSaveTicketAndProducts() {
        viewModelScope.launch {
            val ticket = TicketEntity(
                storeName = "Tienda genérica",
                date = System.currentTimeMillis(),
                totalAmount = _totalExtracted.value ?: _products.value.sumOf { it.quantity * it.unitPrice }
            )
            saveTicketWithProductsUseCase(ticket, _products.value)
            _showSavedAlert.value = true
        }
    }

    fun dismissSavedAlert() {
        _showSavedAlert.value = false
    }
}
