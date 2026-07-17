package com.benielstudio.expenseflow.repository

import com.benielstudio.expenseflow.data.CategoryEntity
import com.benielstudio.expenseflow.data.ExpenseDao
import com.benielstudio.expenseflow.data.ExpenseEntity
import com.benielstudio.expenseflow.data.ExpenseType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allCategories: Flow<List<CategoryEntity>> = expenseDao.getAllCategories()

    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByCategory(category)
    }

    fun getExpensesByType(type: ExpenseType): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByType(type)
    }

    fun getExpensesSumByType(type: ExpenseType): Flow<Double?> {
        return expenseDao.getExpensesSumByType(type)
    }

    fun getExpensesSumByTypeFromDate(type: ExpenseType, startDate: Long): Flow<Double?> {
        return expenseDao.getExpensesSumByTypeFromDate(type, startDate)
    }

    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun insertCategory(category: CategoryEntity) {
        expenseDao.insertCategory(category)
    }

    suspend fun getCategoryByName(name: String): CategoryEntity? {
        return expenseDao.getCategoryByName(name)
    }
}
