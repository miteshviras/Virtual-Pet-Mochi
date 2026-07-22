package com.example.data

enum class ItemRarity(val label: String, val colorHex: Long, val borderHex: Long) {
  COMMON("Common", 0xFF64748B, 0xFFCBD5E1),
  RARE("Rare", 0xFF2563EB, 0xFF93C5FD),
  EPIC("Epic", 0xFF9333EA, 0xFFD8B4FE),
  LEGENDARY("Legendary", 0xFFD97706, 0xFFFDE047)
}

enum class ItemMainCategory(val displayName: String, val icon: String) {
  ALL("All Items", "🎒"),
  FOOD("Foods & Drinks", "🍎"),
  CARE("Medicine & Bath", "🧼"),
  WEARABLE("Clothing & Hats", "👑"),
  DECOR("Furniture & Decor", "🖼️"),
  TOYS("Toys & Games", "⚽"),
  COLLECTIBLES("Special & Trophies", "🏆")
}

data class ItemStatModifier(
  val statType: StatType,
  val boostAmount: Float
)

data class MasterItem(
  val id: String,
  val internalName: String,
  val displayName: String,
  val description: String,
  val mainCategory: ItemMainCategory,
  val subcategory: String,
  val icon: String,
  val rarity: ItemRarity = ItemRarity.COMMON,
  val purchasePrice: Int,
  val sellPrice: Int = (purchasePrice * 0.6f).toInt(),
  val stackLimit: Int = 99,
  val unlockLevel: Int = 1,
  val tags: List<String> = emptyList(),
  val isUsable: Boolean = true,
  val isEquippable: Boolean = false,
  val isPlaceable: Boolean = false,
  val isConsumable: Boolean = true,
  val statModifiers: List<ItemStatModifier> = emptyList(),
  val count: Int = 0
) {
  // Bridge method to seamlessly support existing InventoryItem interface
  fun toInventoryItem(): InventoryItem {
    val primaryModifier = statModifiers.firstOrNull()
    return InventoryItem(
      id = id,
      name = displayName,
      category = when (mainCategory) {
        ItemMainCategory.FOOD -> ItemCategory.FOOD
        ItemMainCategory.CARE -> if (subcategory.contains("bath", true) || id.contains("soap", true)) ItemCategory.SOAP else ItemCategory.MEDICINE
        ItemMainCategory.WEARABLE -> ItemCategory.CLOTHING
        ItemMainCategory.DECOR -> ItemCategory.DECOR
        ItemMainCategory.TOYS -> ItemCategory.TOY
        else -> ItemCategory.FOOD
      },
      icon = icon,
      price = purchasePrice,
      statBoostType = primaryModifier?.statType ?: StatType.HAPPINESS,
      statBoostAmount = primaryModifier?.boostAmount ?: 10f,
      count = count,
      description = description
    )
  }
}

enum class InventorySortOption(val label: String, val icon: String) {
  BY_NAME("Name", "🔤"),
  BY_RARITY("Rarity", "⭐"),
  BY_PRICE("Price", "🪙"),
  BY_QUANTITY("Quantity", "🔢")
}
