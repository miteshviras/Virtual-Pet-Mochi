package com.example.data

enum class FurnitureCategory(val displayName: String, val icon: String) {
  BEDS("Beds", "🛏️"),
  SEATING("Chairs & Sofas", "🪑"),
  TABLES("Tables & Desks", "🪵"),
  STORAGE("Cabinets & Shelves", "📦"),
  LAMPS("Lamps & Light", "💡"),
  RUGS("Rugs & Carpets", "🪡"),
  PLANTS("Plants & Nature", "🪴"),
  TOYS("Toys & Play", "🧸"),
  WALL_DECOR("Wall Decor", "🖼️"),
  WALLPAPER("Wallpapers", "🧱"),
  FLOORING("Flooring", "🪵"),
  OUTDOOR("Outdoor Decor", "🌳"),
  SEASONAL("Seasonal", "🎄")
}

data class FurnitureDefinition(
  val id: String,
  val displayName: String,
  val category: FurnitureCategory,
  val icon: String,
  val allowedRooms: List<RoomType> = emptyList(),
  val unlockLevel: Int = 1,
  val price: Int = 50,
  val rarity: ItemRarity = ItemRarity.COMMON,
  val defaultWidthFraction: Float = 0.2f,
  val defaultHeightFraction: Float = 0.2f,
  val tags: List<String> = emptyList()
)

data class PlacedFurniture(
  val instanceId: String,
  val itemId: String,
  val displayName: String,
  val icon: String,
  val roomType: RoomType,
  val xFraction: Float,
  val yFraction: Float,
  val rotationDegrees: Int = 0,
  val category: FurnitureCategory = FurnitureCategory.STORAGE
)

data class WallpaperOption(
  val id: String,
  val displayName: String,
  val icon: String,
  val unlockLevel: Int = 1,
  val topColorHex: Long,
  val bottomColorHex: Long,
  val patternName: String = "clean"
)

data class FloorOption(
  val id: String,
  val displayName: String,
  val icon: String,
  val unlockLevel: Int = 1,
  val colorHex: Long,
  val patternType: String = "wood_planks"
)

sealed class RoomDecorationEvent {
  data class FurniturePlaced(val room: RoomType, val item: PlacedFurniture) : RoomDecorationEvent()
  data class FurnitureRemoved(val room: RoomType, val instanceId: String) : RoomDecorationEvent()
  data class FurnitureMoved(val room: RoomType, val instanceId: String, val newX: Float, val newY: Float) : RoomDecorationEvent()
  data class FurnitureRotated(val room: RoomType, val instanceId: String, val newRotation: Int) : RoomDecorationEvent()
  data class WallpaperChanged(val room: RoomType, val wallpaperId: String) : RoomDecorationEvent()
  data class FloorChanged(val room: RoomType, val floorId: String) : RoomDecorationEvent()
  data class RoomReset(val room: RoomType) : RoomDecorationEvent()
}

object RoomCustomizationRegistry {
  val wallpapers = listOf(
    WallpaperOption("wp_starry_night", "Starry Night", "🌌", 1, 0xFF1E1B4B, 0xFF312E81, "stars"),
    WallpaperOption("wp_pastel_stripes", "Pastel Stripes", "🎨", 1, 0xFFFDF4FF, 0xFFF5D0FE, "stripes"),
    WallpaperOption("wp_cozy_brick", "Cozy Brick", "🧱", 2, 0xFFFFF7ED, 0xFFFED7AA, "bricks"),
    WallpaperOption("wp_mint_grid", "Mint Grid", "🌿", 3, 0xFFECFEFF, 0xFFA5F3FC, "grid"),
    WallpaperOption("wp_floral_blossom", "Floral Blossom", "🌸", 4, 0xFFFFF1F2, 0xFFFECDD3, "dots"),
    WallpaperOption("wp_sunny_meadow", "Sunny Meadow", "☀️", 5, 0xFFF7FEE7, 0xFFA3E635, "clean")
  )

  val floorings = listOf(
    FloorOption("fl_oak_wood", "Oak Planks", "🪵", 1, 0xFF854D0E, "wood_planks"),
    FloorOption("fl_cozy_carpet", "Soft Pink Carpet", "🪡", 1, 0xFFF472B6, "carpet"),
    FloorOption("fl_marble_tile", "Marble Tile", "🏛️", 2, 0xFFE2E8F0, "tiles"),
    FloorOption("fl_checkered", "Checkered Tile", "🏁", 3, 0xFF334155, "checkered"),
    FloorOption("fl_green_lawn", "Soft Grass Lawn", "🌱", 4, 0xFF65A30D, "grass")
  )

  val furnitureCatalog = listOf(
    FurnitureDefinition("furn_bed_canopy", "Royal Canopy Bed", FurnitureCategory.BEDS, "🛏️", listOf(RoomType.SLEEP), 1, 150, ItemRarity.RARE),
    FurnitureDefinition("furn_sofa_cloud", "Cloud Sofa", FurnitureCategory.SEATING, "🛋️", listOf(RoomType.SLEEP, RoomType.PLAY, RoomType.KITCHEN), 1, 120, ItemRarity.COMMON),
    FurnitureDefinition("furn_table_glass", "Modern Coffee Table", FurnitureCategory.TABLES, "🪵", emptyList(), 1, 80, ItemRarity.COMMON),
    FurnitureDefinition("furn_shelf_books", "Oak Bookshelf", FurnitureCategory.STORAGE, "📚", emptyList(), 2, 100, ItemRarity.RARE),
    FurnitureDefinition("furn_lamp_lava", "Neon Lava Lamp", FurnitureCategory.LAMPS, "💡", emptyList(), 2, 90, ItemRarity.RARE),
    FurnitureDefinition("furn_rug_rainbow", "Rainbow Plush Rug", FurnitureCategory.RUGS, "🪡", emptyList(), 1, 75, ItemRarity.COMMON),
    FurnitureDefinition("furn_plant_monstera", "Giant Monstera", FurnitureCategory.PLANTS, "🪴", emptyList(), 3, 110, ItemRarity.RARE),
    FurnitureDefinition("furn_toy_arcade", "Mini Arcade Cabinet", FurnitureCategory.TOYS, "🕹️", listOf(RoomType.PLAY), 3, 200, ItemRarity.EPIC),
    FurnitureDefinition("furn_wall_painting", "Starry Painting", FurnitureCategory.WALL_DECOR, "🖼️", emptyList(), 1, 65, ItemRarity.COMMON),
    FurnitureDefinition("furn_outdoor_fountain", "Magic Stone Fountain", FurnitureCategory.OUTDOOR, "⛲", listOf(RoomType.GARDEN), 4, 250, ItemRarity.EPIC)
  )

  fun getWallpaperById(id: String): WallpaperOption? {
    return wallpapers.firstOrNull { it.id == id } ?: wallpapers.firstOrNull()
  }

  fun getFloorById(id: String): FloorOption? {
    return floorings.firstOrNull { it.id == id } ?: floorings.firstOrNull()
  }
}
