package com.amartinez.cuentasclaritas.data.repository

import com.amartinez.cuentasclaritas.data.database.dao.UserDao
import com.amartinez.cuentasclaritas.data.database.entities.UserEntity
import com.amartinez.cuentasclaritas.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    override suspend fun getAllUsers(): List<UserEntity> = userDao.getAllUsers()
    override suspend fun getUserById(userId: Long): UserEntity? = userDao.getUserById(userId)
}

