package com.amartinez.cuentasclaritas.di

import android.content.Context
import androidx.room.Room
import com.amartinez.cuentasclaritas.data.database.AppDatabase
import com.amartinez.cuentasclaritas.data.database.dao.ProductDao
import com.amartinez.cuentasclaritas.data.database.dao.TicketDao
import com.amartinez.cuentasclaritas.data.database.dao.UserDao
import com.amartinez.cuentasclaritas.data.database.dao.ProductAssignmentDao
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
        )
        .addMigrations(MIGRATION_1_2)
        .build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideTicketDao(db: AppDatabase): TicketDao = db.ticketDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideProductAssignmentDao(db: AppDatabase): ProductAssignmentDao = db.productAssignmentDao()
}

// MIGRACIÓN REAL DE 1 A 2
val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS users (userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS product_assignments (assignmentId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ticketId INTEGER NOT NULL, productId INTEGER NOT NULL, userId INTEGER NOT NULL)")
    }
}
