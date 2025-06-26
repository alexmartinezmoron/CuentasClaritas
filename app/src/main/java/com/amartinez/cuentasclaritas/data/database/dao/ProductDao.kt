package com.amartinez.cuentasclaritas.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amartinez.cuentasclaritas.data.database.entities.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE ticketId = :ticketId")
    suspend fun getProductsByTicketId(ticketId: Long): List<ProductEntity>
}
