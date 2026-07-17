package com.benielstudio.expenseflow.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.benielstudio.expenseflow.data.ExpenseType
import com.benielstudio.expenseflow.ui.theme.ExpenseColor
import com.benielstudio.expenseflow.ui.theme.IncomeColor
import com.benielstudio.expenseflow.utils.IconUtils
import com.benielstudio.expenseflow.utils.formatCurrency

@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    modifier: Modifier = Modifier
) {
    val currency by viewModel.currency.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val categoryStats by viewModel.categoryStats.collectAsState()
    val monthlyTrendStats by viewModel.monthlyTrendStats.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Stats Header
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Segmented Control (Income / Expense toggle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selectedType == ExpenseType.EXPENSE) ExpenseColor else Color.Transparent)
                    .clickable { viewModel.setSelectedType(ExpenseType.EXPENSE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Expenses",
                    color = if (selectedType == ExpenseType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selectedType == ExpenseType.INCOME) IncomeColor else Color.Transparent)
                    .clickable { viewModel.setSelectedType(ExpenseType.INCOME) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Income",
                    color = if (selectedType == ExpenseType.INCOME) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Monthly Trend Line Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monthly Cash Flow",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (monthlyTrendStats.all { it.amount == 0.0 }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Not enough data for cash flow trend.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    MonthlyTrendLineChart(
                        trendData = monthlyTrendStats,
                        currency = currency,
                        lineColor = if (selectedType == ExpenseType.INCOME) IncomeColor else ExpenseColor
                    )
                }
            }
        }

        // Category Stats Title
        Text(
            text = if (selectedType == ExpenseType.EXPENSE) "Expenses by Category" else "Income by Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Category Stats List
        if (categoryStats.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    categoryStats.forEach { stat ->
                        val color = IconUtils.getColorFromHex(stat.colorHex)
                        val animatedProgress by animateFloatAsState(
                            targetValue = stat.percentage,
                            animationSpec = tween(durationMillis = 800), label = "BarProgress"
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val defaultCategories = com.benielstudio.expenseflow.data.CategoryEntity.getDefaultCategories()
                                val categoryConfig = defaultCategories.find { it.name.equals(stat.categoryName, ignoreCase = true) }
                                Icon(
                                    imageVector = IconUtils.getIconByName(categoryConfig?.icon ?: "more_horiz"),
                                    contentDescription = stat.categoryName,
                                    tint = color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stat.categoryName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = stat.totalAmount.formatCurrency(currency),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = animatedProgress,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = color,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Text(
                                        text = "${(stat.percentage * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.width(32.dp),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyTrendLineChart(
    trendData: List<MonthlyTrendStat>,
    currency: String,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    val maxVal = trendData.maxOfOrNull { it.amount } ?: 1.0
    val maxY = if (maxVal == 0.0) 1.0 else maxVal * 1.2

    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f).toArgb()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width
        val height = size.height
        val paddingLeft = 40.dp.toPx()
        val paddingBottom = 30.dp.toPx()
        
        val graphWidth = width - paddingLeft - 10.dp.toPx()
        val graphHeight = height - paddingBottom - 10.dp.toPx()

        // Draw Y Grid lines (3 levels)
        val gridColor = Color.LightGray.copy(alpha = 0.15f)
        for (i in 0..2) {
            val y = 10.dp.toPx() + graphHeight * (i / 2f)
            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
            
            // Draw axis labels
            val gridVal = maxY - (maxY * (i / 2f))
            drawContext.canvas.nativeCanvas.drawText(
                gridVal.formatCurrency(currency).split(".")[0], // Int format
                5.dp.toPx(),
                y + 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = textColor
                    textSize = 10.sp.toPx()
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
            )
        }

        if (trendData.size > 1) {
            val points = trendData.mapIndexed { index, stat ->
                val x = paddingLeft + (index.toFloat() / (trendData.size - 1)) * graphWidth
                val y = 10.dp.toPx() + graphHeight - (stat.amount / maxY).toFloat() * graphHeight
                Offset(x, y)
            }

            val path = Path()
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val pPrev = points[i - 1]
                val pCurr = points[i]
                val controlX1 = pPrev.x + (pCurr.x - pPrev.x) / 2f
                val controlY1 = pPrev.y
                val controlX2 = pPrev.x + (pCurr.x - pPrev.x) / 2f
                val controlY2 = pCurr.y
                path.cubicTo(controlX1, controlY1, controlX2, controlY2, pCurr.x, pCurr.y)
            }

            // Fill area below trend line with gradient
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, 10.dp.toPx() + graphHeight)
                lineTo(points.first().x, 10.dp.toPx() + graphHeight)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.35f),
                        lineColor.copy(alpha = 0.0f)
                    ),
                    startY = 0f,
                    endY = 10.dp.toPx() + graphHeight
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.5.dp.toPx())
            )

            // Draw points and labels
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = point
                )

                // Draw X label (Month name)
                drawContext.canvas.nativeCanvas.drawText(
                    trendData[index].monthName,
                    point.x - 12.dp.toPx(),
                    height - 5.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = textColor
                        textSize = 11.sp.toPx()
                        typeface = android.graphics.Typeface.DEFAULT
                    }
                )
            }
        }
    }
}
