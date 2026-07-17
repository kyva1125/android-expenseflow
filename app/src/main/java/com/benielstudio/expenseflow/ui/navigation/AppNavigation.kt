package com.benielstudio.expenseflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.benielstudio.expenseflow.ui.dashboard.DashboardScreen
import com.benielstudio.expenseflow.ui.dashboard.DashboardViewModel
import com.benielstudio.expenseflow.ui.settings.SettingsScreen
import com.benielstudio.expenseflow.ui.settings.SettingsViewModel
import com.benielstudio.expenseflow.ui.stats.StatsScreen
import com.benielstudio.expenseflow.ui.stats.StatsViewModel
import com.benielstudio.expenseflow.ui.transactions.AddTransactionScreen
import com.benielstudio.expenseflow.ui.transactions.TransactionListScreen
import com.benielstudio.expenseflow.ui.transactions.TransactionViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Stats,
        Screen.Settings
    )

    val showBottomBarAndFab = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBarAndFab) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Dashboard.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBarAndFab) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddTransaction.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { paddingValues ->
        val dashboardViewModel: DashboardViewModel = hiltViewModel()
        val transactionViewModel: TransactionViewModel = hiltViewModel()
        val statsViewModel: StatsViewModel = hiltViewModel()
        val settingsViewModel: SettingsViewModel = hiltViewModel()

        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    onAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionListScreen(
                    viewModel = transactionViewModel
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = statsViewModel
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel
                )
            }

            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    viewModel = transactionViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
