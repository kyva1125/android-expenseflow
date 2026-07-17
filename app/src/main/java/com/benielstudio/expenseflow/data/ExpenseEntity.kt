package com.benielstudio.expenseflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseType {
    INCOME, EXPENSE
}

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: ExpenseType,
    val date: Long,
    val note: String
)
