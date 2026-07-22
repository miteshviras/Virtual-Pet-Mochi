package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet_stats")
data class PetEntity(
  @PrimaryKey val id: Int = 1,
  val name: String = "Mochi",
  val level: Int = 12,
  val xp: Int = 65,
  val maxXp: Int = 100,
  val coins: Int = 2450,
  val hunger: Float = 85f,      // 0 - 100
  val energy: Float = 42f,      // 0 - 100
  val happiness: Float = 95f,   // 0 - 100
  val cleanliness: Float = 20f, // 0 - 100
  val health: Float = 90f,      // 0 - 100
  val friendship: Float = 80f,  // 0 - 100
  val equippedHat: String = "none",
  val equippedHair: String = "none",
  val equippedGlasses: String = "none",
  val equippedFace: String = "none",
  val equippedNeck: String = "none",
  val equippedShirt: String = "none",
  val equippedJacket: String = "none",
  val equippedPants: String = "none",
  val equippedShoes: String = "none",
  val equippedTail: String = "none",
  val equippedBack: String = "none",
  val equippedHand: String = "none",
  val equippedColor: String = "pastel_pink",
  val eyeStyle: String = "sparkle_round",
  val eyeColor: String = "berry_violet",
  val mouthStyle: String = "sweet_smile",
  val bodyPattern: String = "none",
  val bedroomWallpaper: String = "wp_starry_night",
  val bedroomFloor: String = "fl_oak_wood",
  val kitchenWallpaper: String = "wp_cozy_brick",
  val kitchenFloor: String = "fl_checkered",
  val bathroomWallpaper: String = "wp_mint_grid",
  val bathroomFloor: String = "fl_marble_tile",
  val playroomWallpaper: String = "wp_pastel_stripes",
  val playroomFloor: String = "fl_cozy_carpet",
  val gardenWallpaper: String = "wp_sunny_meadow",
  val gardenFloor: String = "fl_green_lawn",
  val placedFurnitureJson: String = "",
  val highScoresJson: String = "",
  val achievementsJson: String = "",
  val dailyQuestsJson: String = "",
  val lastDailyResetDate: String = "",
  val unlockedCollectionsJson: String = "",
  val lifetimeCoinsEarned: Int = 2450,
  val lifetimeCoinsSpent: Int = 1200,
  val totalFoodsFed: Int = 0,
  val totalBathsGiven: Int = 0,
  val totalCareActionsPerformed: Int = 0,
  val totalFurniturePlaced: Int = 0,
  val totalCustomizationsEquipped: Int = 0,
  val totalActivitiesPlayed: Int = 0,
  val totalMiniGameCoinsEarned: Int = 0,
  val lastVisitedRoom: String = "PLAY",
  val isOnboardingCompleted: Boolean = false,
  val lastUpdateTime: Long = System.currentTimeMillis()
)

data class InventoryItem(
  val id: String,
  val name: String,
  val category: ItemCategory,
  val icon: String,
  val price: Int,
  val statBoostType: StatType,
  val statBoostAmount: Float,
  val count: Int = 0,
  val description: String = ""
)

enum class ItemCategory {
  FOOD, SOAP, MEDICINE, TOY, CLOTHING, DECOR
}

enum class StatType {
  HUNGER, ENERGY, HAPPINESS, CLEANLINESS, HEALTH, FRIENDSHIP, XP
}

enum class RoomType(val displayName: String, val icon: String) {
  KITCHEN("Kitchen", "🍳"),
  BATH("Bath", "🛁"),
  PLAY("Playroom", "🧸"),
  SLEEP("Bedroom", "🛏️"),
  GARDEN("Garden", "🌳")
}

enum class PetEmotion {
  HAPPY, HUNGRY, TIRED, DIRTY, PLAYFUL, SLEEPING, CELEBRATING, EATING, BATHING, SICK, EXCITED, LONELY
}

enum class PetMoodState(val label: String, val icon: String, val description: String) {
  HAPPY("Happy", "😊", "Mochi is feeling joyful and content!"),
  CALM("Calm", "😌", "Mochi is feeling relaxed and peaceful."),
  HUNGRY("Hungry", "😋", "Mochi's tummy is rumbling for a snack!"),
  SLEEPY("Sleepy", "😴", "Mochi needs a cozy nap in bedroom."),
  DIRTY("Dirty", "🧼", "Mochi needs a warm bubble bath!"),
  SICK("Sick", "🤒", "Mochi needs medicine or a cozy rest."),
  EXCITED("Excited", "🥳", "Mochi is bursting with high energy!"),
  LONELY("Lonely", "🥺", "Mochi wants to play mini-games together!")
}

