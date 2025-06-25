package com.amartinez.cuentasclaritas.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeName: String?,
    val date: Long, // Timestamp
    val totalAmount: Double
)
