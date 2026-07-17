package com.benielstudio.expenseflow.di

import android.content.Context
import androidx.room.Room
import com.benielstudio.expenseflow.data.ExpenseDao
import com.benielstudio.expenseflow.data.ExpenseDatabase
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
    fun provideExpenseDatabase(
        @ApplicationContext context: Context
    ): ExpenseDatabase {
        return Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            ExpenseDatabase.DATABASE_NAME
        )
        .addCallback(ExpenseDatabase.CALLBACK)
        .build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: ExpenseDatabase): ExpenseDao {
        return database.expenseDao()
    }
}
