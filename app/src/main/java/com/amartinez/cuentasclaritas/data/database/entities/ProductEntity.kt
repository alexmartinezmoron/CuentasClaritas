package com.amartinez.cuentasclaritas.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = TicketEntity::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE // If a ticket is deleted, its products are also deleted
        )
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ticketId: Long, // Foreign key to link with TicketEntity
    val name: String,
    val quantity: Double,
    val price: Double
)
