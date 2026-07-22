package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameSession
import com.example.data.GameSessionStatus
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import kotlinx.coroutines.delay
import kotlin.random.Random

// --- 1. BUBBLE BURST GAME ENGINE ---
@Composable
fun BubbleBurstEngine(
  session: GameSession,
  onUpdateScore: (pointsDelta: Int, hitSuccess: Boolean) -> Unit,
  onComplete: (finalScore: Int) -> Unit
) {
  val bubbles = remember { mutableStateListOf<BubbleObject>() }
  var timeRemaining by remember { mutableIntStateOf(session.timeRemainingSeconds) }

  LaunchedEffect(session.status) {
    if (session.status == GameSessionStatus.RUNNING) {
      if (bubbles.isEmpty()) {
        repeat(7) {
          bubbles.add(
            BubbleObject(
              id = it,
              x = Random.nextFloat() * 240f,
              y = Random.nextFloat() * 200f,
              icon = listOf("🫧", "🧼", "✨", "🫧", "💎").random(),
              points = 10
            )
          )
        }
      }

      while (timeRemaining > 0 && session.status == GameSessionStatus.RUNNING) {
        delay(1000)
        timeRemaining--
        // Drifting animation tick
        bubbles.forEachIndexed { idx, b ->
          val newY = (b.y - Random.nextFloat() * 15f)
          val resetY = if (newY < 10f) 220f else newY
          bubbles[idx] = b.copy(y = resetY)
        }
      }

      if (timeRemaining <= 0) {
        onComplete(session.currentScore)
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))))
  ) {
    bubbles.forEachIndexed { index, bubble ->
      Box(
        modifier = Modifier
          .offset(x = bubble.x.dp, y = bubble.y.dp)
          .size(54.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.85f))
          .border(2.dp, Color(0xFFA5F3FC), CircleShape)
          .clickable {
            if (session.status == GameSessionStatus.RUNNING) {
              onUpdateScore(bubble.points, true)
              com.example.util.SoundManager.playTapSound()
              // Respawn bubble
              bubbles[index] = bubble.copy(
                x = Random.nextFloat() * 240f,
                y = 220f + Random.nextFloat() * 40f
              )
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Text(bubble.icon, fontSize = 24.sp)
      }
    }
  }
}

data class BubbleObject(val id: Int, val x: Float, val y: Float, val icon: String, val points: Int)

// --- 2. FRUIT CATCH GAME ENGINE ---
@Composable
fun FruitCatchEngine(
  session: GameSession,
  onUpdateScore: (pointsDelta: Int, hitSuccess: Boolean) -> Unit,
  onComplete: (finalScore: Int) -> Unit
) {
  var basketX by remember { mutableStateOf(120f) }
  val fruits = remember { mutableStateListOf<FallingFruit>() }
  var timeRemaining by remember { mutableIntStateOf(session.timeRemainingSeconds) }

  LaunchedEffect(session.status) {
    if (session.status == GameSessionStatus.RUNNING) {
      if (fruits.isEmpty()) {
        repeat(5) { id ->
          fruits.add(
            FallingFruit(
              id = id,
              x = Random.nextFloat() * 250f,
              y = Random.nextFloat() * -150f,
              icon = listOf("🍎", "🫐", "🍌", "🍓", "💣").random()
            )
          )
        }
      }

      while (timeRemaining > 0 && session.status == GameSessionStatus.RUNNING) {
        delay(100)
        timeRemaining = (timeRemaining - 0.1f).coerceAtLeast(0f).toInt()

        fruits.forEachIndexed { index, fruit ->
          val newY = fruit.y + 12f
          // Check collision with basket (y around 220dp)
          if (newY >= 210f && newY <= 240f && kotlin.math.abs(fruit.x - basketX) < 45f) {
            if (fruit.icon == "💣") {
              onUpdateScore(-15, false)
              com.example.util.SoundManager.playErrorSound()
            } else {
              onUpdateScore(15, true)
              com.example.util.SoundManager.playEatingSound()
            }
            // Respawn fruit
            fruits[index] = fruit.copy(
              x = Random.nextFloat() * 250f,
              y = -40f,
              icon = listOf("🍎", "🫐", "🍌", "🍓", "💣").random()
            )
          } else if (newY > 260f) {
            // Missed item
            fruits[index] = fruit.copy(
              x = Random.nextFloat() * 250f,
              y = -40f,
              icon = listOf("🍎", "🫐", "🍌", "🍓", "💣").random()
            )
          } else {
            fruits[index] = fruit.copy(y = newY)
          }
        }
      }

      if (timeRemaining <= 0) {
        onComplete(session.currentScore)
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF047857))))
  ) {
    // Falling fruits
    fruits.forEach { fruit ->
      Text(
        text = fruit.icon,
        fontSize = 28.sp,
        modifier = Modifier.offset(x = fruit.x.dp, y = fruit.y.dp)
      )
    }

    // Basket at bottom
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .offset(x = basketX.dp, y = (-20).dp)
        .size(60.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFFF59E0B))
        .clickable {
          // Move basket horizontally across touch area
          basketX = (basketX + 60f) % 260f
        },
      contentAlignment = Alignment.Center
    ) {
      Text("🧺", fontSize = 32.sp)
    }
  }
}

