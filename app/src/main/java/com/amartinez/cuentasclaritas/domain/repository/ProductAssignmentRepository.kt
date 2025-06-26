package com.amartinez.cuentasclaritas.domain.repository

import com.amartinez.cuentasclaritas.data.database.entities.ProductAssignmentEntity

interface ProductAssignmentRepository {
    suspend fun insertAssignments(assignments: List<ProductAssignmentEntity>)
    suspend fun getAssignmentsForTicket(ticketId: Long): List<ProductAssignmentEntity>
    suspend fun updateAssignment(assignment: ProductAssignmentEntity)
    suspend fun deleteAssignment(assignment: ProductAssignmentEntity)
}

