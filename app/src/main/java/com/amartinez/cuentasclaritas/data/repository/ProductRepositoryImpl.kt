package com.amartinez.cuentasclaritas.data.repository

import com.amartinez.cuentasclaritas.data.database.dao.ProductDao
import com.amartinez.cuentasclaritas.data.database.entities.ProductEntity
import com.amartinez.cuentasclaritas.domain.repository.ProductRepository
import com.amartinez.cuentasclaritas.presentation.tickettable.model.TicketProduct
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun saveProducts(products: List<TicketProduct>, ticketId: Long) {
        val entities = products.map { it.toEntity(ticketId) }
        productDao.insertProducts(entities)
    }
}

fun TicketProduct.toEntity(ticketId: Long): ProductEntity = ProductEntity(
    ticketId = ticketId,
    name = this.name,
    quantity = this.quantity.toDouble(),
    price = this.unitPrice
)
