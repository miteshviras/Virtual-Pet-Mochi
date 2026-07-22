# Android & Unity Game Architecture Brain Document

## Overview
"Virtual Pet Mochi" is a high-performance, completely original offline Android virtual pet game powered by Unity 6 LTS C# architecture / Jetpack Compose Android host architecture.

## Architecture Layers
1. **Presentation Layer (UI/Views)**:
   - Modern M3 HUD & Responsive Control Panels (Hunger, Energy, Happiness, Health, Cleanliness, Friendship, XP, Level)
   - Room Navigation: Bedroom, Kitchen, Bathroom, Playroom, Garden with Horizontal Drag Swiping & Camera Transitions
   - Interactive Room Props: Lamp, Refrigerator, Bubble Bathtub, Bouncy Ball, Magic Apple Tree, Puzzles, Book Shelf
   - Mini-Games Hub (Bubble Burst, Memory Match, Fruit Catch, Color Pop, Puzzle Blocks, Sky Jump, Firefly Chase)
   - Shop & Inventory UI (Foods, Potions, Cosmetics, Room Skins)

2. **Core Systems & Managers (C# / Kotlin Architecture)**:
   - `GameManager`: Core lifecycle, state initialization, game loop, real-time decay loop, offline time simulation (capped at 12h)
   - `PetController` & `PetStateManager`: Evaluates `PetMoodState` (Happy, Calm, Hungry, Sleepy, Dirty, Sick, Excited, Lonely)
   - `CareActionManager` & `CareActionRegistry`: 10 core care interactions (Feed, Water, Sleep, Wake, Bath, Brush, Medicine, Pet/Hug, Play, Praise) with room dependency validation and anti-spam cooldown timers
   - `SaveSystem` & `PetDatabase`: Room DB local persistence with `lastVisitedRoom` and timestamped offline progression
   - `RoomManager` & `RoomRegistry`: 5 room configurations with interactive props, positions, rewards, and ambient sound metadata
   - `RoomViewport`: Camera slide/fade transition view with gesture swiping & dynamic object placements
   - `CareActionsPanel` & `FloatingStatOverlay`: Contextual care action toolbar and floating stat gain indicator pills (+25 Hunger, +10 XP)
   - `SoundManager`: Offline audio tone synthesizer for tap blips, prop interactions, room transitions, care action sounds (eating, drinking, bath splash, brush, cuddle, praise, medicine), and coins
   - `EconomyManager` & `RewardSystem`: Data-driven economy engine with currency validation, transaction history logs, daily streak care reward dispenser (Day 1-7), special offer bundles, and modular reward payloads (Coins, XP, Items, Gems)
   - `ShopManager` & `InventoryManager`: Data-driven item database, multi-quantity buying/selling (1x, 5x, Max), bundles & limited items, inventory sorting/filtering, stack management, consumable usage, equipping & room placement
   - `ItemDatabase` & `ItemDefinition`: ScriptableObjects/Data-driven models supporting ItemRarity (COMMON, RARE, EPIC, LEGENDARY), tags, unlock levels, stack limits, multi-stat modifiers, and shop bundles
   - `PetAI` & `AnimationController`: Procedural pet expressions (sick, sleepy, dirty, excited), physics squish/stretch, ambient mood particles
   - `AudioManager`: Channel-based SFX and ambient background audio system with dedicated purchase, reward, error, and category switch sounds
   - `MiniGameManager`: Modular mini-game runtime with high scores and reward formulas
   - `EventSystem`: Observer pattern for decoupled event dispatch (`OnPetStatChanged`, `OnLevelUp`, `OnItemUsed`, etc.)

3. **Data Models & ScriptableObjects**:
   - `PetStats`: Hunger (0-100), Energy (0-100), Happiness (0-100), Health (0-100), Cleanliness (0-100), Friendship (0-100), XP, Level
   - `MasterItem`: ID, InternalName, DisplayName, Description, MainCategory, Subcategory, Icon, Rarity (Common, Rare, Epic, Legendary), PurchasePrice, SellPrice, StackLimit, UnlockLevel, Tags, Usable, Equippable, Placeable, Consumable, StatModifiers
   - `SaveData`: Timestamped pet state, master item inventory records, equipped items (12 slots), appearance customization settings, room placements, mini-game high scores
   - `CosmeticSlot`: HAT, HAIR, GLASSES, FACE, NECK, SHIRT, JACKET, PANTS, SHOES, TAIL, BACK, HAND
   - `AppearanceOptions`: Skin Color palette, Eye Styles, Eye Colors, Mouth Expressions, Body Patterns

4. **Persistence & Security**:
   - 100% Offline execution without network dependencies or ads.

5. **Phase 6: Pet Customization & Appearance System**:
   - Modular 12-Slot Cosmetic Engine (`CosmeticSlot`, `CustomizationRegistry`) supporting real-time preview, equipping, unequipping, and state locking based on player level & item inventory ownership.
   - Layered Procedural Pet Canvas Rendering (`MochiPetView`) rendering skin color gradients, body patterns, eye styles/colors, mouth expressions, clothes (shirts, jackets, pants, shoes), and accessories (hats, glasses, hair, neck, back, hand, tail) across all pet emotions & squish animations.
   - Event-Driven Customization Pipeline (`OnCosmeticEquipped`, `OnCosmeticRemoved`, `OnAppearanceChanged`, `OnCustomizationSaved`) integrated with `SoundManager` audio cues and `SaveManager` / Room DB persistence.

6. **Phase 7: Home Decoration & Furniture Placement System**:
   - Modular Furniture Placement Engine (`PlacedFurniture`, `FurnitureCategory`, `RoomCustomizationRegistry`) with independent spatial configurations for Bedroom, Kitchen, Bathroom, Playroom, and Garden.
   - Interactive Placement Studio (`RoomDecorationSheet` & `RoomViewport` Overlay) supporting real-time grid snapping, dragging, collision boundary constraints, rotation, item removal/inventory return, wallpaper changes, and flooring swaps.
   - Decoupled Room Event System (`OnFurniturePlaced`, `OnFurnitureRemoved`, `OnFurnitureMoved`, `OnWallpaperChanged`, `OnFloorChanged`, `OnRoomReset`) integrated with `SoundManager` audio cues and Room Database persistence (v4).

7. **Phase 8: Activity Hub & Mini-Game Framework**:
   - Scalable Activity Hub (`ActivityHubSheet`) browsing 6 activity categories (Mini-Games, Pet Activities, Training, Educational, Seasonal Events, Special Challenges) with difficulty filters, level requirements, and high scores.
   - Generic Mini-Game Framework & Session Engine (`ActivityFrameworkData`, `MiniGameFrameworkHost`) providing standardized game loop, HUD, combo multipliers, pause/resume, countdowns, stars (1-3), and victory/defeat results overlays.
   - Decoupled Activity Event Pipeline (`GameStarted`, `GamePaused`, `GameResumed`, `GameCompleted`, `GameFailed`, `RewardGranted`, `HighScoreUpdated`) integrated with `EconomyManager`, `SoundManager` audio cues, and Room DB persistence (v5).

8. **Phase 9: Player Progression & Meta Systems**:
   - Player Level & Pet Friendship Progression Engine (`ProgressionData`, `ProgressionManager`) supporting XP levelling, pet growth, level-up celebration overlays, and reward payouts.
   - Comprehensive Achievements & Tiers Engine (`AchievementData`, `AchievementsSheet`) tracking 12+ multi-tier achievements (Feeding, Games, Coins, Furniture, Customization, Care) with progress claim buttons and reward disbursements.
   - Daily Quests & Tasks System (`DailyQuestData`, `DailyTasksSheet`) generating 5 dynamic daily quests with automatic midnight reset, progress bars, and reward claim states.
   - Collections & Codex Compendium (`CollectionsSheet`) tracking percentage completion across 6 categories (Foods, Furniture, Accessories, Wallpapers, Mini-Games, Achievements).
   - Player Profile & Lifetime Statistics Dashboard (`PlayerProfileSheet`) recording lifetime play time, total coins earned/spent, foods fed, games played, best scores, care actions, furniture placed, and customization count.
   - Event-Driven Meta Progression Pipeline (`OnLevelUp`, `OnAchievementCompleted`, `OnDailyQuestClaimed`, `OnCollectionUnlocked`) integrated with `SoundManager` victory tones and Room DB persistence (v6).
