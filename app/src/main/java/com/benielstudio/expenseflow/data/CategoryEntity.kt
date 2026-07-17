package com.benielstudio.expenseflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String
) {
    companion object {
        fun getDefaultCategories(): List<CategoryEntity> {
            return listOf(
                CategoryEntity(name = "Salary", icon = "attach_money", colorHex = "#4CAF50"),
                CategoryEntity(name = "Food & Dining", icon = "restaurant", colorHex = "#FF9800"),
                CategoryEntity(name = "Transport", icon = "directions_car", colorHex = "#2196F3"),
                CategoryEntity(name = "Shopping", icon = "shopping_bag", colorHex = "#E91E63"),
                CategoryEntity(name = "Utilities", icon = "home", colorHex = "#9C27B0"),
                CategoryEntity(name = "Entertainment", icon = "movie", colorHex = "#9E9E9E"),
                CategoryEntity(name = "Medical", icon = "medical_services", colorHex = "#F44336"),
                CategoryEntity(name = "Education", icon = "school", colorHex = "#3F51B5"),
                CategoryEntity(name = "Investments", icon = "trending_up", colorHex = "#009688"),
                CategoryEntity(name = "Others", icon = "more_horiz", colorHex = "#607D8B")
            )
        }
    }
}
