package com.example.data

enum class CareActionType {
  FEED, WATER, SLEEP, WAKE, BATH, BRUSH, MEDICINE, PET_HUG, PLAY, PRAISE
}

data class CareAction(
  val id: String,
  val type: CareActionType,
  val name: String,
  val icon: String,
  val room: RoomType,
  val cooldownSec: Int = 3,
  val description: String,
  val statBoostText: String
)

data class FloatingStatPopup(
  val id: Long = System.currentTimeMillis(),
  val text: String,
  val colorHex: Long = 0xFF10B981
)

object CareActionRegistry {
  val actionsByRoom = mapOf(
    RoomType.SLEEP to listOf(
      CareAction("sleep", CareActionType.SLEEP, "Sleep / Wake", "😴", RoomType.SLEEP, 3, "Rest Mochi in a warm bed", "+Energy Zzz"),
      CareAction("praise_sleep", CareActionType.PRAISE, "Lullaby", "🎵", RoomType.SLEEP, 4, "Sing a gentle lullaby", "+15 Joy • +10 Friendship")
    ),
    RoomType.KITCHEN to listOf(
      CareAction("feed", CareActionType.FEED, "Feed Snack", "🍎", RoomType.KITCHEN, 3, "Feed delicious fruit snack", "+25 Hunger • +5 XP"),
      CareAction("water", CareActionType.WATER, "Give Water", "💧", RoomType.KITCHEN, 3, "Provide fresh cool water", "+15 Hunger • +10 Energy")
    ),
    RoomType.BATH to listOf(
      CareAction("bath", CareActionType.BATH, "Bubble Bath", "🛁", RoomType.BATH, 4, "Wash Mochi with warm bubbles", "+35 Clean • +10 Joy"),
      CareAction("brush", CareActionType.BRUSH, "Brush Fur", "🪮", RoomType.BATH, 3, "Groom fur to make it fluffy", "+20 Clean • +10 Friendship")
    ),
    RoomType.PLAY to listOf(
      CareAction("play", CareActionType.PLAY, "Play Game", "⚽", RoomType.PLAY, 3, "Play bouncy ball together", "+30 Joy • +15 XP"),
      CareAction("hug", CareActionType.PET_HUG, "Cuddle & Hug", "🫂", RoomType.PLAY, 3, "Give warm loving hugs", "+20 Friendship • +15 Joy")
    ),
    RoomType.GARDEN to listOf(
      CareAction("praise", CareActionType.PRAISE, "Praise Mochi", "🌟", RoomType.GARDEN, 3, "Give sweet words of praise", "+25 Joy • +15 Friendship"),
      CareAction("medicine", CareActionType.MEDICINE, "Give Medicine", "🧪", RoomType.GARDEN, 5, "Nourish with healing vitamins", "+30 Health • +15 Energy")
    )
  )
}
