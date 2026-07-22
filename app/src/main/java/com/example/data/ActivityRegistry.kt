package com.example.data

object ActivityRegistry {

  val activities: List<ActivityDefinition> = listOf(
    // --- MINI GAMES ---
    ActivityDefinition(
      id = "bubble",
      displayName = "Bubble Burst",
      icon = "🫧",
      description = "Pop glowing bubbles before they float away!",
      category = ActivityCategory.MINI_GAMES,
      difficulty = GameDifficulty.EASY,
      unlockLevel = 1,
      baseCoinReward = 50,
      baseXpReward = 20,
      durationSeconds = 25,
      tags = listOf("arcade", "pop", "tap")
    ),
    ActivityDefinition(
      id = "fruit",
      displayName = "Fruit Catch",
      icon = "🍎",
      description = "Catch falling apples & berries into your basket!",
      category = ActivityCategory.MINI_GAMES,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 2,
      baseCoinReward = 70,
      baseXpReward = 30,
      durationSeconds = 30,
      tags = listOf("arcade", "catch", "action")
    ),
    ActivityDefinition(
      id = "memory",
      displayName = "Memory Match",
      icon = "🃏",
      description = "Match pairs of pet snacks to test your brain!",
      category = ActivityCategory.MINI_GAMES,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 3,
      baseCoinReward = 65,
      baseXpReward = 25,
      durationSeconds = 30,
      tags = listOf("puzzle", "memory")
    ),
    ActivityDefinition(
      id = "color",
      displayName = "Color Pop",
      icon = "🌈",
      description = "Tap matching colored jelly dots in fast sequence!",
      category = ActivityCategory.MINI_GAMES,
      difficulty = GameDifficulty.EASY,
      unlockLevel = 4,
      baseCoinReward = 55,
      baseXpReward = 22,
      durationSeconds = 20,
      tags = listOf("arcade", "color")
    ),
    ActivityDefinition(
      id = "sky",
      displayName = "Sky Jump",
      icon = "🚀",
      description = "Bounce Mochi up fluffy clouds into space!",
      category = ActivityCategory.MINI_GAMES,
      difficulty = GameDifficulty.HARD,
      unlockLevel = 5,
      baseCoinReward = 90,
      baseXpReward = 40,
      durationSeconds = 35,
      tags = listOf("jump", "arcade")
    ),
    ActivityDefinition(
      id = "firefly",
      displayName = "Firefly Chase",
      icon = "✨",
      description = "Tap glowing fireflies in the twilight garden!",
      category = ActivityCategory.MINI_GAMES,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 6,
      baseCoinReward = 75,
      baseXpReward = 35,
      durationSeconds = 30,
      tags = listOf("garden", "relaxing")
    ),

    // --- PET ACTIVITIES ---
    ActivityDefinition(
      id = "agility",
      displayName = "Agility Obstacle Course",
      icon = "🏃",
      description = "Guide Mochi through hurdles & balance beams!",
      category = ActivityCategory.PET_ACTIVITIES,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 2,
      baseCoinReward = 80,
      baseXpReward = 35,
      durationSeconds = 30,
      tags = listOf("sport", "agility")
    ),
    ActivityDefinition(
      id = "hide_seek",
      displayName = "Hide & Seek",
      icon = "🙈",
      description = "Find where Mochi is hiding behind room props!",
      category = ActivityCategory.PET_ACTIVITIES,
      difficulty = GameDifficulty.EASY,
      unlockLevel = 3,
      baseCoinReward = 60,
      baseXpReward = 25,
      durationSeconds = 25,
      tags = listOf("bonding", "fun")
    ),
    ActivityDefinition(
      id = "tug_war",
      displayName = "Tug of War",
      icon = "🪢",
      description = "Rhythmically tap to pull the rope against Mochi!",
      category = ActivityCategory.PET_ACTIVITIES,
      difficulty = GameDifficulty.HARD,
      unlockLevel = 4,
      baseCoinReward = 85,
      baseXpReward = 40,
      durationSeconds = 20,
      tags = listOf("strength", "play")
    ),

    // --- TRAINING ACTIVITIES ---
    ActivityDefinition(
      id = "trick_master",
      displayName = "Trick Academy",
      icon = "🎪",
      description = "Teach Mochi backflips, high-fives & wave gestures!",
      category = ActivityCategory.TRAINING,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 3,
      baseCoinReward = 75,
      baseXpReward = 45,
      durationSeconds = 30,
      tags = listOf("trick", "skills")
    ),
    ActivityDefinition(
      id = "potty_train",
      displayName = "Potty Routine",
      icon = "🚽",
      description = "Master proper bathroom habits with positive rewards!",
      category = ActivityCategory.TRAINING,
      difficulty = GameDifficulty.EASY,
      unlockLevel = 1,
      baseCoinReward = 50,
      baseXpReward = 30,
      durationSeconds = 20,
      tags = listOf("hygiene", "routine")
    ),

    // --- EDUCATIONAL ---
    ActivityDefinition(
      id = "snack_math",
      displayName = "Snack Math Quiz",
      icon = "🧮",
      description = "Solve fun snack count puzzles to feed Mochi!",
      category = ActivityCategory.EDUCATIONAL,
      difficulty = GameDifficulty.EASY,
      unlockLevel = 2,
      baseCoinReward = 70,
      baseXpReward = 35,
      durationSeconds = 30,
      tags = listOf("math", "brain")
    ),
    ActivityDefinition(
      id = "word_puzzle",
      displayName = "Pet Word Scramble",
      icon = "🧩",
      description = "Unscramble pet food & toy names for bonus gems!",
      category = ActivityCategory.EDUCATIONAL,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 4,
      baseCoinReward = 90,
      baseXpReward = 50,
      durationSeconds = 35,
      tags = listOf("words", "brain")
    ),

    // --- SEASONAL EVENTS ---
    ActivityDefinition(
      id = "water_splash",
      displayName = "Summer Water Splash 🌊",
      icon = "🌊",
      description = "Dodge water balloons in the summer garden party!",
      category = ActivityCategory.SEASONAL_EVENTS,
      difficulty = GameDifficulty.NORMAL,
      unlockLevel = 1,
      baseCoinReward = 100,
      baseXpReward = 50,
      durationSeconds = 30,
      tags = listOf("summer", "event")
    ),
    ActivityDefinition(
      id = "candy_hunt",
      displayName = "Halloween Candy Hunt 🎃",
      icon = "🎃",
      description = "Collect spooky pumpkins & glowing candy buckets!",
      category = ActivityCategory.SEASONAL_EVENTS,
      difficulty = GameDifficulty.HARD,
      unlockLevel = 1,
      baseCoinReward = 120,
      baseXpReward = 60,
      durationSeconds = 30,
      tags = listOf("halloween", "spooky")
    ),

    // --- SPECIAL CHALLENGES ---
    ActivityDefinition(
      id = "speed_blitz",
      displayName = "60s Speed Tap Blitz",
      icon = "⚡",
      description = "Ultra high-speed tap frenzy to set a world high score!",
      category = ActivityCategory.SPECIAL_CHALLENGES,
      difficulty = GameDifficulty.ENDLESS,
      unlockLevel = 5,
      baseCoinReward = 150,
      baseXpReward = 75,
      durationSeconds = 60,
      tags = listOf("speed", "blitz")
    ),
    ActivityDefinition(
      id = "flawless_combo",
      displayName = "Flawless Combo Streak",
      icon = "🔥",
      description = "Maintain a 20x combo multiplier without missing a single beat!",
      category = ActivityCategory.SPECIAL_CHALLENGES,
      difficulty = GameDifficulty.HARD,
      unlockLevel = 6,
      baseCoinReward = 180,
      baseXpReward = 90,
      durationSeconds = 40,
      tags = listOf("combo", "master")
    )
  )

  fun getActivityById(id: String): ActivityDefinition? {
    return activities.find { it.id == id }
  }

  fun getActivitiesByCategory(category: ActivityCategory): List<ActivityDefinition> {
    return activities.filter { it.category == category }
  }
}
