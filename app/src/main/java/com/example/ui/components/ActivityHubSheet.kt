package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActivityCategory
import com.example.data.ActivityDefinition
import com.example.data.ActivityRegistry
import com.example.data.GameDifficulty
import com.example.data.GameSession
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHubSheet(
  playerLevel: Int,
  highScoresMap: Map<String, Int>,
  activeActivity: ActivityDefinition?,
  gameSession: GameSession?,
  onDismiss: () -> Unit,
  onStartActivity: (activityId: String, difficulty: GameDifficulty) -> Unit,
  onPauseGame: () -> Unit,
  onResumeGame: () -> Unit,
  onRestartGame: () -> Unit,
  onExitGame: () -> Unit,
  onUpdateScore: (pointsDelta: Int, hitSuccess: Boolean) -> Unit,
  onCompleteGame: (finalScore: Int) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedCategory by remember { mutableStateOf(ActivityCategory.MINI_GAMES) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .testTag("activity_hub_sheet")
    ) {
      // Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6)))),
            contentAlignment = Alignment.Center
          ) {
            Text("🎮", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Activity Arcade & Hub",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "Play mini-games, pet training & seasonal events!",
              fontSize = 12.sp,
              color = SleekTextMuted
            )
          }
        }

        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFFF1F5F9))
            .testTag("close_activity_hub")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (gameSession == null || activeActivity == null) {
        // --- CATEGORY TABS ROW ---
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(ActivityCategory.values()) { category ->
            val isSelected = selectedCategory == category
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) SleekPrimary else Color(0xFFF1F5F9))
                .clickable { selectedCategory = category }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(category.icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = category.displayName,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else Color(0xFF64748B)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- ACTIVITIES GRID ---
        val activityList = ActivityRegistry.getActivitiesByCategory(selectedCategory)

        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.height(340.dp)
        ) {
          items(activityList) { activity ->
            val isLocked = playerLevel < activity.unlockLevel
            val highScore = highScoresMap[activity.id] ?: 0

            ActivityCard(
              activity = activity,
              isLocked = isLocked,
              highScore = highScore,
              onPlay = { onStartActivity(activity.id, activity.difficulty) }
            )
          }
        }
      } else {
        // --- ACTIVE MINI-GAME FRAMEWORK HOST ---
        MiniGameFrameworkHost(
          session = gameSession,
          onPause = onPauseGame,
          onResume = onResumeGame,
          onRestart = onRestartGame,
          onExit = onExitGame
        ) {
          when (activeActivity.id) {
            "bubble" -> BubbleBurstEngine(
              session = gameSession,
              onUpdateScore = onUpdateScore,
              onComplete = onCompleteGame
            )
            "fruit" -> FruitCatchEngine(
              session = gameSession,
              onUpdateScore = onUpdateScore,
              onComplete = onCompleteGame
            )
            "memory" -> MemoryMatchEngine(
              session = gameSession,
              onUpdateScore = onUpdateScore,
              onComplete = onCompleteGame
            )
            else -> AgilityCourseEngine(
              session = gameSession,
              onUpdateScore = onUpdateScore,
              onComplete = onCompleteGame
            )
          }
        }
      }
    }
  }
}

@Composable
fun ActivityCard(
  activity: ActivityDefinition,
  isLocked: Boolean,
  highScore: Int,
  onPlay: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .background(if (isLocked) Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
          contentAlignment = Alignment.Center
        ) {
          Text(activity.icon, fontSize = 24.sp)
        }

        // Difficulty pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE2E8F0))
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text(
            text = activity.difficulty.displayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = activity.displayName,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SleekTextDark,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = activity.description,
        fontSize = 11.sp,
        color = Color.Gray,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (highScore > 0) "Best: $highScore 🏆" else "Earn ✨${activity.baseCoinReward}",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = CoinAmber
        )

        if (isLocked) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Lvl ${activity.unlockLevel}", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = onPlay,
            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(28.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
