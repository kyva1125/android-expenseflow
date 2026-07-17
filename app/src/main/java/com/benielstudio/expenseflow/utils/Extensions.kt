package com.benielstudio.expenseflow.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.formatCurrency(currencySymbol: String = "$"): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val formatted = format.format(this)
    // Replace standard currency symbol with the preferred symbol if needed,
    // or just return a formatted version with the symbol prepended.
    return try {
        val cleanFormatted = formatted.replace(format.currency?.symbol ?: "$", "").trim()
        "$currencySymbol$cleanFormatted"
    } catch (e: Exception) {
        String.format(Locale.getDefault(), "%s%.2f", currencySymbol, this)
    }
}

fun Long.formatDate(pattern: String = "MMM dd, yyyy"): String {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        ""
    }
}
