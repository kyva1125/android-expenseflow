package com.benielstudio.expenseflow.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object IconUtils {
    fun getIconByName(name: String): ImageVector {
        return when (name) {
            "attach_money" -> Icons.Default.AttachMoney
            "restaurant" -> Icons.Default.Restaurant
            "directions_car" -> Icons.Default.DirectionsCar
            "shopping_bag" -> Icons.Default.ShoppingBag
            "home" -> Icons.Default.Home
            "movie" -> Icons.Default.Movie
            "medical_services" -> Icons.Default.MedicalServices
            "school" -> Icons.Default.School
            "trending_up" -> Icons.Default.TrendingUp
            else -> Icons.Default.MoreHoriz
        }
    }

    fun getColorFromHex(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            Color(0xFF607D8B) // Fallback slate gray
        }
    }
}
