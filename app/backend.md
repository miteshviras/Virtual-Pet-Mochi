# Backend & Local Data Persistence Architecture (backend.md)

## 1. Overview & Data Architecture
The **Mochi Pet Companion** application is a 100% client-side, offline-first virtual pet game built for Android. All persistence, game state updates, inventory management, room decoration data, high scores, and achievement progression are handled locally on device using **Android Room Database (SQLite)**.

There are no external remote REST or GraphQL API dependencies, ensuring zero network latency, complete data privacy, and full compliance with Google Play Kids & Family policies.

---

## 2. Local Database Schema & Contracts

### 2.1 Entity: `pets` (PetEntity.kt)
Primary key: `id` (Int, Auto-Generate = true)

| Column Name | Data Type | Default / Description |
| :--- | :--- | :--- |
| `id` | INTEGER | Primary Key |
| `name` | TEXT | Pet name ("Mochi") |
| `species` | TEXT | Pet species ("Cat") |
| `hunger` | REAL | Hunger level [0.0 - 100.0] |
| `happiness` | REAL | Happiness level [0.0 - 100.0] |
| `energy` | REAL | Energy level [0.0 - 100.0] |
| `cleanliness` | REAL | Cleanliness level [0.0 - 100.0] |
| `health` | REAL | Health level [0.0 - 100.0] |
| `friendship` | REAL | Friendship bond level [0.0 - 100.0] |
| `level` | INTEGER | Player level (default: 1) |
| `xp` | INTEGER | Current XP points |
| `maxXp` | INTEGER | XP required for next level |
| `coins` | INTEGER | Currency balance |
| `currentRoom` | TEXT | Active room ID ("LIVING_ROOM") |
| `placedFurnitureJson` | TEXT | Serialized list of placed room furniture |
| `inventoryJson` | TEXT | Serialized inventory items and counts |
| `highScoresJson` | TEXT | Serialized map of mini-game high scores |
| `achievementsJson` | TEXT | Serialized achievement progress |
| `dailyQuestsJson` | TEXT | Serialized daily quests state |
| `lastDailyResetDate` | TEXT | Date string for daily reset ("YYYY-MM-DD") |
| `unlockedCollectionsJson` | TEXT | Serialized list of unlocked codex IDs |
| `isOnboardingCompleted` | INTEGER | Flag indicating if first-launch onboarding is complete (0 or 1) |
| `lifetimeCoinsEarned` | INTEGER | Total lifetime coins earned |
| `lifetimeCoinsSpent` | INTEGER | Total lifetime coins spent |
| `totalFoodsFed` | INTEGER | Lifetime foods fed |
| `totalBathsGiven` | INTEGER | Lifetime baths given |
| `totalCareActionsPerformed` | INTEGER | Total care interactions |
| `totalFurniturePlaced` | INTEGER | Lifetime furniture placed |
| `totalCustomizationsEquipped` | INTEGER | Outfits equipped |
| `totalActivitiesPlayed` | INTEGER | Mini-games played |
| `totalMiniGameCoinsEarned` | INTEGER | Mini-game coin earnings |

---

## 3. Data Access Object (PetDao.kt)

```kotlin
@Dao
interface PetDao {
  @Query("SELECT * FROM pets LIMIT 1")
  fun getPet(): Flow<PetEntity?>

  @Query("SELECT * FROM pets WHERE id = :id")
  suspend fun getPetById(id: Int): PetEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPet(pet: PetEntity): Long

  @Update
  suspend fun updatePetStats(pet: PetEntity)
}
```

---

## 4. Local Serialization Standards
- **Delimiter formats**:
  - Map / Object lists: Item tokens separated by `;;`, attributes separated by `::` or `,`.
  - Simple Sets: Elements separated by `,`.
- **Corrupted Data Safety**: Fallback parsing with try-catch blocks ensuring default values are restored if serialized JSON/string tokens are malformed.
