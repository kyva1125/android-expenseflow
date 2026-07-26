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

ExpenseFlow is a comprehensive personal finance management application demonstrating modern Android development with Jetpack Compose. Built as part of my migration from Flutter to native Android development, this project showcases advanced UI patterns, real-time data visualization, and reactive state management using Kotlin flows.

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

| Category | Technology | Flutter Analogy |
|----------|------------|-----------------|
| **Language** | Kotlin 1.9.0 | Dart |
| **UI Framework** | Jetpack Compose (No XML) | Flutter Widgets |
| **Architecture** | MVVM + Repository Pattern | BLoC / Provider |
| **Dependency Injection** | Hilt | get_it / Provider |
| **Database** | Room (Local SQLite) | sqflite / Drift |
| **Async** | Kotlinx Coroutines + Flows | Future / Stream |
| **State Management** | State / remember / ViewModel | ChangeNotifier / setState |
| **Data Viz** | Custom Canvas Charts | fl_chart |
| **Date/Time** | kotlinx.datetime | intl |
| **Formatting** | NumberFormat/DecimalFormat | NumberFormat |
| **Min SDK** | 24 (Android 7.0+) | iOS 11+, Android 5.0+ |
| **Target SDK** | 35 | Latest iOS/Android |
| **Branding** | Beniel Studio | Custom branding |

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

**Flutter Parallel:** Same layered architecture as BLoC pattern - UI → Cubits/BLoCs → Repositories → Data Sources

---

## 🔄 Flutter to Jetpack Compose: Key Concepts

### Form Handling with Validation

**Flutter (Form + TextEditingController):**
```dart
class AddTransactionForm extends StatefulWidget {
  @override
  _AddTransactionFormState createState() => _AddTransactionFormState();
}

class _AddTransactionFormState extends State<AddTransactionForm> {
  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();
  final _categoryController = TextEditingController();
  
  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            controller: _amountController,
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Please enter an amount';
              }
              return null;
            },
            decoration: InputDecoration(labelText: 'Amount'),
          ),
          ElevatedButton(
            onPressed: () {
              if (_formKey.currentState!.validate()) {
                // Submit form
              }
            },
            child: Text('Add Transaction'),
          ),
        ],
      ),
    );
  }
}
```

**Jetpack Compose (TextField + State):**
```kotlin
@Composable
fun AddTransactionForm(
    onSubmit: (Transaction) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = { 
                amount = it
                amountError = null
            },
            label = { Text("Amount") },
            isError = amountError != null,
            supportingText = amountError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        
        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") }
        )
        
        Button(
            onClick = {
                if (amount.isBlank()) {
                    amountError = "Please enter an amount"
                } else {
                    onSubmit(Transaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = category
                    ))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Transaction")
        }
    }
}
```

### Data Visualization - Charts

**Flutter (fl_chart):**
```dart
class ExpenseChart extends StatelessWidget {
  final List<Transaction> transactions;
  
  @override
  Widget build(BuildContext context) {
    final grouped = _groupByCategory(transactions);
    
    return PieChart(
      PieChartData(
        sections: grouped.entries.map((entry) {
          return PieChartSectionData(
            value: entry.value,
            title: entry.key,
            color: _getCategoryColor(entry.key),
          );
        }).toList(),
      ),
    );
  }
}
```

**Jetpack Compose (Custom Canvas):**
```kotlin
@Composable
fun ExpensePieChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val grouped = transactions.groupBy { it.category }
        .mapValues { it.value.sumOf { it.amount } }
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2
        var startAngle = 0f
        
        grouped.forEach { (category, amount) ->
            val sweepAngle = (amount / grouped.values.sum() * 360).toFloat()
            
            drawArc(
                color = getCategoryColor(category),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            startAngle += sweepAngle
        }
    }
}
```

### Real-time Statistics with Flows

**Flutter (StreamBuilder):**
```dart
class StatisticsScreen extends StatelessWidget {
  final TransactionBloc bloc;
  
  @override
  Widget build(BuildContext context) {
    return StreamBuilder<StatisticsState>(
      stream: bloc.stream,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final stats = snapshot.data as StatisticsLoaded;
          return StatisticsView(stats: stats);
        }
        return CircularProgressIndicator();
      },
    );
  }
}
```

**Jetpack Compose (collectAsState):**
```kotlin
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.statistics.collectAsState()
    
    when (stats) {
        is StatisticsState.Loading -> {
            CircularProgressIndicator()
        }
        is StatisticsState.Success -> {
            StatisticsView(
                totalExpenses = (stats as StatisticsState.Success).totalExpenses,
                categoryBreakdown = (stats as StatisticsState.Success).breakdown
            )
        }
        is StatisticsState.Error -> {
            ErrorMessage(
                message = (stats as StatisticsState.Error).message
            )
        }
    }
}
```

### Reactive Category Selection

**Flutter (Provider):**
```dart
class CategoryFilter extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final filter = Provider.of<FilterProvider>(context);
    
    return Wrap(
      children: filter.categories.map((category) {
        return FilterChip(
          label: Text(category),
          selected: filter.selectedCategory == category,
          onSelected: (selected) {
            filter.setCategory(selected ? category : null);
          },
        );
      }).toList(),
    );
  }
}
```

