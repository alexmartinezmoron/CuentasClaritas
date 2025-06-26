package com.amartinez.cuentasclaritas.domain.usecase

import com.amartinez.cuentasclaritas.data.database.entities.TicketEntity
import com.amartinez.cuentasclaritas.domain.repository.ProductRepository
import com.amartinez.cuentasclaritas.domain.repository.TicketRepository
import com.amartinez.cuentasclaritas.presentation.tickettable.model.TicketProduct
import javax.inject.Inject

class SaveTicketWithProductsUseCase @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(ticket: TicketEntity, products: List<TicketProduct>): Long {
        val ticketId = ticketRepository.insertTicket(ticket)
        productRepository.saveProducts(products, ticketId)
        return ticketId
    }
}
