package com.example.data

import androidx.compose.ui.graphics.Color

enum class CosmeticSlot(val displayName: String, val icon: String) {
  HAT("Hat", "👑"),
  HAIR("Hair Decor", "🎀"),
  GLASSES("Glasses", "👓"),
  FACE("Face Decor", "✨"),
  NECK("Neck Accessory", "🧣"),
  SHIRT("Shirt", "👕"),
  JACKET("Jacket", "🧥"),
  PANTS("Pants / Lower", "👖"),
  SHOES("Shoes", "👟"),
  TAIL("Tail Accessory", "🐾"),
  BACK("Back Accessory", "🎒"),
  HAND("Hand Prop", "🪄")
}

data class SkinColorOption(
  val id: String,
  val displayName: String,
  val colors: List<Long>, // ARGB hex colors for gradient
  val icon: String,
  val unlockLevel: Int = 1,
  val price: Int = 0
)

data class EyeStyleOption(
  val id: String,
  val displayName: String,
  val icon: String,
  val unlockLevel: Int = 1,
  val price: Int = 0
)

data class EyeColorOption(
  val id: String,
  val displayName: String,
  val colorHex: Long,
  val icon: String,
  val unlockLevel: Int = 1,
  val price: Int = 0
)

data class MouthStyleOption(
  val id: String,
  val displayName: String,
  val icon: String,
  val unlockLevel: Int = 1,
  val price: Int = 0
)

data class BodyPatternOption(
  val id: String,
  val displayName: String,
  val icon: String,
  val unlockLevel: Int = 1,
  val price: Int = 0
)

sealed class CustomizationEvent {
  data class CosmeticEquipped(val slot: CosmeticSlot, val itemId: String) : CustomizationEvent()
  data class CosmeticRemoved(val slot: CosmeticSlot) : CustomizationEvent()
  data class AppearanceChanged(val category: String, val optionId: String) : CustomizationEvent()
  object CustomizationSaved : CustomizationEvent()
}

object CustomizationRegistry {

  val skinColors = listOf(
    SkinColorOption("pastel_pink", "Pastel Pink", listOf(0xFFF472B6, 0xFFC084FC, 0xFF818CF8), "🌸", 1, 0),
    SkinColorOption("cotton_candy", "Cotton Candy", listOf(0xFFF43F5E, 0xFFFB7185, 0xFFFFE4E6), "🍬", 1, 50),
    SkinColorOption("lavender", "Lavender Cloud", listOf(0xFFA855F7, 0xFFC084FC, 0xFFE9D5FF), "🪻", 2, 75),
    SkinColorOption("mint_jelly", "Mint Jelly", listOf(0xFF10B981, 0xFF34D399, 0xFFA7F3D0), "🍃", 3, 100),
    SkinColorOption("honey_gold", "Honey Gold", listOf(0xFFF59E0B, 0xFFFBBF24, 0xFFFEF08A), "🍯", 4, 150),
    SkinColorOption("cloud_white", "Cloud White", listOf(0xFFF8FAFC, 0xFFE2E8F0, 0xFFCBD5E1), "☁️", 5, 200),
    SkinColorOption("midnight_berry", "Midnight Berry", listOf(0xFF4338CA, 0xFF6366F1, 0xFF818CF8), "🌌", 6, 250),
    SkinColorOption("sunset_peach", "Sunset Peach", listOf(0xFFEA580C, 0xFFFB923C, 0xFFFED7AA), "🍑", 7, 300),
    SkinColorOption("bubblegum_cyan", "Bubblegum Cyan", listOf(0xFF06B6D4, 0xFF22D3EE, 0xFFA5F3FC), "🩵", 8, 350),
    SkinColorOption("obsidian", "Obsidian Shadow", listOf(0xFF1E293B, 0xFF334155, 0xFF64748B), "💎", 10, 500)
  )

  val eyeStyles = listOf(
    EyeStyleOption("sparkle_round", "Sparkle Round", "✨", 1, 0),
    EyeStyleOption("anime_star", "Anime Star", "🌟", 1, 50),
    EyeStyleOption("cute_arc", "Happy Arc", "◠", 2, 60),
    EyeStyleOption("cool_oval", "Cool Oval", "🕶️", 3, 80),
    EyeStyleOption("winking_star", "Winking Star", "😉", 4, 100),
    EyeStyleOption("cat_eye", "Cat Eye", "🐱", 5, 120)
  )