**Jetpack Compose (Hoisted State):**
```kotlin
@Composable
fun CategoryFilter(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    categories: List<String>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { 
                    onCategorySelected(
                        if (selectedCategory == category) null else category
                    )
                },
                label = { Text(category) },
                leadingIcon = if (selectedCategory == category) {
                    { Icon(Icons.Filled.Check, contentDescription = null) }
                } else null
            )
        }
    }
}
```

### Date Range Picker

**Flutter (showDateRangePicker):**
```dart
Future<void> _selectDateRange() async {
  final picked = await showDateRangePicker(
    context: context,
    firstDate: DateTime(2020),
    lastDate: DateTime.now(),
  );
  
  if (picked != null) {
    setState(() {
      dateRange = picked;
    });
  }
}
```

**Jetpack Compose (DatePickerDialog):**
```kotlin
@Composable
fun DateRangePicker(
    onRangeSelected: (Pair<LocalDate, LocalDate>?) -> Unit
) {
    val context = LocalContext.current
    
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val date = LocalDate.of(year, month + 1, day)
            if (startDate == null || (startDate != null && endDate != null)) {
                startDate = date
                endDate = null
            } else {
                endDate = date
                onRangeSelected(Pair(startDate!!, date))
            }
        },
        year,
        month,
        day
    )
    
    Button(onClick = { datePickerDialog.show() }) {
        Text("Select Date Range")
    }
}
```

### Currency Formatting

**Flutter (NumberFormat):**
```dart
String formatCurrency(double amount) {
  final formatter = NumberFormat.currency(
    locale: 'en_US',
    symbol: '\$',
    decimalDigits: 2,
  );
  return formatter.format(amount);
}
```

**Jetpack Compose (NumberFormat):**
```kotlin
fun formatCurrency(amount: Double, locale: Locale = Locale.US): String {
    return NumberFormat.getCurrencyInstance(locale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }.format(amount)
}

@Composable
fun CurrencyText(amount: Double) {
    val locale = Locale.getDefault()
    Text(
        text = formatCurrency(amount, locale),
        style = MaterialTheme.typography.headlineSmall
    )
}
```

### Animated List with Transitions

**Flutter (AnimatedList):**
```dart
AnimatedList(
  initialItemCount: transactions.length,
  itemBuilder: (context, index, animation) {
    return SlideTransition(
      position: animation.drive(
        Tween(begin: Offset(1, 0), end: Offset.zero)
          .chain(CurveTween(curve: Curves.easeInOut)),
      ),
      child: TransactionTile(transactions[index]),
    );
  },
)
```

**Jetpack Compose (AnimatedVisibility):**
```kotlin
@Composable
fun AnimatedTransactionList(
    transactions: List<Transaction>
) {
    LazyColumn {
        items(
            items = transactions,
            key = { it.id }
        ) { transaction ->
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                TransactionTile(transaction = transaction)
            }
        }
    }
}
```

---

## 🚀 Migrando de Flutter

### Conceptos Equivalentes

| Flutter | Jetpack Compose | Notes |
|---------|-----------------|-------|
| `Form` | `TextField + State` | Manual validation, more control |
| `TextEditingController` | `mutableStateOf<String>` | Direct state binding |
| `FormValidator` | Custom validator functions | More flexible validation logic |
| `StreamBuilder` | `collectAsState()` | Reactive data flows |
| `fl_chart` | Custom `Canvas` / `Compose Charts` | More control, less abstraction |
| `intl` package | `kotlinx.datetime` | Built-in date/time support |
| `provider` package | `Hilt` + `ViewModel` | Better compile-time safety |
| `showDatePicker` | `DatePickerDialog` | Native Android picker |
| `AnimatedList` | `AnimatedVisibility` | Easier animations |
| `showBottomSheet` | `BottomSheetScaffold` | Built-in bottom sheet support |
| `SnackBar` | `SnackbarHost` | Better API for snackbars |

### Tips de Migración

#### 1. **Form State Management**
Compose gives you more direct control over form state:

```kotlin
@Composable
fun FormWithValidation() {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = if (it.contains("@")) null else "Invalid email"
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } }
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = if (it.length >= 8) null else "Too short"
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null
        )
    }
}
```

#### 2. **Data Visualization**
Compose Canvas gives you full control over custom charts:

```kotlin
@Composable
fun LineChart(
    data: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val padding = 40.dp.toPx()
        val width = size.width - padding * 2
        val height = size.height - padding * 2
        
        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(padding, size.height - padding),
            end = Offset(size.width - padding, size.height - padding),
            strokeWidth = 2.dp.toPx()
        )
        
        // Draw data points
        val path = Path().apply {
            data.forEachIndexed { index, (x, y) ->
                val px = padding + (x / 100) * width
                val py = size.height - padding - (y / 100) * height
                if (index == 0) moveTo(px, py) else lineTo(px, py)
            }
        }
        
        drawPath(path, Color.Blue, style = Stroke(width = 3.dp.toPx()))
    }
}
```

