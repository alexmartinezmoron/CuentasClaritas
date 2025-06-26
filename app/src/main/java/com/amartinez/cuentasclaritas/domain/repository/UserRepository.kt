package com.amartinez.cuentasclaritas.domain.repository

import com.amartinez.cuentasclaritas.data.database.entities.UserEntity

interface UserRepository {
    suspend fun insertUser(user: UserEntity): Long
    suspend fun getAllUsers(): List<UserEntity>
    suspend fun getUserById(userId: Long): UserEntity?
}

