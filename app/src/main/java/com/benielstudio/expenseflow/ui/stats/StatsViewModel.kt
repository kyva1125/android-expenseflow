package com.benielstudio.expenseflow.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class CategoryStat(
    val categoryName: String,
    val totalAmount: Double,
    val colorHex: String,
    val percentage: Float
)

data class MonthlyTrendStat(
    val monthName: String,
    val amount: Double
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currency: StateFlow<String> = settingsRepository.currency

    private val _selectedType = MutableStateFlow(ExpenseType.EXPENSE)
    val selectedType: StateFlow<ExpenseType> = _selectedType.asStateFlow()

    // Aggregate category stats
    val categoryStats: StateFlow<List<CategoryStat>> = combine(
        repository.allExpenses,
        repository.allCategories,
        _selectedType
    ) { expenses, categories, type ->
        val filtered = expenses.filter { it.type == type }
        val totalSum = filtered.sumOf { it.amount }
        
        if (totalSum == 0.0) return@combine emptyList<CategoryStat>()

        val groupMap = filtered.groupBy { it.category }
        val catMap = categories.associateBy { it.name }

        groupMap.map { (catName, list) ->
            val sum = list.sumOf { it.amount }
            val color = catMap[catName]?.colorHex ?: "#607D8B"
            val pct = (sum / totalSum).toFloat()
            CategoryStat(catName, sum, color, pct)
        }.sortedByDescending { it.totalAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aggregate monthly trends (last 6 months)
    val monthlyTrendStats: StateFlow<List<MonthlyTrendStat>> = combine(
        repository.allExpenses,
        _selectedType
    ) { expenses, type ->
        val filtered = expenses.filter { it.type == type }
        
        val sdf = SimpleDateFormat("MMM", Locale.getDefault())
        
        // Let's create an ordered list of the last 6 months
        val last6Months = (0..5).map { index ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -index)
            sdf.format(cal.time)
        }.reversed()

        val monthMap = filtered.groupBy {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            sdf.format(cal.time)
        }

        last6Months.map { monthName ->
            val sum = monthMap[monthName]?.sumOf { it.amount } ?: 0.0
            MonthlyTrendStat(monthName, sum)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedType(type: ExpenseType) {
        _selectedType.value = type
    }
}
