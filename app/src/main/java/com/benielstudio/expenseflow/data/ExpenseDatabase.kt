package com.benielstudio.expenseflow.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ExpenseEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ExpenseDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_flow_db"

        val CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed default categories
                CategoryEntity.getDefaultCategories().forEach { category ->
                    db.execSQL(
                        "INSERT INTO categories (name, icon, colorHex) VALUES ('${category.name}', '${category.icon}', '${category.colorHex}')"
                    )
                }
            }
        }
    }
}
