package com.amartinez.cuentasclaritas.data.database.dao

import androidx.room.*
import com.amartinez.cuentasclaritas.data.database.entities.ProductAssignmentEntity

@Dao
interface ProductAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<ProductAssignmentEntity>)

    @Query("SELECT * FROM product_assignments WHERE ticketId = :ticketId")
    suspend fun getAssignmentsForTicket(ticketId: Long): List<ProductAssignmentEntity>

    @Query("SELECT * FROM product_assignments WHERE assignmentId = :assignmentId")
    suspend fun getAssignmentById(assignmentId: Long): ProductAssignmentEntity?

    @Update
    suspend fun updateAssignment(assignment: ProductAssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: ProductAssignmentEntity)
}

