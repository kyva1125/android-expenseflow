package com.benielstudio.expenseflow.ui.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benielstudio.expenseflow.repository.ExpenseRepository
import com.benielstudio.expenseflow.repository.SettingsRepository
import com.benielstudio.expenseflow.utils.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val currency: StateFlow<String> = settingsRepository.currency
    val isDarkTheme: StateFlow<Boolean> = settingsRepository.isDarkTheme

    fun setCurrency(symbol: String) {
        settingsRepository.setCurrency(symbol)
    }

    fun toggleTheme(enabled: Boolean) {
        settingsRepository.setDarkTheme(enabled)
    }

    fun exportDataToCsv(context: Context, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val expenses = repository.allExpenses.first()
                if (expenses.isEmpty()) {
                    onComplete(false, "No transactions to export.")
                    return@launch
                }
                
                val csvContent = buildString {
                    append("ID,Title,Amount,Category,Type,Date,Note\n")
                    expenses.forEach { expense ->
                        val escapedTitle = expense.title.replace("\"", "\"\"")
                        val escapedNote = expense.note.replace("\"", "\"\"")
                        val formattedDate = expense.date.formatDate("yyyy-MM-dd HH:mm:ss")
                        append("${expense.id},\"$escapedTitle\",${expense.amount},\"${expense.category}\",${expense.type},$formattedDate,\"$escapedNote\"\n")
                    }
                }

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_SUBJECT, "ExpenseFlow Export")
                    putExtra(Intent.EXTRA_TEXT, csvContent)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                val chooserIntent = Intent.createChooser(intent, "Export Transactions CSV").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooserIntent)
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.localizedMessage ?: "Unknown error occurred during export.")
            }
        }
    }
}
