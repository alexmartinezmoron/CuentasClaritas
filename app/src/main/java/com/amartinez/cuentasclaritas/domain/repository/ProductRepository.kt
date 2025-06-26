package com.amartinez.cuentasclaritas.domain.repository

import com.amartinez.cuentasclaritas.presentation.tickettable.model.TicketProduct

interface ProductRepository {
    suspend fun saveProducts(products: List<TicketProduct>, ticketId: Long)
}