data class FallingFruit(val id: Int, val x: Float, val y: Float, val icon: String)

// --- 3. MEMORY MATCH GAME ENGINE ---
@Composable
fun MemoryMatchEngine(
  session: GameSession,
  onUpdateScore: (pointsDelta: Int, hitSuccess: Boolean) -> Unit,
  onComplete: (finalScore: Int) -> Unit
) {
  val icons = remember { listOf("🍎", "🫐", "🥛", "🍰", "🍎", "🫐", "🥛", "🍰").shuffled() }
  val flipped = remember { mutableStateListOf(*Array(8) { false }) }
  val matched = remember { mutableStateListOf(*Array(8) { false }) }
  var selectedIndex by remember { mutableStateOf<Int?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF1E1B4B))
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text("Match Snacks 🃏", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Spacer(modifier = Modifier.height(12.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(200.dp)
      ) {
        itemsIndexed(icons) { index, icon ->
          val isFlipped = flipped[index] || matched[index]
          Box(
            modifier = Modifier
              .size(60.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isFlipped) Color(0xFF6366F1) else Color(0xFF312E81))
              .border(2.dp, if (matched[index]) Color(0xFF10B981) else Color(0xFF4338CA), RoundedCornerShape(14.dp))
              .clickable {
                if (!isFlipped && session.status == GameSessionStatus.RUNNING) {
                  flipped[index] = true
                  com.example.util.SoundManager.playTapSound()
                  val firstIndex = selectedIndex
                  if (firstIndex == null) {
                    selectedIndex = index
                  } else {
                    if (icons[firstIndex] == icons[index]) {
                      matched[firstIndex] = true
                      matched[index] = true
                      onUpdateScore(30, true)
                      com.example.util.SoundManager.playComboSound()
                      selectedIndex = null

                      if (matched.all { it }) {
                        onComplete(session.currentScore + 30)
                      }
                    } else {
                      onUpdateScore(-5, false)
                      com.example.util.SoundManager.playErrorSound()
                      // Flip back after short delay
                      flipped[firstIndex] = false
                      flipped[index] = false
                      selectedIndex = null
                    }
                  }
                }
              },
            contentAlignment = Alignment.Center
          ) {
            Text(if (isFlipped) icon else "❓", fontSize = 24.sp)
          }
        }
      }
    }
  }
}

// --- 4. AGILITY COURSE RHYTHM ENGINE ---
@Composable
fun AgilityCourseEngine(
  session: GameSession,
  onUpdateScore: (pointsDelta: Int, hitSuccess: Boolean) -> Unit,
  onComplete: (finalScore: Int) -> Unit
) {
  var activeStep by remember { mutableIntStateOf(0) }
  val targetSteps = remember { listOf("🏃 Jump!", "🪨 Dodge!", "🪜 Climb!", "💨 Sprint!") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))))
      .padding(20.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text("Agility Routine 🏃", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
      Spacer(modifier = Modifier.height(16.dp))

      Box(
        modifier = Modifier
          .size(110.dp)
          .clip(CircleShape)
          .background(Color.White)
          .border(4.dp, Color(0xFFF43F5E), CircleShape)
          .clickable {
            if (session.status == GameSessionStatus.RUNNING) {
              onUpdateScore(20, true)
              com.example.util.SoundManager.playTapSound()
              activeStep = (activeStep + 1) % targetSteps.size
              if (session.currentScore >= 120) {
                onComplete(session.currentScore + 20)
              }
            }
          },
        contentAlignment = Alignment.Center
      ) {
        Text(targetSteps[activeStep], fontSize = 16.sp, fontWeight = FontWeight.Black, color = SleekTextDark)
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text("Tap fast to command Mochi through hurdles!", fontSize = 12.sp, color = Color.White)
    }
  }
}
