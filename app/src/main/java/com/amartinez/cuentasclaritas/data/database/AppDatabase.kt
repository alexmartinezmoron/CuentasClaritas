package com.amartinez.cuentasclaritas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amartinez.cuentasclaritas.data.database.dao.ProductDao
import com.amartinez.cuentasclaritas.data.database.dao.TicketDao
import com.amartinez.cuentasclaritas.data.database.entities.ProductEntity
import com.amartinez.cuentasclaritas.data.database.entities.TicketEntity

@Database(
    entities = [ProductEntity::class, TicketEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun ticketDao(): TicketDao
}

