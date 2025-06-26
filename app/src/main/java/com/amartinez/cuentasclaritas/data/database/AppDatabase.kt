package com.amartinez.cuentasclaritas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amartinez.cuentasclaritas.data.database.dao.ProductDao
import com.amartinez.cuentasclaritas.data.database.dao.TicketDao
import com.amartinez.cuentasclaritas.data.database.dao.UserDao
import com.amartinez.cuentasclaritas.data.database.dao.ProductAssignmentDao
import com.amartinez.cuentasclaritas.data.database.entities.ProductEntity
import com.amartinez.cuentasclaritas.data.database.entities.TicketEntity
import com.amartinez.cuentasclaritas.data.database.entities.UserEntity
import com.amartinez.cuentasclaritas.data.database.entities.ProductAssignmentEntity

@Database(
    entities = [ProductEntity::class, TicketEntity::class, UserEntity::class, ProductAssignmentEntity::class],
    version = 2, // Incrementa la versión por el cambio de esquema
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun ticketDao(): TicketDao
    abstract fun userDao(): UserDao
    abstract fun productAssignmentDao(): ProductAssignmentDao
}
