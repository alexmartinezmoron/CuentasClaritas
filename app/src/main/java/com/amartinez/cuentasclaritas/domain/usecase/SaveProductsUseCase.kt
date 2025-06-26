package com.amartinez.cuentasclaritas.domain.usecase

import com.amartinez.cuentasclaritas.domain.repository.ProductRepository
import com.amartinez.cuentasclaritas.presentation.tickettable.model.TicketProduct
import javax.inject.Inject

class SaveProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(products: List<TicketProduct>, ticketId: Long) {
        repository.saveProducts(products, ticketId)
    }
}
