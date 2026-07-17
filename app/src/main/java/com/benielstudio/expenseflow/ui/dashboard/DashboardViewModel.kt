package com.benielstudio.expenseflow.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benielstudio.expenseflow.data.ExpenseEntity
import com.benielstudio.expenseflow.data.ExpenseType
import com.benielstudio.expenseflow.repository.ExpenseRepository
import com.benielstudio.expenseflow.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currency: StateFlow<String> = settingsRepository.currency

    val recentTransactions: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .map { expenses -> expenses.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = repository.getExpensesSumByType(ExpenseType.INCOME)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = repository.getExpensesSumByType(ExpenseType.EXPENSE)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance: StateFlow<Double> = combine(totalIncome, totalExpenses) { income, expense ->
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categoryBreakdown: StateFlow<Map<String, Double>> = repository.allExpenses
        .map { expenses ->
            expenses.filter { it.type == ExpenseType.EXPENSE }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}
