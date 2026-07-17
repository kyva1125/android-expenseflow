package com.benielstudio.expenseflow.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("expense_flow_prefs", Context.MODE_PRIVATE)

    private val _currency = MutableStateFlow(prefs.getString("currency", "$") ?: "$")
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setCurrency(symbol: String) {
        prefs.edit().putString("currency", symbol).apply()
        _currency.value = symbol
    }

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean("dark_theme", enabled).apply()
        _isDarkTheme.value = enabled
    }
}