  val eyeColors = listOf(
    EyeColorOption("berry_violet", "Berry Violet", 0xFFA855F7, "🟣", 1, 0),
    EyeColorOption("ocean_blue", "Ocean Blue", 0xFF3B82F6, "🔵", 1, 40),
    EyeColorOption("emerald_green", "Emerald Green", 0xFF10B981, "🟢", 2, 50),
    EyeColorOption("golden_amber", "Golden Amber", 0xFFF59E0B, "🟡", 3, 60),
    EyeColorOption("ruby_red", "Ruby Red", 0xFFEF4444, "🔴", 4, 75),
    EyeColorOption("rose_pink", "Rose Pink", 0xFFEC4899, "🩷", 5, 90),
    EyeColorOption("chocolate_brown", "Chocolate Brown", 0xFF78350F, "🟤", 6, 100),
    EyeColorOption("midnight_charcoal", "Midnight Charcoal", 0xFF0F172A, "🖤", 7, 120)
  )

  val mouthStyles = listOf(
    MouthStyleOption("sweet_smile", "Sweet Smile", "😊", 1, 0),
    MouthStyleOption("tiny_open", "Tiny Open", "😮", 1, 30),
    MouthStyleOption("cat_w", "Cat W-Mouth", "3", 2, 50),
    MouthStyleOption("tongue_blep", "Tongue Blep", "😋", 3, 70),
    MouthStyleOption("joyful_open", "Joyful Open", "😃", 4, 90),
    MouthStyleOption("cool_smirk", "Cool Smirk", "😏", 5, 110)
  )

  val bodyPatterns = listOf(
    BodyPatternOption("none", "Pure Solid", "⚪", 1, 0),
    BodyPatternOption("spots", "Pastel Spots", "🦛", 2, 60),
    BodyPatternOption("hearts", "Heart Patches", "💖", 3, 100),
    BodyPatternOption("stardust", "Star Dust", "⭐", 4, 150),
    BodyPatternOption("chevrons", "Chevron Waves", "🌊", 5, 200),
    BodyPatternOption("galaxy", "Galaxy Glow", "🌌", 8, 350)
  )

  fun getSlotForItem(item: MasterItem): CosmeticSlot? {
    if (!item.isEquippable) return null
    return when (item.subcategory.lowercase()) {
      "hat", "headwear" -> CosmeticSlot.HAT
      "hair", "ribbon" -> CosmeticSlot.HAIR
      "glasses", "eyewear" -> CosmeticSlot.GLASSES
      "face", "mask", "blush" -> CosmeticSlot.FACE
      "neck", "scarf", "tie" -> CosmeticSlot.NECK
      "shirt", "top" -> CosmeticSlot.SHIRT
      "jacket", "coat", "cape" -> CosmeticSlot.JACKET
      "pants", "shorts", "skirt" -> CosmeticSlot.PANTS
      "shoes", "boots" -> CosmeticSlot.SHOES
      "tail" -> CosmeticSlot.TAIL
      "back", "backpack", "wings" -> CosmeticSlot.BACK
      "hand", "wand", "prop" -> CosmeticSlot.HAND
      else -> when {
        item.id.startsWith("hat_") -> CosmeticSlot.HAT
        item.id.startsWith("hair_") -> CosmeticSlot.HAIR
        item.id.startsWith("glasses_") -> CosmeticSlot.GLASSES
        item.id.startsWith("face_") -> CosmeticSlot.FACE
        item.id.startsWith("neck_") -> CosmeticSlot.NECK
        item.id.startsWith("shirt_") -> CosmeticSlot.SHIRT
        item.id.startsWith("jacket_") -> CosmeticSlot.JACKET
        item.id.startsWith("pants_") -> CosmeticSlot.PANTS
        item.id.startsWith("shoes_") -> CosmeticSlot.SHOES
        item.id.startsWith("tail_") -> CosmeticSlot.TAIL
        item.id.startsWith("back_") -> CosmeticSlot.BACK
        item.id.startsWith("hand_") -> CosmeticSlot.HAND
        else -> CosmeticSlot.HAT
      }
    }
  }
}
