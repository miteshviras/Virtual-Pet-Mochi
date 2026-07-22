package com.example.data

enum class AchievementCategory(val displayName: String, val icon: String) {
  CARE("Pet Care", "❤️"),
  MINI_GAMES("Arcade Master", "🎮"),
  ECONOMY("Treasure Collector", "💰"),
  DECORATION("Home Decorator", "🛋️"),
  CUSTOMIZATION("Fashionista", "👗"),
  PROGRESSION("Milestones", "⭐")
}

data class AchievementDefinition(
  val id: String,
  val title: String,
  val description: String,
  val icon: String,
  val category: AchievementCategory,
  val maxProgress: Int,
  val rewardCoins: Int,
  val rewardXp: Int,
  val unlockLevel: Int = 1
)

data class AchievementProgress(
  val achievementId: String,
  val currentProgress: Int = 0,
  val isClaimed: Boolean = false
)

enum class DailyQuestType {
  FEED, BATH, PLAY, EARN_COINS, DECORATE
}

data class DailyQuest(
  val id: String,
  val title: String,
  val description: String,
  val icon: String,
  val type: DailyQuestType,
  val targetProgress: Int,
  val currentProgress: Int = 0,
  val rewardCoins: Int = 100,
  val rewardXp: Int = 40,
  val isClaimed: Boolean = false
)

data class PlayerStatsMeta(
  val lifetimeCoinsEarned: Int = 2450,
  val lifetimeCoinsSpent: Int = 1200,
  val totalFoodsFed: Int = 0,
  val totalBathsGiven: Int = 0,
  val totalCareActionsPerformed: Int = 0,
  val totalFurniturePlaced: Int = 0,
  val totalCustomizationsEquipped: Int = 0,
  val totalActivitiesPlayed: Int = 0,
  val totalMiniGameCoinsEarned: Int = 0
)

enum class CollectionCategory(val displayName: String, val icon: String) {
  FOOD("Gourmet Snacks", "🍎"),
  FURNITURE("Room Furniture", "🛋️"),
  ACCESSORIES("Fashion Outfits", "👗"),
  WALLPAPERS_FLOORS("Skins & Tiles", "🎨"),
  MINI_GAMES("Arcade Games", "🎮"),
  ACHIEVEMENTS("Trophies", "🏆")
}

data class CollectionItem(
  val id: String,
  val name: String,
  val icon: String,
  val category: CollectionCategory,
  val isUnlocked: Boolean = false,
  val unlockSource: String = "Leveling & Shop"
)

object MetaRegistry {

  val achievements: List<AchievementDefinition> = listOf(
    // CARE
    AchievementDefinition("ach_feed_10", "First Feeds", "Feed Mochi 10 delicious snacks", "🍎", AchievementCategory.CARE, 10, 100, 30),
    AchievementDefinition("ach_feed_50", "Gourmet Chef", "Feed Mochi 50 delicious snacks", "🍱", AchievementCategory.CARE, 50, 250, 75),
    AchievementDefinition("ach_bath_10", "Squeaky Clean", "Give Mochi 10 warm bubble baths", "🧼", AchievementCategory.CARE, 10, 100, 30),
    AchievementDefinition("ach_care_100", "Devoted Companion", "Perform 100 pet care interactions", "💖", AchievementCategory.CARE, 100, 500, 150),

    // MINI GAMES
    AchievementDefinition("ach_games_10", "Arcade Rookie", "Play 10 mini-games or activities", "🎮", AchievementCategory.MINI_GAMES, 10, 150, 50),
    AchievementDefinition("ach_games_50", "Arcade Champion", "Play 50 mini-games or activities", "🏆", AchievementCategory.MINI_GAMES, 50, 600, 200),
    AchievementDefinition("ach_game_coins_1000", "Coin Magnet", "Earn 1,000 coins from mini-games", "✨", AchievementCategory.MINI_GAMES, 1000, 300, 100),

    // ECONOMY
    AchievementDefinition("ach_coins_5000", "Big Saver", "Accumulate 5,000 lifetime coins", "💰", AchievementCategory.ECONOMY, 5000, 400, 120),
    AchievementDefinition("ach_spend_2000", "Shopaholic", "Spend 2,000 coins in the pet boutique", "🛍️", AchievementCategory.ECONOMY, 2000, 250, 80),

    // DECORATION
    AchievementDefinition("ach_decor_5", "Interior Decorator", "Place 5 furniture items in rooms", "🛋️", AchievementCategory.DECORATION, 5, 200, 60),
    AchievementDefinition("ach_decor_15", "Master Architect", "Place 15 furniture items in rooms", "🏰", AchievementCategory.DECORATION, 15, 500, 150),

    // CUSTOMIZATION
    AchievementDefinition("ach_fashion_5", "Style Icon", "Equip 5 different cosmetic outfits", "👗", AchievementCategory.CUSTOMIZATION, 5, 200, 60)
  )

