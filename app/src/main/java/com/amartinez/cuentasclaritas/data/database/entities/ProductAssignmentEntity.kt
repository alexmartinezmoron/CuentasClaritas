package com.amartinez.cuentasclaritas.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_assignments")
data class ProductAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val assignmentId: Long = 0,
    val ticketId: Long,
    val productId: Long,
    val userId: Long
)

