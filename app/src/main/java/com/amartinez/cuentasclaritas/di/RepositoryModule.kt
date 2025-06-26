package com.amartinez.cuentasclaritas.di

import com.amartinez.cuentasclaritas.domain.repository.ProductRepository
import com.amartinez.cuentasclaritas.domain.repository.TicketRepository
import com.amartinez.cuentasclaritas.domain.repository.UserRepository
import com.amartinez.cuentasclaritas.domain.repository.ProductAssignmentRepository
import com.amartinez.cuentasclaritas.data.repository.ProductRepositoryImpl
import com.amartinez.cuentasclaritas.data.repository.TicketRepositoryImpl
import com.amartinez.cuentasclaritas.data.repository.UserRepositoryImpl
import com.amartinez.cuentasclaritas.data.repository.ProductAssignmentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        impl: TicketRepositoryImpl
    ): TicketRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductAssignmentRepository(
        impl: ProductAssignmentRepositoryImpl
    ): ProductAssignmentRepository
}
