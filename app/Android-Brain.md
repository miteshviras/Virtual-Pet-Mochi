# Android Architecture & System Design Document (Android-Brain)

## 1. Executive Overview
This document serves as the single source of truth for the architecture, data models, state flows, UI component structure, database schemas, and release readiness guidelines for the **Mochi Pet Companion** virtual pet Android application.

The app is built natively in **Kotlin** using **Jetpack Compose** with **Material 3 Design System**, persistent offline storage via **Room Database**, reactive flow management with **StateFlow / Coroutines**, and custom Canvas animation engines for pet rendering, room decoration, mini-games, and particle celebrations.

---

## 2. Core Architecture & Layered Design

```
                   +---------------------------------------+
                   |           Jetpack Compose UI          |
                   | (MainActivity, RoomViewport, Sheets)  |
                   +-------------------+-------------------+
                                       |
                                       v
                   +---------------------------------------+
                   |             MochiViewModel            |
                   |  (Central State & Game Engine VM)     |
                   +-------------------+-------------------+
                                       |
                     +-----------------+-----------------+
                     |                                   |
                     v                                   v
       +----------------------------+     +----------------------------+
       |   Room Database / PetDao   |     |   Game Registries & Models |
       | (PetEntity Data Storage)   |     | (Furniture, Items, Meta)   |
       +----------------------------+     +----------------------------+
```

### Architecture Pillars:
1. **Model-View-ViewModel (MVVM)**: `MochiViewModel` encapsulates all game loop calculations (hunger decay, happiness, energy, room state, mini-game score persistence, progression systems, achievements, and daily quest tracking).
2. **Unidirectional Data Flow (UDF)**: UI composables observe immutable state flows (`petState`, `roomState`, `masterInventory`, `achievementsMap`, `dailyQuests`, `playerMetaStats`, etc.) and emit user action intents to the ViewModel.
3. **Local Database Persistence**: Room SQLite DB handles pet status, custom room item layouts, unlocked cosmetics, lifetime achievements, high scores, and daily quest progress.
4. **Zero External Server Dependency**: Fully offline-compatible, child-friendly design complying with kids' app guidelines (no ads, no tracking, no hidden external API calls).

---

## 3. Data Models & Database Schemas

### 3.1 Room Database Entities
- **`PetEntity`**: Primary database table (`pets`) holding pet attributes:
  - `id`: Unique Int identifier
  - `name`: Pet name (Default: "Mochi")
  - `species`: Pet species type
  - `hunger`, `happiness`, `energy`, `cleanliness`, `health`, `friendship`: Float values [0.0 - 100.0]
  - `level`: Int level (1, 2, 3...)
  - `xp`, `maxXp`: Int experience values
  - `coins`: Int currency balance
  - `currentRoom`: String room identifier (`LIVING_ROOM`, `BEDROOM`, `BATHROOM`, `KITCHEN`, `PLAYROOM`, `GARDEN`)
  - `placedFurnitureJson`: Serialized string of furniture placed in rooms
  - `inventoryJson`: Serialized string of items, counts, and equippable statuses
  - `highScoresJson`: Serialized map of mini-game high scores
  - `achievementsJson`: Serialized achievement progress map
  - `dailyQuestsJson`: Serialized daily quests state
  - `lastDailyResetDate`: String date tracker (`YYYY-MM-DD`)
  - `unlockedCollectionsJson`: Serialized set of codex unlock IDs
  - Lifetime meta stats (`lifetimeCoinsEarned`, `totalCareActionsPerformed`, etc.)

### 3.2 System Registries
- **`RoomData.kt`**: Defines 6 thematic rooms (`LIVING_ROOM`, `BEDROOM`, `BATHROOM`, `KITCHEN`, `PLAYROOM`, `GARDEN`) with custom background colors, wall accents, default floor textures, and interactive props (Bed, Bathtub, Fridge, Toybox, Flower Pot, Sofa).
- **`FurnitureData.kt`**: Defines room furniture items (Sofa, Lamp, Rug, Plant, Bed, Painting, Table, Desk) with 2D grid snapping, rotation, collision detection, and price tiers.
- **`ItemData.kt` & `ItemDatabaseRegistry.kt`**: Defines master food snacks, hygiene tools, medicines, outfits, hats, glasses, and wallpapers.
- **`ProgressionMetaModels.kt`**: Defines 12+ lifetime achievements across 6 categories (`CARE`, `MINI_GAMES`, `ECONOMY`, `DECORATION`, `CUSTOMIZATION`, `PROGRESSION`), daily quests, codex collection items, and player profile stats.

