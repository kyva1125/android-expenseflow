package com.benielstudio.expenseflow.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromExpenseType(value: ExpenseType): String {
        return value.name
    }

    @TypeConverter
    fun toExpenseType(value: String): ExpenseType {
        return try {
            ExpenseType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ExpenseType.EXPENSE
        }
    }
}
