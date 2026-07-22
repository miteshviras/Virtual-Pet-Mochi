package com.example.data

import androidx.compose.ui.graphics.Color

data class InteractiveProp(
  val id: String,
  val name: String,
  val icon: String,
  val xFraction: Float, // Relative X position inside room (0.1 to 0.9)
  val yFraction: Float, // Relative Y position inside room (0.1 to 0.9)
  val description: String,
  val actionFeedback: String,
  val statBonusType: StatType? = null,
  val statBonusAmount: Float = 0f,
  val coinReward: Int = 0
)

data class RoomConfig(
  val type: RoomType,
  val ambientAudioName: String,
  val gradientTop: Color,
  val gradientBottom: Color,
  val props: List<InteractiveProp>
)

object RoomRegistry {
  val rooms = mapOf(
    RoomType.SLEEP to RoomConfig(
      type = RoomType.SLEEP,
      ambientAudioName = "Lullaby Ambient 🌙",
      gradientTop = Color(0xFF1E1B4B),
      gradientBottom = Color(0xFF312E81),
      props = listOf(
        InteractiveProp("bed", "Cozy Bed", "🛏️", 0.22f, 0.65f, "A warm starry bed for Mochi.", "Mochi feels well rested!", StatType.ENERGY, 20f),
        InteractiveProp("lamp", "Night Lamp", "💡", 0.78f, 0.45f, "Toggle cozy room lighting.", "Click! Soft glow filled the room ✨"),
        InteractiveProp("window", "Moon Window", "🌙", 0.50f, 0.22f, "Gaze out at the magical night sky.", "A bright shooting star flew by! ⭐", StatType.HAPPINESS, 10f, coinReward = 5),
        InteractiveProp("toy_box", "Chest", "📦", 0.82f, 0.75f, "Open to store Mochi's trinkets.", "Click! Toy box opened 🎁")
      )
    ),
    RoomType.KITCHEN to RoomConfig(
      type = RoomType.KITCHEN,
      ambientAudioName = "Cooking Sounds 🍳",
      gradientTop = Color(0xFFFFF7ED),
      gradientBottom = Color(0xFFFED7AA),
      props = listOf(
        InteractiveProp("fridge", "Refrigerator", "🧊", 0.18f, 0.40f, "Contains fresh snacks and drinks.", "Found a free strawberry snack! 🍓", StatType.HUNGER, 15f, coinReward = 2),
        InteractiveProp("table", "Dining Table", "🍽️", 0.50f, 0.70f, "Clean table to enjoy meals.", "Table wiped sparkling clean! ✨"),
        InteractiveProp("pantry", "Food Shelf", "🌾", 0.82f, 0.38f, "Organized baking supplies.", "Fresh flour and honey restocked!")
      )
    ),
    RoomType.BATH to RoomConfig(
      type = RoomType.BATH,
      ambientAudioName = "Water Bubbles 🛁",
      gradientTop = Color(0xFFECFEFF),
      gradientBottom = Color(0xFFA5F3FC),
      props = listOf(
        InteractiveProp("bathtub", "Bubble Tub", "🛁", 0.30f, 0.65f, "Warm bath tub full of bubbles.", "Splash! Bubbles everywhere! 🧼", StatType.CLEANLINESS, 25f),
        InteractiveProp("sink", "Mirror & Sink", "🪞", 0.75f, 0.42f, "Sparkling mirror for grooming.", "Mochi admires its cute reflection! 😊", StatType.FRIENDSHIP, 10f),
        InteractiveProp("soap_shelf", "Shampoo Shelf", "🧴", 0.85f, 0.72f, "Organic sweet lavender soap.", "Fresh lavender scent! 🌸")
      )
    ),
    RoomType.PLAY to RoomConfig(
      type = RoomType.PLAY,
      ambientAudioName = "Cheerful Playground 🧸",
      gradientTop = Color(0xFFEFF6FF),
      gradientBottom = Color(0xFFC7D2FE),
      props = listOf(
        InteractiveProp("ball", "Bouncy Ball", "⚽", 0.20f, 0.72f, "Fun sports ball to kick around.", "Boing! Bounced around the playroom! ⚽", StatType.HAPPINESS, 20f, coinReward = 5),
        InteractiveProp("book", "Fairy Tale Book", "📚", 0.50f, 0.35f, "Magical picture book.", "Read a wonderful adventure story! 📖", StatType.XP, 15f),
        InteractiveProp("puzzle", "Puzzle Table", "🧩", 0.80f, 0.68f, "Jigsaw puzzle desk.", "Puzzle piece snapped in place! 🧩", coinReward = 10)
      )
    ),
    RoomType.GARDEN to RoomConfig(
      type = RoomType.GARDEN,
      ambientAudioName = "Birds & Breeze 🌳",
      gradientTop = Color(0xFFF7FEE7),
      gradientBottom = Color(0xFFA3E635),
      props = listOf(
        InteractiveProp("magic_tree", "Magic Apple Tree", "🌳", 0.25f, 0.45f, "Shaking drops sweet apples!", "A juicy red apple fell down! 🍎", StatType.HUNGER, 15f, coinReward = 5),
        InteractiveProp("flowers", "Flower Bed", "🌸", 0.55f, 0.78f, "Colorful blooming blossoms.", "Flowers blooming with magic dust! ✨", StatType.HAPPINESS, 15f),
        InteractiveProp("bench", "Wooden Bench", "🪑", 0.82f, 0.62f, "Peaceful park bench under the sun.", "Mochi relaxes under the sunshine ☀️", StatType.ENERGY, 15f)
      )
    )
  )
}
