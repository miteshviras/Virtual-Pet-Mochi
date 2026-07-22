# Virtual Pet Mochi 🐾✨

A 100% offline cozy virtual pet application built for Android using modern **Kotlin**, **Jetpack Compose**, and **Room Database**.

---

## 🌟 Key Features

- **100% Offline Mode**: Runs entirely on-device with zero network requests, zero API key requirements, and zero remote dependencies.
- **Dynamic Pet Simulation Engine**: Real-time decay system for Hunger, Energy, Happiness, Cleanliness, Health, and Friendship.
- **Interactive Multi-Room House**: Bedroom, Kitchen, Bathroom, Living Room, Playroom, and Garden with interactive props.
- **Care System**: Feed, bathe, pet, tuck to sleep, give medicine, and play games with Mochi.
- **Pet Dress-Up Studio (12 Cosmetic Slots)**:
  - Slots: Hat, Hair Decor, Glasses, Face, Neck, Shirt, Jacket, Pants, Shoes, Tail, Back, Hand Prop.
  - Customization: 10 Skin Color Gradients, 6 Eye Styles, 8 Eye Colors, 6 Mouth Expressions, and 6 Body Patterns.
- **Economy & Shop**: Master item database, shop bundles, daily reward logins, and level progression.
- **Mini-Games Hub**: Tap-A-Mochi, Fruit Catch, and Memory Match games with coins/XP rewards and high scores.
- **Audio & Haptics**: Procedural audio tones generated via Android `ToneGenerator` for instant sound feedback.

---

## 🛠️ Prerequisites

Before building or running the project, ensure you have:

- **Android Studio** (Ladybug / Jellyfish / Hedgehog or newer)
- **Android SDK**: API Level 34 or 36 (Minimum SDK: 24 / Android 7.0)
- **JDK**: Java 11 or Java 17
- **Gradle**: 8.x + Kotlin Gradle Plugin with KSP

---

## 📦 Installation & Setup Instructions

### 1. Clone or Download the Repository

```bash
git clone https://github.com/your-username/virtual-pet-mochi.git
cd virtual-pet-mochi
```

### 2. Open in Android Studio

1. Launch **Android Studio**.
2. Select **Open an Existing Project**.
3. Choose the `virtual-pet-mochi` root directory.
4. Allow Gradle to sync dependencies automatically.

---

## 🚀 Building & Running the App

### Option A: Via Android Studio (Recommended)

1. Connect your Android physical device via USB debugging OR launch an Android Virtual Device (AVD Emulator).
2. Select the `app` run configuration in the top toolbar.
3. Click the **Run ▶** button (or press `Shift + F10`).

### Option B: Via Command Line (Gradle)

To build the debug APK:

```bash
./gradlew assembleDebug
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

To install directly onto a connected device via ADB:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

To run unit tests:

```bash
./gradlew testDebugUnitTest
```

---

## 🔒 Privacy & Offline Assurance

- **No Internet Permission Required**: This app does not request or require the `android.permission.INTERNET` permission in production.
- **Local Persistence**: All save data is safely stored on your device in local SQLite tables managed by Android Room Database.
- **No Analytics / No Tracking**: Total user privacy with 0 telemetry or external SDKs.

---

## 📂 Architecture Overview

- **UI Framework**: Jetpack Compose (Material 3) with custom Canvas graphics.
- **State Management**: `ViewModel` with `StateFlow` and `collectAsStateWithLifecycle`.
- **Database**: Room DB (`PetDatabase`, `PetDao`, `PetEntity`).
- **Data Models**: Modular `MasterItem`, `CosmeticSlot`, `CustomizationRegistry`, and `EconomyData`.
