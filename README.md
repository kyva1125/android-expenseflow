<div align="center">

  <h1>💰 ExpenseFlow</h1>
  
  <p><strong>Personal Finance Tracker</strong> • Kotlin • Jetpack Compose</p>
  
  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue?logo=kotlin)](https://kotlinlang.org)
  [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.8-blue?logo=jetpack)](https://developer.android.com/jetpack/compose)
  [![Android API](https://img.shields.io/badge/Android%20API-24%2B-green)](https://developer.android.com)
  [![Material3](https://img.shields.io/badge/Material%203-1.1.1-blue)](https://m3.material.io)
  [![Hilt](https://img.shields.io/badge/Hilt-DI-blue)](https://dagger.dev/hilt/)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

  <p>A powerful expense tracking app built with <strong>Jetpack Compose</strong> featuring real-time analytics, categorization, and budget management.</p>

</div>

---

## 🎯 About

ExpenseFlow is a comprehensive personal finance management application demonstrating modern Android development with Jetpack Compose. This project showcases advanced UI patterns, real-time data visualization, custom chart components, reactive state management, and complex form handling with validation.

---

## ✨ Features

- **💸 Transaction Tracking** - Log income and expenses in real-time
- **📊 Smart Analytics** - Visual breakdown of spending patterns with charts
- **🏷️ Category Management** - Organize expenses by custom categories
- **📈 Statistics Dashboard** - Monthly/yearly spending insights
- **💡 Budget Alerts** - Set spending limits and receive notifications
- **🎨 Material3 Design** - Modern, accessible UI with Dark Mode
- **💾 Local Storage** - Room database for offline data persistence
- **⚡ Instant Updates** - State-driven UI with Jetpack Compose
- **🔄 Real-time Sync** - Reactive updates using Kotlin flows
- **📊 Data Visualization** - Custom chart components for expense analysis

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 1.9.0 |
| **UI Framework** | Jetpack Compose (No XML) |
| **Architecture** | MVVM + Repository Pattern |
| **Dependency Injection** | Hilt |
| **Database** | Room (Local SQLite) |
| **Async** | Kotlinx Coroutines + Flows |
| **State Management** | State / remember / ViewModel |
| **Data Viz** | Custom Canvas Charts |
| **Date/Time** | kotlinx.datetime |
| **Formatting** | NumberFormat/DecimalFormat |
| **Min SDK** | 24 (Android 7.0+) |
| **Target SDK** | 35 |
| **Branding** | Beniel Studio |

---

## 📱 Screens

- **🏠 Home** - Recent transactions & quick actions
- **📊 Statistics** - Monthly/annual spending analysis with charts
- **➕ Add Transaction** - Income/expense entry with form validation
- **📋 Transaction List** - Full transaction history with filters
- **🏷️ Categories** - Manage expense categories and budgets
- **⚙️ Settings** - Preferences, currency, and data management

---

## 🚀 Architecture

```
┌─────────────────────────────────────┐
│          UI Layer (Compose)          │
│  - Screens, ViewModels, Components  │
│  - Chart Components, Form Widgets   │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│      Repository Layer (Data)        │
│  - TransactionRepository            │
│  - CategoryRepository               │
│  - Room Database (Local Storage)     │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│       Domain Layer (Business)       │
│  - Entities, Use Cases, Utils       │
│  - Calculations, Aggregations       │
└─────────────────────────────────────┘
```

---

## 🎯 Jetpack Compose Expertise

### Advanced Compose Patterns

#### 1. Form Handling with Custom Validation

```kotlin
@Immutable
data class TransactionFormState(
    val amount: String = "",
    val category: String = "",
    val description: String = "",
    val date: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val amountError: String? = null,
    val categoryError: String? = null,
    val isValid: Boolean = false
)

@Composable
fun rememberTransactionFormState(): TransactionFormState {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    
    val amountError by remember(amount) {
        derivedStateOf {
            when {
                amount.isBlank() -> "Please enter an amount"
                amount.toDoubleOrNull() == null -> "Invalid amount"
                amount.toDoubleOrNull()!! <= 0 -> "Amount must be positive"
                else -> null
            }
        }
    }
    
    val categoryError by remember(category) {
        derivedStateOf {
            when {
                category.isBlank() -> "Please select a category"
                else -> null
            }
        }
    }
    
    val isValid by remember(amount, category, amountError, categoryError) {
        derivedStateOf {
            amountError == null && categoryError == null && 
            amount.isNotBlank() && category.isNotBlank()
        }
    }
    
    return remember(amount, category, description, date, amountError, categoryError, isValid) {
        TransactionFormState(
            amount = amount,
            category = category,
            description = description,
            date = date,
            amountError = amountError,
            categoryError = categoryError,
            isValid = isValid
        )
    }
}
```

#### 2. Custom Chart Component with Canvas

```kotlin
@Composable
fun ExpensePieChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val theme = MaterialTheme.colorScheme
    
    // Group transactions by category and calculate totals
    val groupedData by remember(transactions) {
        derivedStateOf {
            transactions
                .groupBy { it.category }
                .mapValues { (_, items) -> items.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
        }
    }
    
    val totalAmount by remember(groupedData) {
        derivedStateOf { groupedData.sumOf { it.second } }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 * 0.8f
        var startAngle = -90f
        
        groupedData.forEachIndexed { index, (category, amount) ->
            val sweepAngle = (amount / totalAmount * 360).toFloat()
            val color = categoryColor(index, theme)
            
            // Draw pie slice
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Fill
            )
            
            // Draw percentage label
            if (sweepAngle > 15f) {
                val labelAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val labelRadius = radius * 0.65f
                val labelX = center.x + labelRadius * cos(labelAngle).toFloat()
                val labelY = center.y + labelRadius * sin(labelAngle).toFloat()
                
                val percentage = ((amount / totalAmount) * 100).toInt()
                drawText(
                    textMeasurer = TextMeasurer(),
                    text = AnnotatedString("$percentage%"),
                    topLeft = Offset(labelX, labelY),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            startAngle += sweepAngle
        }
        
        // Draw center hole for donut chart effect
        drawCircle(
            color = theme.surface,
            radius = radius * 0.4f,
            center = center
        )
    }
}
```

#### 3. Reactive Statistics with Flows

```kotlin
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    // Combined flow for real-time statistics
    val monthlyExpenses: StateFlow<Map<Int, Double>> = transactionRepository
        .getAllTransactions()
        .map { transactions ->
            transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.date.monthNumber }
                .mapValues { (_, items) -> items.sumOf { it.amount } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    
    // Derived flow for category breakdown
    val categoryBreakdown: StateFlow<Map<String, Double>> = transactionRepository
        .getAllTransactions()
        .map { transactions ->
            transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { (_, items) -> items.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }
                .take(5)
                .toMap()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState.Loading
            
            try {
                val transactions = transactionRepository.getAllTransactions().first()
                val totalIncome = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
                val totalExpenses = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                
                _uiState.value = StatisticsUiState.Success(
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    balance = totalIncome - totalExpenses
                )
            } catch (e: Exception) {
                _uiState.value = StatisticsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

@Immutable
sealed class StatisticsUiState {
    data object Loading : StatisticsUiState()
    data class Success(
        val totalIncome: Double,
        val totalExpenses: Double,
        val balance: Double
    ) : StatisticsUiState()
    data class Error(val message: String) : StatisticsUiState()
}
```

#### 4. Animated Transactions List

```kotlin
@Composable
fun AnimatedTransactionList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit,
    onTransactionDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = transactions,
            key = { it.id }  // Critical for stable item tracking
        ) { transaction ->
            var dismissed by remember { mutableStateOf(false) }
            
            AnimatedVisibility(
                visible = !dismissed,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeOut()
            ) {
                SwipeToDismiss(
                    state = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                dismissed = true
                                onTransactionDelete(transaction)
                                true
                            } else {
                                false
                            }
                        },
                        positionalThreshold = { 150.dp.toPx() }
                    ),
                    background = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = if (transaction.type == TransactionType.INCOME) 
                                        Color(0xFF4CAF50) 
                                    else 
                                        Color(0xFFF44336)
                                )
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    dismissContent = {
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction) }
                        )
                    }
                )
            }
        }
    }
}
```

#### 5. Currency Formatting with CompositionLocal

```kotlin
data class CurrencyConfig(
    val locale: Locale,
    val symbol: String,
    val decimalDigits: Int = 2
)

val LocalCurrencyConfig = compositionLocalOf { 
    CurrencyConfig(Locale.US, "$", 2) 
}

@Composable
fun ExpenseFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    currencyLocale: Locale = Locale.US,
    currencySymbol: String = "$",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF2196F3)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF2196F3)
        )
    }
    
    val currencyConfig = CurrencyConfig(currencyLocale, currencySymbol)
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = {
            CompositionLocalProvider(
                LocalCurrencyConfig provides currencyConfig
            ) {
                content()
            }
        }
    )
}

@Composable
fun CurrencyText(
    amount: Double,
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    modifier: Modifier = Modifier
) {
    val currencyConfig = LocalCurrencyConfig.current
    
    val formattedAmount by remember(amount, currencyConfig) {
        derivedStateOf {
            NumberFormat.getCurrencyInstance(currencyConfig.locale).apply {
                maximumFractionDigits = currencyConfig.decimalDigits
                minimumFractionDigits = currencyConfig.decimalDigits
                currency = java.util.Currency.getInstance(currencyConfig.locale.country)
            }.format(amount)
        }
    }
    
    Text(
        text = formattedAmount,
        style = style,
        modifier = modifier
    )
}
```

#### 6. Category Filter with State Hoisting

```kotlin
@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") },
                leadingIcon = if (selectedCategory == null) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else null
            )
        }
        
        items(categories) { category ->
            val isSelected = selectedCategory == category
            
            FilterChip(
                selected = isSelected,
                onClick = { 
                    onCategorySelected(if (isSelected) null else category)
                },
                label = { Text(category) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else null
            )
        }
    }
}

@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column {
        // Category filter - state hoisted to ViewModel
        CategoryFilter(
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = viewModel::onCategorySelected
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Transaction list - filtered based on selected category
        LazyColumn {
            items(
                items = uiState.filteredTransactions,
                key = { it.id }
            ) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}
```

#### 7. Date Range Picker with rememberSaveable

```kotlin
@Composable
fun DateRangePicker(
    onRangeSelected: (Pair<LocalDate, LocalDate>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // State that survives configuration changes
    var startDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var endDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    
    val calendarState = rememberCalendarState()
    val selectionState = rememberDateRangePickerState()
    
    val formattedRange by remember(startDate, endDate) {
        derivedStateOf {
            when {
                startDate != null && endDate != null -> {
                    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    "${startDate.format(formatter)} - ${endDate.format(formatter)}"
                }
                startDate != null -> {
                    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    "${startDate.format(formatter)} - End"
                }
                else -> "Select Date Range"
            }
        }
    }
    
    OutlinedButton(
        onClick = {
            selectionState.setSelection(startDate, endDate)
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = formattedRange)
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Select date range"
        )
    }
    
    LaunchedEffect(selectionState.selectedStartDateMillis, selectionState.selectedEndDateMillis) {
        val newStartDate = selectionState.selectedStartDateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
        }
        val newEndDate = selectionState.selectedEndDateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
        }
        
        if (newStartDate != startDate || newEndDate != endDate) {
            startDate = newStartDate
            endDate = newEndDate
            
            if (startDate != null && endDate != null) {
                onRangeSelected(Pair(startDate, endDate))
            }
        }
    }
}
```

#### 8. Budget Progress Indicator with Animated Values

```kotlin
@Composable
fun BudgetProgress(
    currentAmount: Double,
    budgetAmount: Double,
    category: String,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (currentAmount / budgetAmount).coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "budgetProgress"
    )
    
    val isOverBudget by remember(currentAmount, budgetAmount) {
        derivedStateOf { currentAmount > budgetAmount }
    }
    
    val progressColor by animateColorAsState(
        targetValue = when {
            isOverBudget -> Color.Red
            progress > 0.8f -> Color(0xFFFF9800)
            else -> Color(0xFF4CAF50)
        },
        animationSpec = tween(durationMillis = 300),
        label = "progressColor"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "$${String.format("%.2f", currentAmount)} / $${String.format("%.2f", budgetAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOverBudget) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f)
            )
            
            if (isOverBudget) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Over budget by $${String.format("%.2f", currentAmount - budgetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    }
}
```

### State Management Best Practices

#### ViewModel with StateFlow

```kotlin
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()
    
    // Combined state for filtered transactions
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        repository.getAllTransactions(),
        filterState
    ) { transactions, filter ->
        transactions.filter { transaction ->
            val matchesCategory = filter.selectedCategory == null || 
                transaction.category == filter.selectedCategory
            val matchesType = filter.selectedType == null || 
                transaction.type == filter.selectedType
            val matchesDateRange = when {
                filter.startDate != null && filter.endDate != null -> 
                    transaction.date in filter.startDate..filter.endDate
                else -> true
            }
            matchesCategory && matchesType && matchesDateRange
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }
    
    fun updateFilter(filter: FilterState) {
        _filterState.value = filter
    }
}

@Immutable
data class TransactionUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@Immutable
data class FilterState(
    val selectedCategory: String? = null,
    val selectedType: TransactionType? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)
```

### Performance Optimization Techniques

#### Stable Data Classes

```kotlin
@Immutable
data class Transaction(
    val id: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: LocalDate,
    val type: TransactionType
) {
    // Stable equals/hashCode for Compose optimization
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Transaction
        return id == other.id &&
               amount == other.amount &&
               category == other.category &&
               description == other.description &&
               date == other.date &&
               type == other.type
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

@Stable
enum class TransactionType {
    INCOME, EXPENSE
}
```

#### Efficient List Rendering

```kotlin
@Composable
fun OptimizedTransactionList(
    transactions: List<Transaction>,
    onItemClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = transactions,
            key = { it.id }  // Critical for performance
        ) { transaction ->
            // Each item only recomposes when its own transaction changes
            TransactionListItem(
                transaction = transaction,
                onClick = onItemClick
            )
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: (Transaction) -> Unit
) {
    // This composable is optimized - only recomposes when transaction changes
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick(transaction) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            CurrencyText(
                amount = transaction.amount,
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.type == TransactionType.INCOME) 
                    Color(0xFF4CAF50) 
                else 
                    Color(0xFFF44336)
            )
        }
    }
}
```

### Recomposition Strategies

#### Minimizing Recomposition Scope

```kotlin
@Composable
fun ExpenseForm(
    onSubmit: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    // Form validation state
    val isValid by remember(amount, category) {
        derivedStateOf {
            amount.isNotBlank() && 
            category.isNotBlank() && 
            amount.toDoubleOrNull() != null && 
            amount.toDoubleOrNull()!! > 0
        }
    }
    
    Column(modifier = Modifier.padding(16.dp)) {
        // Amount field - only recomposes when amount changes
        AmountField(
            value = amount,
            onValueChange = { amount = it }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category field - only recomposes when category changes
        CategoryField(
            value = category,
            onValueChange = { category = it }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Description field - only recomposes when description changes
        DescriptionField(
            value = description,
            onValueChange = { description = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Submit button - only recomposes when isValid changes
        Button(
            onClick = {
                onSubmit(
                    Transaction(
                        id = UUID.randomUUID().toString(),
                        amount = amount.toDouble(),
                        category = category,
                        description = description,
                        date = LocalDate.now(),
                        type = TransactionType.EXPENSE
                    )
                )
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Transaction")
        }
    }
}

// Separate composables for better recomposition control
@Composable
fun AmountField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Amount") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        isError = value.isNotBlank() && (value.toDoubleOrNull() == null || value.toDoubleOrNull()!! <= 0),
        supportingText = {
            if (value.isNotBlank() && value.toDoubleOrNull() == null) {
                Text("Please enter a valid amount")
            }
        }
    )
}
```

### Side Effects Handling

#### LaunchedEffect for One-time Events

```kotlin
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val transaction by viewModel.transaction.collectAsState()
    
    // Load transaction when transactionId changes
    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }
    
    // Track screen view
    LaunchedEffect(Unit) {
        viewModel.trackScreenView("TransactionDetail")
    }
    
    when {
        transaction.isLoading -> CircularProgressIndicator()
        transaction.error != null -> ErrorMessage(message = transaction.error)
        transaction.data != null -> TransactionDetailView(transaction = transaction.data!!)
    }
}
```

#### DisposableEffect for Resource Management

```kotlin
@Composable
fun RealTimeExpenseUpdates(
    onNewExpense: (Transaction) -> Unit
) {
    val context = LocalContext.current
    
    DisposableEffect(Unit) {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Resume real-time updates
            }
            
            override fun onLost(network: Network) {
                // Pause real-time updates
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or later
- JDK 17
- Android SDK 24+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/kyva1125/android-expenseflow.git
cd android-expenseflow
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run on emulator or physical device

### Build

```bash
./gradlew assembleDebug
```

### Run Tests

```bash
./gradlew test
./gradlew connectedAndroidTest
```

---

## 📖 Key Compose Concepts Used

- **Declarative UI** - UI is a function of state
- **Composition** - Describe the UI once, Compose handles updates
- **Recomposition** - Smart recomposition only updates what changed
- **State Hoisting** - State managed at the lowest common parent
- **Side Effects** - Controlled execution of non-compose code
- **Immutable Data** - State objects are immutable for thread safety
- **Stability** - Compose compiler optimizations for performance
- **Custom Canvas** - Drawing custom charts and visualizations
- **Animation API** - Smooth transitions and animations

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author

**Nick Ledesma** - Jetpack Compose Expert

- GitHub: [@kyva1125](https://github.com/kyva1125)

---

## 🌟 Showcasing Advanced Compose Expertise

This project demonstrates deep knowledge of Jetpack Compose including:
- Complex form handling with validation
- Custom chart components with Canvas API
- Real-time data visualization
- Reactive state management with StateFlow
- Advanced animation techniques
- Swipe-to-delete gestures
- Date range pickers
- Currency formatting with CompositionLocal
- Performance optimization with stable types
- Material3 design system integration
- Modern Android architecture (MVVM, Clean Architecture)
- Reactive programming with Kotlin Flow
- Dependency injection with Hilt
- Offline-first data persistence with Room

Built with ❤️ using Jetpack Compose