<div align="center">

  <h1>💰 ExpenseFlow</h1>
  
  <p><strong>Personal Finance Tracker</strong> • Kotlin • Jetpack Compose</p>
  
  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue?logo=kotlin)](https://kotlinlang.org)
  [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.8-blue?logo=jetpack)](https://developer.android.com/jetpack/compose)
  [![Android API](https://img.shields.io/badge/Android%20API-24%2B-green)](https://developer.android.com)
  [![Material3](https://img.shields.io/badge/Material%203-1.1.1-blue)](https://m3.material.io)
  [![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

  <p>A powerful expense tracking app built with <strong>Jetpack Compose</strong> featuring real-time analytics, categorization, and budget management.</p>

</div>

## ✨ Features

- **💸 Transaction Tracking** - Log income and expenses in real-time
- **📊 Smart Analytics** - Visual breakdown of spending patterns
- **🏷️ Category Management** - Organize expenses by custom categories
- **📈 Statistics Dashboard** - Monthly/yearly spending insights
- **🎨 Material3 Design** - Modern, accessible UI with Dark Mode
- **💾 Local Storage** - Room database for offline data persistence
- **⚡ Instant Updates** - State-driven UI with Jetpack Compose

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 1.9.0 |
| **UI Framework** | Jetpack Compose (No XML) |
| **Architecture** | MVVM + Repository Pattern |
| **Dependency Injection** | Hilt |
| **Database** | Room (Local SQLite) |
| **Coroutines** | Kotlinx Coroutines |
| **Min SDK** | 24 (Android 7.0+) |
| **Target SDK** | 35 |
| **Branding** | Beniel Studio |

## 📱 Screens

- **🏠 Home** - Recent transactions & quick actions
- **📊 Statistics** - Monthly/annual spending analysis
- **➕ Add Transaction** - Income/expense entry
- **📋 Transaction List** - Full transaction history
- **⚙️ Settings** - Categories & preferences

## 🚀 Architecture

```
┌─────────────────────────────────────┐
│          UI Layer (Compose)          │
│  - Screens, ViewModels, Components  │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│      Repository Layer (Data)        │
│  - TransactionRepository            │
│  - SettingsRepository               │
│  - Room Database (Local Storage)     │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│       Domain Layer (Business)       │
│  - Entities, Use Cases, Utils       │
└─────────────────────────────────────┘
```

## 📦 Installation

```bash
git clone https://github.com/kyva1125/android-expenseflow.git
cd android-expenseflow
./gradlew assembleDebug
```

## 🔑 Environment Variables

No external API keys required - fully offline capable.

## 📸 Screenshots

> **Coming Soon** - Screenshots demonstrating analytics dashboard and transaction flows

## 🧪 Testing

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## 📄 License

MIT License - see [LICENSE](LICENSE) for details

## 👤 Author

**Nick Ledesma** - [GitHub](https://github.com/kyva1125)

---

<div align="center">

**Built with ❤️ using Kotlin & Jetpack Compose**

</div>