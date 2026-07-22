package com.example.data

enum class ActivityCategory(val displayName: String, val icon: String, val description: String) {
  MINI_GAMES("Mini-Games", "🎮", "Fun arcade games to earn coins & XP"),
  PET_ACTIVITIES("Pet Activities", "⚽", "Interactive bonding games with Mochi"),
  TRAINING("Training", "🎪", "Teach tricks & agility routines"),
  EDUCATIONAL("Educational", "🧠", "Brain-teasing quizzes & word games"),
  SEASONAL_EVENTS("Seasonal Events", "🎃", "Limited-time festive challenges"),
  SPECIAL_CHALLENGES("Special Challenges", "⚡", "High-speed score attack trials")
}

enum class GameDifficulty(val displayName: String, val icon: String, val rewardMultiplier: Float) {
  EASY("Easy", "🟢", 1.0f),
  NORMAL("Normal", "🟡", 1.5f),
  HARD("Hard", "🔴", 2.0f),
  ENDLESS("Endless", "♾️", 2.5f)
}

data class ActivityDefinition(
  val id: String,
  val displayName: String,
  val icon: String,
  val description: String,
  val category: ActivityCategory,
  val difficulty: GameDifficulty = GameDifficulty.NORMAL,
  val unlockLevel: Int = 1,
  val baseCoinReward: Int = 50,
  val baseXpReward: Int = 20,
  val durationSeconds: Int = 30,
  val tags: List<String> = emptyList()
)

enum class GameSessionStatus {
  IDLE, COUNTDOWN, RUNNING, PAUSED, COMPLETED, FAILED
}

data class GameSession(
  val activityId: String,
  val activityName: String,
  val icon: String,
  val difficulty: GameDifficulty,
  val currentScore: Int = 0,
  val highScore: Int = 0,
  val combo: Int = 0,
  val maxCombo: Int = 0,
  val multiplier: Float = 1.0f,
  val accuracy: Float = 100f,
  val timeRemainingSeconds: Int = 30,
  val maxTimeSeconds: Int = 30,
  val coinsEarned: Int = 0,
  val xpEarned: Int = 0,
  val starsEarned: Int = 0,
  val status: GameSessionStatus = GameSessionStatus.IDLE,
  val isNewHighScore: Boolean = false
)

sealed class ActivityEvent {
  data class GameStarted(val gameId: String, val difficulty: GameDifficulty) : ActivityEvent()
  data class GamePaused(val gameId: String) : ActivityEvent()
  data class GameResumed(val gameId: String) : ActivityEvent()
  data class GameCompleted(
    val gameId: String,
    val finalScore: Int,
    val stars: Int,
    val coins: Int,
    val xp: Int,
    val isHighScore: Boolean
  ) : ActivityEvent()
  data class GameFailed(val gameId: String, val score: Int) : ActivityEvent()
  data class RewardGranted(val coins: Int, val xp: Int) : ActivityEvent()
  data class HighScoreUpdated(val gameId: String, val newHighScore: Int) : ActivityEvent()
}
