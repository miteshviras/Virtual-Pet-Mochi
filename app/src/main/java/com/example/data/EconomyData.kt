package com.example.data

enum class TransactionType(val label: String, val icon: String) {
  PURCHASE("Purchase", "🛍️"),
  SELL("Sale", "🪙"),
  REWARD("Care Reward", "🎁"),
  DAILY_BONUS("Daily Streak", "📅"),
  MINI_GAME("Mini-Game", "🎮")
}

data class EconomyTransaction(
  val id: String = System.currentTimeMillis().toString(),
  val timestamp: Long = System.currentTimeMillis(),
  val type: TransactionType,
  val amount: Int,
  val title: String,
  val balanceAfter: Int
)

data class ShopBundle(
  val id: String,
  val title: String,
  val description: String,
  val originalPrice: Int,
  val bundlePrice: Int,
  val icon: String,
  val items: List<Pair<String, Int>>, // item ID to quantity
  val isLimited: Boolean = false
)

data class DailyRewardItem(
  val day: Int,
  val rewardCoins: Int,
  val rewardXp: Int,
  val rewardItem: MasterItem? = null,
  val icon: String,
  val description: String
)

object ShopBundleRegistry {
  val availableBundles = listOf(
    ShopBundle(
      id = "bundle_starter",
      title = "Care Starter Bundle",
      description = "Includes 3x Apple 🍎, 2x Fresh Milk 🥛, and 1x Health Potion 🧪 at 35% OFF!",
      originalPrice = 135,
      bundlePrice = 85,
      icon = "🎁",
      items = listOf(
        "item_apple" to 3,
        "item_milk" to 2,
        "item_potion" to 1
      )
    ),
    ShopBundle(
      id = "bundle_royal",
      title = "Royal Luxury Suite",
      description = "Includes Golden Royal Crown 👑 & Starry Night Lamp 💡 for royal living!",
      originalPrice = 700,
      bundlePrice = 490,
      icon = "👑",
      items = listOf(
        "hat_crown" to 1,
        "decor_lamp" to 1
      ),
      isLimited = true
    ),
    ShopBundle(
      id = "bundle_sweet_party",
      title = "Sweet Macaron Party",
      description = "Includes 3x Rainbow Macarons 🧁 & 1x Party Cap 🥳 for ultimate joy!",
      originalPrice = 440,
      bundlePrice = 290,
      icon = "🥳",
      items = listOf(
        "item_macaron" to 3,
        "hat_party" to 1
      )
    )
  )
}

object DailyRewardRegistry {
  val sevenDayRewards = listOf(
    DailyRewardItem(1, 50, 15, null, "🪙", "+50 Coins to start your care journey!"),
    DailyRewardItem(2, 75, 20, ItemDatabaseRegistry.getItemById("item_apple"), "🍎", "+75 Coins + 1x Crispy Apple!"),
    DailyRewardItem(3, 100, 25, ItemDatabaseRegistry.getItemById("item_milk"), "🥛", "+100 Coins + 1x Fresh Milk!"),
    DailyRewardItem(4, 150, 30, ItemDatabaseRegistry.getItemById("item_soap"), "🧼", "+150 Coins + 1x Lavender Soap!"),
    DailyRewardItem(5, 200, 40, ItemDatabaseRegistry.getItemById("toy_ball"), "⚽", "+200 Coins + 1x Bouncy Ball!"),
    DailyRewardItem(6, 250, 50, ItemDatabaseRegistry.getItemById("item_macaron"), "🧁", "+250 Coins + 1x Rainbow Macaron!"),
    DailyRewardItem(7, 500, 100, ItemDatabaseRegistry.getItemById("hat_party"), "🥳", "GRAND STREAK! +500 Coins + Party Cap!")
  )
}
