package com.amartinez.cuentasclaritas.presentation.tickettable.model

data class TicketProduct(
    var quantity: Int = 1,
    var name: String = "",
    var unitPrice: Double = 0.0
) {
    val totalPrice: Double get() = quantity * unitPrice
}