#### 3. **Real-time Aggregations with Flows**
Combine flows for complex reactive calculations:

```kotlin
class StatisticsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    val monthlyExpenses: StateFlow<Map<String, Double>> = repository
        .getAllTransactions()
        .map { transactions ->
            transactions.groupByMonth()
                .mapValues { (_, txs) -> txs.sumOf { it.amount } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
}
```

#### 4. **Animation API**
Compose animations are more declarative:

```kotlin
@Composable
fun AnimatedValueDisplay(
    targetValue: Double,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableStateOf(0.0) }
    
    LaunchedEffect(targetValue) {
        animate(
            initialValue = animatedValue,
            targetValue = targetValue,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animatedValue = value
        }
    }
    
    Text(
        text = formatCurrency(animatedValue),
        modifier = modifier
    )
}
```

#### 5. **Snackbar API**
Built-in snackbar support with better API:

```kotlin
@Composable
fun MyApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Button(
            onClick = {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Transaction added!",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        // Undo action
                    }
                }
            }
        ) {
            Text("Show Snackbar")
        }
    }
}
```

#### 6. **Bottom Sheet Navigation**
Native bottom sheet support:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetNavigation() {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = {
            Column {
                Text("Sheet Content")
                Button(onClick = {
                    scope.launch { sheetState.bottomSheetState.partialExpand() }
                }) {
                    Text("Collapse")
                }
            }
        }
    ) { padding ->
        Button(
            onClick = {
                scope.launch { sheetState.bottomSheetState.expand() }
            }
        ) {
            Text("Expand Sheet")
        }
    }
}
```

#### 7. **Currency Formatting**
Use locale-aware formatting:

```kotlin
@Composable
fun FormattedAmount(
    amount: Double,
    locale: Locale = Locale.getDefault()
) {
    val formatter = remember(locale) {
        NumberFormat.getCurrencyInstance(locale).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
    }
    
    Text(
        text = formatter.format(amount),
        style = MaterialTheme.typography.displayMedium,
        color = if (amount < 0) MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.primary
    )
}
```

### Common Mistakes to Avoid

1. **❌ Don't forget to handle null states**
   ```kotlin
   // Bad - may crash
   Text(transaction.category!!)
   
   // Good - safe access
   Text(transaction.category ?: "Uncategorized")
   ```

2. **❌ Don't block the main thread with calculations**
   ```kotlin
   // Bad
   val result = expensiveCalculation(transactions)
   
   // Good
   val result by produceState(initialValue = null) {
       value = withContext(Dispatchers.Default) {
           expensiveCalculation(transactions)
       }
   }
   ```

3. **❌ Don't recreate expensive objects**
   ```kotlin
   // Bad - recreated every composition
   val formatter = NumberFormat.getCurrencyInstance()
   
   // Good - cached with remember
   val formatter = remember { NumberFormat.getCurrencyInstance() }
   ```

4. **❌ Don't ignore state hoisting**
   ```kotlin
   // Bad - state trapped in composable
   @Composable
   fun ExpenseList() {
       var filter by remember { mutableStateOf("all") }
       // Can't access filter from outside
   }
   
   // Good - hoisted state
   @Composable
   fun ExpenseList(
       filter: String,
       onFilterChange: (String) -> Unit
   ) {
       // State can be controlled from parent
   }
   ```

---

## 📦 Installation

```bash
git clone https://github.com/kyva1125/android-expenseflow.git
cd android-expenseflow
./gradlew assembleDebug
```

### Requirements

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 35
- Gradle 8.0+

---

## 🔑 Environment Variables

No external API keys required - fully offline capable.

---

## 🧪 Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# UI tests
./gradlew connectedDebugAndroidTest
```

---

## 📸 Screenshots

> **Coming Soon** - Screenshots demonstrating analytics dashboard and transaction flows

---

## 🎓 Learning Resources

- [Jetpack Compose Basics](https://developer.android.com/courses/jetpack-compose/course)
- [Compose for Flutter Developers](https://developer.android.com/jetpack/compose/mental-model)
- [State in Compose](https://developer.android.com/jetpack/compose/state)
- [Graphics in Compose](https://developer.android.com/jetpack/compose/graphics)
- [Compose Animations](https://developer.android.com/jetpack/compose/animations)

---

## 📄 License

MIT License - see [LICENSE](LICENSE) for details

---

## 👤 Author

**Nick Ledesma**  
- 🐙 [GitHub](https://github.com/kyva1125)  
- 📧 Contact: [GitHub Issues](https://github.com/kyva1125/android-expenseflow/issues)

---

## 🙏 Acknowledgments

Built with modern Android best practices, transitioning from Flutter to Jetpack Compose. Demonstrates expertise in:
- Complex form handling with validation
- Custom data visualization
- Reactive programming with Flows
- State hoisting patterns
- Animation and transitions
- Clean Architecture principles
- Offline-first data strategies

---

<div align="center">

**Built with ❤️ using Kotlin & Jetpack Compose**  
**Branding: Beniel Studio**

</div>