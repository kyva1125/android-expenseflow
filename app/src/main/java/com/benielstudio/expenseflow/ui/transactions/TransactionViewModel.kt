package com.benielstudio.expenseflow.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benielstudio.expenseflow.data.CategoryEntity
import com.benielstudio.expenseflow.data.ExpenseEntity
import com.benielstudio.expenseflow.data.ExpenseType
import com.benielstudio.expenseflow.repository.ExpenseRepository
import com.benielstudio.expenseflow.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currency: StateFlow<String> = settingsRepository.currency

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow("ALL")
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("ALL")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(
        repository.allExpenses,
        _searchQuery,
        _selectedTypeFilter,
        _selectedCategoryFilter
    ) { expenses, query, typeFilter, catFilter ->
        expenses.filter { expense ->
            val matchesQuery = expense.title.contains(query, ignoreCase = true) ||
                    expense.note.contains(query, ignoreCase = true)
            
            val matchesType = when (typeFilter) {
                "INCOME" -> expense.type == ExpenseType.INCOME
                "EXPENSE" -> expense.type == ExpenseType.EXPENSE
                else -> true
            }

            val matchesCategory = if (catFilter == "ALL") {
                true
            } else {
                expense.category.equals(catFilter, ignoreCase = true)
            }

            matchesQuery && matchesType && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(filter: String) {
        _selectedTypeFilter.value = filter
    }

    fun setCategoryFilter(filter: String) {
        _selectedCategoryFilter.value = filter
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        type: ExpenseType,
        date: Long,
        note: String
    ) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    type = type,
                    date = date,
                    note = note
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}
