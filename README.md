# 🕌 Islamic App - Comprehensive Muslim Companion & Qibla Finder

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android Gradle Plugin](https://img.shields.io/badge/AGP-9.5.1-blue.svg?style=flat&logo=android)](https://developer.android.com/studio/releases/gradle-plugin)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10.00-green.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Expressive-orange.svg?style=flat&logo=materialdesign)](https://m3.material.io/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM%20%2B%20UDF-brightgreen.svg?style=flat)](#-architecture--design-patterns)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

**Islamic App** (Noor App) is an all-in-one native Android companion for Muslims built with **Kotlin**, **Jetpack Compose (Material 3)**, and modern **Clean Architecture**. The app provides a spiritually enriching experience featuring a full Holy Quran reader with audio recitations, authentic Hadith collections, precise Prayer Timings with custom Adhan alarms, real-time Qibla compass, Digital Tasbih, Hijri Calendar, Salah Progress Tracker, 99 Names of Allah, and a Zakat Calculator.

---

## 🌟 Overview & Philosophy

1. **Spiritual Serenity through Design**: Clean, modern Material 3 design system featuring custom themes, Arabic script rendering using the traditional *Amiri Quran* typeface, Tajweed guides, and rosetted Ayah counters.
2. **Comprehensive Daily Utility**: All essentials for a Muslim's daily worship—from accurate location-based prayer timing calculations and exact alarm notifications to a daily/weekly Salah tracker and digital Dhikr tools.
3. **Modern Android Architecture**: Strict separation of concerns following Clean Architecture guidelines (Data, Domain, Presentation), Unidirectional Data Flow (UDF), Kotlin Coroutines, Room Database, Retrofit APIs, and Dagger Hilt dependency injection.

---

## ✨ Core Features & Capabilities

### 📖 1. Holy Quran (القرآن الكريم)
- **Surah & Juz Browsing**: Flexible navigation by Surah (114 chapters) or Juz (30 parts).
- **Sacred Typography & Script**: Custom rendering with Tajweed Waqf marks using the `Amiri Quran` font.
- **Tafseer & Ayah Details**: Instant access to Ayah Tafseer, translation, and transliteration settings.
- **Audio Recitations**: Reciter selection and offline audio download management with playback preview capabilities.
- **Bookmarks & Customization**: Bookmark favorite Ayahs, toggle text displays, adjust font sizes, and view Tajweed guides.

### 📚 2. Authentic Hadith Collections (الحديث النبوي)
- **Book Collections**: Explore major authentic Hadith collections.
- **Structured Navigation**: Chapter-by-chapter browsing with quick search capabilities across Hadiths and narrators.

### 📿 3. Ibadat, Duas & Digital Tasbih (الأذكار والتسبيح)
- **Digital Tasbih Counter**: Interactive counter with haptic feedback, custom bead graphics, sound effects, reset options, and target setting.
- **Dhikr Presets**: Quick select common remembrances (*SubhanAllah*, *Alhamdulillah*, *Allahu Akbar*, etc.).
- **Fortress of the Muslim (Duas)**: Categorized daily supplications with search, copy, share, and custom color palettes.

### 🕋 4. Qibla Finder & Sensor Compass
- **Real-Time Compass**: Real-time sensor integration (`CompassSensorManager`) with visual calibration guidance and directional accuracy for finding the Qibla.
- **Location Awareness**: GPS location detection via Google Play Services Location for accurate bearings.

### ⏰ 5. Prayer Timings & Exact Adhan Alarms
- **Location-Based Schedule**: Automatic prayer calculation supporting multiple calculation methods and juristic settings (e.g., Asr juristic method).
- **Alarm Management**: Custom notification alarms using exact alarm scheduling (`SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`) and `PrayerAlarmReceiver` for timely Adhan alerts.

### 📅 6. Hijri Calendar & Salah Tracker
- **Islamic Calendar**: Interactive Hijri and Gregorian calendar viewer with day-by-day prayer timing lookup and Hijri date offset adjustments.
- **Salah Tracker**: Track daily and weekly prayer completion with interactive checklists and visual progress cards.

### 🧮 7. Additional Islamic Utilities
- **99 Names of Allah (Asma ul-Husna)**: Detailed view of the divine names and meanings.
- **Zakat Calculator**: Instant wealth calculation tool based on current Nisab rates.

---

## 📐 Architecture & Design Patterns

The project enforces **Clean Architecture**, **MVVM**, and **Unidirectional Data Flow (UDF)**:

```
com.example.noorapp/
├── App.kt                          # Application entry point (@HiltAndroidApp)
├── core/                           # Core utilities, theme, and design system
│   ├── common/                     # CompassSensorManager, LocationHelper, PrayerSettingsManager, etc.
│   ├── designsystem/               # Reusable Compose design primitives (AppText, AppButton, AppCard, etc.)
│   ├── theme/                      # Colors, Typography (Amiri Quran), Dimensions (Sdp/Ssp), Theme
│   └── utils/                      # Helper utilities, DateTimeUtils, PrayerTimeCalculator, HijriCalendarCalculator
├── data/                           # Data Layer (Local & Remote Data Sources)
│   ├── local/                      # Room Database (IslamicAppDatabase), DAOs, Entities, Database Helpers
│   ├── remote/                     # Retrofit Services (Quran, Hadith, Dua, Prayer APIs) & DTOs
│   └── repository/                 # Repository implementation classes (QuranRepositoryImpl, etc.)
├── di/                             # Dagger Hilt Modules
│   ├── DatabaseModule.kt           # Room Database & DAO bindings
│   ├── LocationModule.kt           # Fused Location Provider bindings
│   ├── NetworkModule.kt            # Retrofit & OkHttp client bindings
│   ├── RepositoryModule.kt         # Repository interface-to-implementation bindings
│   └── UseCaseModule.kt            # Domain Use Case bindings
├── domain/                         # Domain Layer (Business Logic)
│   ├── model/                      # Domain Data Models (Ayah, PrayerSchedule, SalahRecord, etc.)
│   ├── repository/                 # Repository Interfaces
│   └── usecase/                    # Single-responsibility Use Cases (quran, hadith, dua, qibla, salah, etc.)
├── service/                        # Background Services & Receivers
│   ├── AlarmScheduler.kt           # Exact Alarm scheduling logic
│   └── PrayerAlarmReceiver.kt      # BroadcastReceiver for trigger-time Adhan notifications
└── ui/                             # Presentation Layer (Jetpack Compose + MVVM)
    ├── MainActivity.kt             # Main Entry Activity
    ├── alarm/                      # Prayer Alarm settings screen & ViewModel
    ├── calendar/                   # Hijri Calendar screen & ViewModel
    ├── compass/                    # Qibla Compass screen & ViewModel
    ├── hadith/                     # Hadith collection screens & ViewModel
    ├── home/                       # Home Dashboard screen & ViewModel
    ├── ibadat/                     # Duas & Supplications screens & ViewModel
    ├── more/                       # App settings, Names of Allah, Zakat calculator & ViewModel
    ├── navigation/                 # AppNavigation graph & Screen routes
    ├── prayers/                    # Prayer Timings screen & ViewModel
    ├── quran/                      # Full Quran reader, Surah detail, Tafseer & ViewModel
    ├── salahtracker/               # Daily/Weekly Salah tracker & ViewModel
    ├── splash/                     # Animated Splash screen
    └── tasbih/                     # Digital Tasbih counter & ViewModel
```

---

## 🛠 Tech Stack & Version Catalog

Dependencies are centrally managed in `gradle/libs.versions.toml`:

| Component / Layer | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) | `2.2.10` | Primary development language |
| **Build Tool** | [Android Gradle Plugin (AGP)](https://developer.android.com/studio/build) | `9.5.1` | Build system |
| **Code Generation** | [KSP](https://kotlinlang.org/docs/ksp-overview.html) | `2.2.10-2.0.2` | Kotlin Symbol Processing for Room & Hilt |
| **UI Framework** | [Jetpack Compose BOM](https://developer.android.com/develop/ui/compose/bom) | `2024.10.00` | Declarative UI toolkit |
| **Design System** | [Material 3](https://m3.material.io/) / [Android Material](https://github.com/material-components/material-components-android) | `1.14.0` | Material design components |
| **Dependency Injection** | [Dagger Hilt](https://dagger.dev/hilt/) | `2.60.1` | Compile-time dependency injection |
| **Navigation** | [Navigation Compose](https://developer.android.com/guide/navigation) | `2.8.4` | Single-activity Compose navigation |
| **Hilt Navigation** | [Hilt Navigation Compose](https://developer.android.com/jetpack/androidx/releases/hilt) | `1.2.0` | ViewModel injection in Compose navigation |
| **Local Database** | [Room Database](https://developer.android.com/training/data-storage/room) | `2.8.5` | SQLite abstraction layer with Coroutines Flow |
| **Networking** | [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) | `3.0.0` / `4.12.0` | REST API requests & logging interceptor |
| **Location** | [Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary) | `21.4.0` | Device location for Qibla & Prayer calculations |
| **Coroutines** | [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) | `1.11.0` | Asynchronous programming & Play Services await |
| **Lifecycle** | [AndroidX Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) | `2.9.0` | ViewModels & Compose lifecycle state integration |
| **Core KTX** | [AndroidX Core KTX](https://developer.android.com/jetpack/androidx/releases/core) | `1.18.0` | Kotlin extensions for Android framework |
| **Activity** | [Activity Compose](https://developer.android.com/jetpack/androidx/releases/activity) | `1.13.0` | Compose integration for Activity |

---

## ⚙️ App Configuration Specs

- **Application ID**: `com.example.noorapp`
- **Compile SDK**: `36`
- **Target SDK**: `36`
- **Min SDK**: `24` (Android 7.0 Nougat)
- **Java Compatibility**: JDK 21 (`JavaVersion.VERSION_21`)
- **Version Code**: `3`
- **Version Name**: `"1.0.0"`

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2.1) or newer recommended.
- **Java Development Kit**: JDK 21 configured in Android Studio.
- **Android SDK**: API Level 36 SDK installed.

### 1. Clone the Project
```bash
git clone https://github.com/hammad-ahmed-android-engineer/IslamicApp.git
cd IslamicApp
```

### 2. Build & Run from Command Line

#### Compile Project:
```bash
# Windows
.\gradlew.bat compileDebugKotlin

# macOS / Linux
./gradlew compileDebugKotlin
```

#### Build Debug APK:
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
The compiled APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

#### Install on Connected Device / Emulator:
```bash
# Windows
.\gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```

---

## 🔒 Permissions Breakdown

- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: Computes exact Qibla direction and local prayer schedules based on geographical coordinates.
- `INTERNET`: Downloads Quran audio recitations and accesses online APIs for Hadiths, Duas, and Prayer schedules.
- `VIBRATE`: Provides physical haptic feedback when counting on the Digital Tasbih.
- `POST_NOTIFICATIONS`: Delivers Adhan notifications for prayer times (Android 13+).
- `SCHEDULE_EXACT_ALARM` & `USE_EXACT_ALARM`: Ensures Adhan alarms trigger precisely at calculated prayer times.
- `WAKE_LOCK`: Ensures prayer alarm notifications alert the user reliably even when the device is idle.

---

## 📄 License

Distributed under the terms of the **Apache License 2.0**. See `LICENSE` for details.