---

## 4. UI Layout & Screen Navigation

The interface utilizes a single-activity architecture (`MainActivity`) centered around an interactive viewport and modular modal bottom sheets:

1. **`SleekTopBar`**:
   - Player Level Badge & XP progress bar
   - Quick action triggers: Trophies 🏆, Daily Quests 📜, Collections Codex 📚, Dress-Up Studio 👗, Room Decor Studio 🛋️, Coin Counter 💰
2. **`SleekNeedsDashboard`**:
   - Live status meters for Hunger 🍎, Happiness 😊, Energy ⚡, Cleanliness 🧼, Health ❤️
3. **`RoomViewport`**:
   - Multi-room touch swipe/navigation
   - Custom dynamic Wallpaper & Floor rendering
   - 2D Furniture Placement & Editing overlay (Drag, Rotate, Grid Snap, Remove)
   - Interactive Props (Clicking bed sleeps pet, bathtub bathes pet, fridge feeds pet)
   - Interactive `MochiPetView` with dynamic body physics, breathing animations, mood reactions, and outfit rendering
4. **`CareActionsPanel` & `SleekNavBar`**:
   - Primary care navigation (Feed, Clean, Sleep, Play, Medicine, Decorate, Store)
5. **Modal Bottom Sheets & Celebration Overlays**:
   - `ShopAndInventorySheet`: Multi-tab store and bag management
   - `RoomDecorationSheet`: Furniture catalog and placement controls
   - `CustomizationSheet`: Pet cosmetics and outfit picker
   - `MiniGameHubSheet`: Arcade selection (Bubble Pop, Fruit Catcher, Memory Match)
   - `PlayerProfileSheet`: Caretaker stats, level progress, lifetime counters
   - `AchievementsSheet`: Trophy milestones and reward claims
   - `DailyTasksSheet`: Daily care quests and resets
   - `CollectionsSheet`: Codex item catalog across all categories
   - `LevelUpCelebrationDialog`: Animated level-up celebratory modal
   - `AchievementUnlockedBanner`: Top floating banner for unlocked achievements
   - `ParticleEffectOverlay`: Canvas particle burst engine (stars, coins, sparkles, hearts)

---

## 5. Game Engine & State Flow Management

All core game logic resides within `MochiViewModel.kt`:
- **Real-time Stat Decay Loop**: Coroutine job ticking every 10 seconds, applying subtle hunger/energy/cleanliness decay.
- **Automatic Auto-Save**: Room DB updates whenever stats, inventory, or room configurations change.
- **Progression Engine**:
  - `addXpAndCoins()`: Increments XP, calculates level-ups, triggers celebratory modals, awards level bonuses.
  - `recordCareAction()`, `recordCoinsEarned()`, `recordCoinsSpent()`, `recordFurniturePlaced()`, `recordCustomizationEquipped()`, `recordMiniGamePlayed()`: Automatically updates achievements, daily quest progress, and lifetime stats.
- **Audio & Haptic Feedback**: Handled through `SoundManager.kt` and Compose `LocalHapticFeedback` / Android vibration.

---

## 6. Phase 10 Production Polish & QA Verification

- **Particle Effects**: `ParticleEffectOverlay.kt` renders canvas-based particle physics for reward collection, level ups, feeding, and achievements.
- **Audio Polish**: Optimized `ToneGenerator` audio stream management with volume safety.
- **Haptic Support**: Tactile feedback for key touches, purchase confirmations, and level-ups.
- **Performance & Stability**: Verified 60 FPS Compose rendering, zero memory leaks, and lightweight object reuse.
- **Kids' App Compliance**: Fully offline, ad-free, data privacy compliant.
