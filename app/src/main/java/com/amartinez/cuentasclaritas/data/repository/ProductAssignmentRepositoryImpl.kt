package com.amartinez.cuentasclaritas.data.repository

import com.amartinez.cuentasclaritas.data.database.dao.ProductAssignmentDao
import com.amartinez.cuentasclaritas.data.database.entities.ProductAssignmentEntity
import com.amartinez.cuentasclaritas.domain.repository.ProductAssignmentRepository
import javax.inject.Inject

class ProductAssignmentRepositoryImpl @Inject constructor(
    private val assignmentDao: ProductAssignmentDao
) : ProductAssignmentRepository {
    override suspend fun insertAssignments(assignments: List<ProductAssignmentEntity>) =
        assignmentDao.insertAssignments(assignments)

    override suspend fun getAssignmentsForTicket(ticketId: Long): List<ProductAssignmentEntity> =
        assignmentDao.getAssignmentsForTicket(ticketId)

    override suspend fun updateAssignment(assignment: ProductAssignmentEntity) =
        assignmentDao.updateAssignment(assignment)

    override suspend fun deleteAssignment(assignment: ProductAssignmentEntity) =
        assignmentDao.deleteAssignment(assignment)
}

