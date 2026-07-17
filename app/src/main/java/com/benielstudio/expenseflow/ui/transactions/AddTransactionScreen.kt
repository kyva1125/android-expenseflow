package com.benielstudio.expenseflow.ui.transactions

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.benielstudio.expenseflow.data.CategoryEntity
import com.benielstudio.expenseflow.data.ExpenseType
import com.benielstudio.expenseflow.ui.theme.ExpenseColor
import com.benielstudio.expenseflow.ui.theme.IncomeColor
import com.benielstudio.expenseflow.utils.IconUtils
import com.benielstudio.expenseflow.utils.formatDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val currency by viewModel.currency.collectAsState()

    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ExpenseType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    
    val calendar = remember { Calendar.getInstance() }
    var dateInMillis by remember { mutableStateOf(calendar.timeInMillis) }
    var dateString by remember { mutableStateOf(calendar.timeInMillis.formatDate("MMM dd, yyyy")) }

    // Pre-select first category if available
    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && selectedCategory == null) {
            selectedCategory = categories.first()
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            dateInMillis = calendar.timeInMillis
            dateString = calendar.timeInMillis.formatDate("MMM dd, yyyy")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type Segmented Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Expense Toggle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedType == ExpenseType.EXPENSE) ExpenseColor else Color.Transparent)
                        .clickable { selectedType = ExpenseType.EXPENSE },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Expense",
                        color = if (selectedType == ExpenseType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Income Toggle
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedType == ExpenseType.INCOME) IncomeColor else Color.Transparent)
                        .clickable { selectedType = ExpenseType.INCOME },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Income",
                        color = if (selectedType == ExpenseType.INCOME) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Big Amount Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currency,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == ExpenseType.INCOME) IncomeColor else ExpenseColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        TextField(
                            value = amount,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.toDoubleOrNull() != null || input == ".") {
                                    amount = input
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedType == ExpenseType.INCOME) IncomeColor else ExpenseColor,
                                textAlign = TextAlign.Center
                            ),
                            placeholder = {
                                Text(
                                    "0.00",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }

            // Form Inputs (Title, Note, Date)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("e.g., Grocery Shopping") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                placeholder = { Text("Add more details...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Date Picker Row Trigger
            OutlinedTextField(
                value = dateString,
                onValueChange = {},
                label = { Text("Date") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Choose Date",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp)
            )

            // Category Picker
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory?.id == category.id
                        val color = IconUtils.getColorFromHex(category.colorHex)
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) color.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) color else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = IconUtils.getIconByName(category.icon),
                                    contentDescription = category.name,
                                    tint = color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = category.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    val finalAmount = amount.toDoubleOrNull() ?: 0.0
                    val finalCategory = selectedCategory?.name ?: "Others"
                    if (title.isNotEmpty() && finalAmount > 0.0) {
                        viewModel.addExpense(
                            title = title,
                            amount = finalAmount,
                            category = finalCategory,
                            type = selectedType,
                            date = dateInMillis,
                            note = note
                        )
                        onNavigateBack()
                    }
                },
                enabled = title.isNotEmpty() && (amount.toDoubleOrNull() ?: 0.0) > 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Transaction", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

