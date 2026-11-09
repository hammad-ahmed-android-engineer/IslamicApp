# 🕌 Islamic App - Comprehensive Muslim Companion & Qibla Finder

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.13.2-blue.svg?style=flat&logo=android)](https://developer.android.com/studio/releases/gradle-plugin)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-green.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Expressive-orange.svg?style=flat&logo=materialdesign)](https://m3.material.io/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM%20%2B%20UDF-brightgreen.svg?style=flat)](#-architecture--design-patterns)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

**Islamic App** is a beautifully designed, all-in-one native Android companion for Muslims. Built with **Kotlin**, **Jetpack Compose (Material 3)**, and strict **Clean Architecture**, this app provides a seamless and spiritually enriching experience. It features a full Holy Quran reader, authentic Hadith collections, precise Prayer Timings, Qibla direction, and daily Ibadat tools (Digital Tasbih and Duas)—all wrapped in a serene, custom-themed UI.

---

## 🌟 Overview & Philosophy

The Islamic App operates on three core principles:
1. **Spiritual Serenity through Design**: The UI leverages a custom color palette (Medina Sage, Warm Parchment, and Desert Ochre) alongside traditional Arabic typography (Amiri Quran font) to minimize distractions and enhance reading focus.
2. **Comprehensive Daily Utility**: From calculating accurate prayer times and pointing to the Qibla, to providing digital Dhikr tools and authentic Hadiths, every feature is tailored for daily Muslim life.
3. **Engineering Excellence**: Built with strict Clean Architecture, reactive state flows, responsive layouts, Coroutines, and offline-first Room database support.

---

## ✨ Core Features & Capabilities

### 📖 1. Holy Quran (القرآن الكريم)
- **Surah & Juz Navigation**: Browse the Quran seamlessly by Surahs (Chapters) or Juz (Parts).
- **Rich Text & Typography**: Renders sacred Arabic text with proper Tajweed Waqf marks and Ayah End signs using the custom `Amiri Quran` font.
- **Translation & Transliteration**: Toggleable English translations and transliterations for deeper understanding.
- **Audio Playback**: Listen to high-quality audio recitations with play/pause controls for individual Ayahs.
- **Ayah Actions**: Bookmark, copy, or share specific Ayahs directly from the reader.

### 📚 2. Authentic Hadith Collections (الحديث النبوي)
- **Multiple Collections**: Browse through various authentic books and collections.
- **Chapter-Based Navigation**: Quickly navigate between sections and chapters of Hadith books.
- **Search Capability**: Instantly search for specific Hadiths by text, chapter, or narrator.

### 📿 3. Ibadat & Remembrance (الأذكار والعبادات)
- **Digital Tasbih**: A highly interactive digital counter featuring haptic feedback, adaptive landscape/portrait bead layouts, reset controls, and target setting.
- **Dhikr Presets**: Quick-select presets for common remembrances (e.g., *SubhanAllah*, *Alhamdulillah*, *Allahu Akbar*).
- **Fortress of the Muslim (Duas)**: A categorized collection of daily supplications, complete with search, copy, and share functionalities.

### 🕋 4. Qibla Finder & Prayer Timings
- **Accurate Qibla Compass**: Leverages device sensors and location to point accurately to the Kaaba.
- **Prayer Schedule**: Calculates precise daily prayer timings based on the user's location.

### 📐 5. Adaptive & Responsive Layouts
- Fluid responsive UI supporting both Portrait and Dual-Column/Horizontal Landscape layouts (e.g., adaptive Tasbih UI placing the bead counter next to the controls in landscape).

---

## 📐 Architecture & Design Patterns

The Islamic App follows modern Android architectural standards with strict **Clean Architecture**, **MVVM**, and **Unidirectional Data Flow (UDF)**:

### Complete Project Directory & Package Structure

```
com.ne.muslimprayers.qiblafinder/
├── core/
│   └── theme/                     # Custom Colors (MedinaSage, WarmParchment, DesertOchre), Typography (QuranFontFamily), Theme, and Dimensions (sdp/ssp)
│
├── data/
│   ├── local/                     # Room Database, DAOs, Entities (e.g., IslamicAppDatabase.kt)
│   ├── remote/                    # Retrofit APIs and DTOs (e.g., QuranApiService.kt, QuranDtos.kt)
│   └── repository/                # Concrete repository implementations (e.g., QuranRepositoryImpl)
│
├── di/                            # Dagger Hilt Modules
│   ├── AppModule.kt               # Application scope bindings & coroutine dispatchers
│   └── DatabaseModule.kt          # Room database, DAOs, and network clients
│
├── domain/
│   ├── model/                     # Domain models (Ayah, DuaItem, TasbihState, etc.)
│   ├── repository/                # Abstract repository interfaces (e.g., QuranRepository)
│   └── usecase/                   # Single-responsibility business use cases
│       └── quran/                 # GetQuranSurahsUseCase, etc.
│
├── ui/                            # Presentation Layer (Jetpack Compose + MVVM + UDF)
│   ├── components/                # Reusable UI Primitives (IslamicTopAppBar, IslamicCard, AppText, LoadingState, ErrorState)
│   ├── hadith/                    # Hadith Screen, ViewModel, and Components (HadithCardItem, HadithDetailDialog)
│   ├── ibadat/                    # Ibadat Screen, ViewModel, Tasbih Tab, Duas Tab, and Bead Counter
│   ├── prayers/                   # Prayer Timings Screen, ViewModel, and Components
│   ├── quran/                     # Quran Screen, ViewModel, SurahDetailReader, AyahCardItem, and Settings Sheet
│   └── qibla/                     # Qibla Compass Screen & ViewModel
│
└── MainActivity.kt                # Single Activity entry point
```

### Key Engineering Practices:
- **Unidirectional Data Flow (UDF)**: ViewModels expose immutable `StateFlow<UiState>` collected securely with `collectAsStateWithLifecycle()`.
- **Dependency Injection**: Fully integrated with **Dagger Hilt** for seamless view model injection and testable repository bindings.
- **Custom Design System**: Utilizes custom `AppText`, `IslamicCard`, and scalable dimensions (`sdp` and `ssp` libraries) for perfect rendering across all screen sizes.
- **Coroutines & Flow**: Heavy lifting, API fetching, and database operations are strictly handled off the main thread using Kotlin Coroutines.

---

## 🛠 Tech Stack & Dependencies

All dependencies are centrally managed via Gradle Version Catalog (`libs.versions.toml`):

| Category | Library / Dependency | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) | `2.0.21` | Modern, expressive, type-safe programming |
| **Build System** | [Android Gradle Plugin (AGP)](https://developer.android.com/build) | `8.13.2` | Project build configuration |
| **Annotation Processing** | [KSP](https://kotlinlang.org/docs/ksp-overview.html) | `2.0.21-1.0.28` | High-performance code generation for Room & Hilt |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/develop/ui/compose/bom) | `1.9.3` | Declarative UI toolkit |
| **Design System** | [Material 3](https://developer.android.com/develop/ui/compose/components) | `1.12.0` | Expressive Material Design components |
| **Dependency Injection** | [Dagger Hilt](https://dagger.dev/hilt/) | `2.52` | Dependency injection framework |
| **Navigation** | [Navigation Compose](https://developer.android.com/guide/navigation) | `2.8.4` | Single-activity Compose navigation |
| **Local Database** | [Room Database](https://developer.android.com/training/data-storage/room) | `2.6.1` | SQLite object mapping with Flow support |
| **Network/API** | [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) | `2.11.0` / `4.12.0` | REST API consumption & logging |
| **Location** | [Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary) | `21.3.0` | High-accuracy location for Qibla/Prayers |
| **Responsive Units** | [SDP & SSP Android](https://github.com/intuit/sdp) | `1.1.1` | Scalable size and font dimensions |
| **Image Loading** | [Coil Compose](https://coil-kt.github.io/coil/) | `2.7.0` | Asynchronous image loading |
| **Permissions** | [Accompanist Permissions](https://google.github.io/accompanist/) | `0.36.0` | Compose-friendly permission requests |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Android Studio Jellyfish (2023.3.1+) or newer.
- **Java Development Kit (JDK)**: JDK 17.
- **Android SDK Requirements**: Requires Android 8.0 Oreo (API 26) or higher.

### 1. Clone the Repository
```bash
git clone https://github.com/hammad-ahmed-android-engineer/IslamicApp.git
cd IslamicApp
```

### 2. Open Project in Android Studio
1. Launch Android Studio.
2. Select **File > Open...** and navigate to the cloned `IslamicApp` directory.
3. Allow Gradle to synchronize dependencies.

### 3. Build & Run via Command Line

#### Compile Kotlin Sources:
```bash
# Windows
.\gradlew.bat compileDebugKotlin

# macOS / Linux
./gradlew compileDebugKotlin
```

#### Assemble Debug APK:
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
The output APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### Install Directly onto a Connected Device / Emulator:
```bash
# Windows
.\gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```

---

## 🔒 Permissions & Security

The Islamic App requests the following permissions strictly to provide core functionalities:

- `android.permission.ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: Required to accurately calculate local prayer timings and the precise direction of the Qibla.
- `android.permission.INTERNET`: Required to download Quranic audio recitations and fetch the latest database updates.
- `android.permission.VIBRATE`: Provides sensory haptic feedback while using the Digital Tasbih.
- `android.permission.POST_NOTIFICATIONS`: Prompts for notification permission on Android 13+ (API 33+) to deliver Adhan (Call to Prayer) alerts.

---

## 🤝 Contributing

Contributions, feedback, and issue reports are highly encouraged!
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m "Add amazing feature"`).
4. Push to the branch (`git push origin feature/amazing-feature`).
5. Open a Pull Request.

---

## 📄 License

This project is open-source and distributed under the terms of the [Apache License 2.0](LICENSE).