  fun getDefaultDailyQuests(): List<DailyQuest> = listOf(
    DailyQuest("dq_feed", "Yummy Meals", "Feed Mochi 2 delicious meals or snacks", "🍎", DailyQuestType.FEED, targetProgress = 2, currentProgress = 0, rewardCoins = 100, rewardXp = 40),
    DailyQuest("dq_play", "Arcade Fun", "Play or win 1 mini-game in the Arcade", "🎮", DailyQuestType.PLAY, targetProgress = 1, currentProgress = 0, rewardCoins = 150, rewardXp = 50),
    DailyQuest("dq_bath", "Sparkling Clean", "Give Mochi 1 warm bubble bath", "🧼", DailyQuestType.BATH, targetProgress = 1, currentProgress = 0, rewardCoins = 120, rewardXp = 45)
  )

  fun getCollectionCatalog(
    unlockedIds: Set<String>,
    masterInventory: List<MasterItem>,
    highScoresMap: Map<String, Int>
  ): List<CollectionItem> {
    val list = mutableListOf<CollectionItem>()

    // Foods
    masterInventory.filter { it.mainCategory == ItemMainCategory.FOOD }.forEach { item ->
      list.add(
        CollectionItem(
          id = item.id,
          name = item.displayName,
          icon = item.icon,
          category = CollectionCategory.FOOD,
          isUnlocked = unlockedIds.contains(item.id) || item.count > 0,
          unlockSource = "Snack Shop"
        )
      )
    }

    // Furniture
    RoomCustomizationRegistry.furnitureCatalog.forEach { f ->
      list.add(
        CollectionItem(
          id = f.id,
          name = f.displayName,
          icon = f.icon,
          category = CollectionCategory.FURNITURE,
          isUnlocked = unlockedIds.contains(f.id),
          unlockSource = "Furniture Store"
        )
      )
    }

    // Accessories / Cosmetics
    masterInventory.filter { it.isEquippable }.forEach { item ->
      list.add(
        CollectionItem(
          id = item.id,
          name = item.displayName,
          icon = item.icon,
          category = CollectionCategory.ACCESSORIES,
          isUnlocked = unlockedIds.contains(item.id) || item.count > 0,
          unlockSource = "Dress-Up Studio"
        )
      )
    }

    // Wallpapers & Floors
    RoomCustomizationRegistry.wallpapers.forEach { wp ->
      list.add(
        CollectionItem(
          id = wp.id,
          name = wp.displayName,
          icon = wp.icon,
          category = CollectionCategory.WALLPAPERS_FLOORS,
          isUnlocked = unlockedIds.contains(wp.id) || wp.unlockLevel <= 1,
          unlockSource = "Decor Studio"
        )
      )
    }

    // Mini Games
    ActivityRegistry.activities.forEach { act ->
      list.add(
        CollectionItem(
          id = act.id,
          name = act.displayName,
          icon = act.icon,
          category = CollectionCategory.MINI_GAMES,
          isUnlocked = (highScoresMap[act.id] ?: 0) > 0,
          unlockSource = "Activity Arcade"
        )
      )
    }

    return list
  }
}
