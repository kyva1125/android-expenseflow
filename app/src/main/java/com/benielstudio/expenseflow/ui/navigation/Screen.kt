package com.benielstudio.expenseflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Transactions : Screen("transactions", "Transactions", Icons.Default.List)
    object Stats : Screen("stats", "Stats", Icons.Default.PieChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AddTransaction : Screen("add_transaction", "Add Transaction")
}
