package com.amartinez.cuentasclaritas.di

import android.content.Context
import androidx.room.Room
import com.amartinez.cuentasclaritas.data.database.AppDatabase
import com.amartinez.cuentasclaritas.data.database.dao.ProductDao
import com.amartinez.cuentasclaritas.data.database.dao.TicketDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cuentas_claritas_db"
        ).build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideTicketDao(db: AppDatabase): TicketDao = db.ticketDao()
}

