package com.example.ui.components

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import kotlinx.coroutines.delay
import kotlin.random.Random

data class MiniGameInfo(
  val id: String,
  val title: String,
  val icon: String,
  val description: String,
  val rewardCoins: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniGameHubSheet(
  activeGameId: String?,
  onDismiss: () -> Unit,
  onOpenGame: (String) -> Unit,
  onGameWin: (coinsEarned: Int, xpEarned: Int) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val gameList = remember {
    listOf(
      MiniGameInfo("bubble", "Bubble Burst 🧼", "🫧", "Pop glowing bubbles before they float away!", 50),
      MiniGameInfo("memory", "Memory Match 🃏", "🧠", "Match pairs of pet snacks to test your brain!", 60),
      MiniGameInfo("fruit", "Fruit Catch 🍎", "🧺", "Catch falling apples and berries with your basket!", 70),
      MiniGameInfo("color", "Color Pop 🌈", "🎨", "Tap matching colored jelly dots!", 45),
      MiniGameInfo("sky", "Sky Jump ☁️", "🚀", "Bounce Mochi up fluffy clouds into space!", 80),
      MiniGameInfo("firefly", "Firefly Chase ✨", "🦋", "Tap glowing fireflies in the twilight garden!", 65)
    )
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Mini-Games Arcade 🎮",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextDark
          )
          Text(
            text = "Play games to earn coins & XP for Mochi!",
            fontSize = 13.sp,
            color = SleekTextMuted
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SleekTextDark)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (activeGameId == null) {
        // Grid Selection
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.height(380.dp)
        ) {
          items(gameList) { game ->
            Column(
              modifier = Modifier
                .testTag("game_item_${game.id}")
                .shadow(2.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                .clickable { onOpenGame(game.id) }
                .padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = game.icon, fontSize = 36.sp)
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = game.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Earn up to ✨ ${game.rewardCoins}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CoinAmber
              )
            }
          }
        }
      } else {
        // Active Interactive Mini-Game View
        when (activeGameId) {
          "bubble" -> BubbleBurstGame(onWin = { coins, xp -> onGameWin(coins, xp) })
          "memory" -> MemoryMatchGame(onWin = { coins, xp -> onGameWin(coins, xp) })
          else -> GenericQuickGame(
            gameName = gameList.find { it.id == activeGameId }?.title ?: "Mini-Game",
            onWin = { coins, xp -> onGameWin(coins, xp) }
          )
        }
      }
    }
  }
}

@Composable
private fun BubbleBurstGame(onWin: (Int, Int) -> Unit) {
  var score by remember { mutableIntStateOf(0) }
  var timeLeft by remember { mutableIntStateOf(15) }
  val bubbles = remember { mutableStateListOf<Offset>() }

  LaunchedEffect(Unit) {
    repeat(8) {
      bubbles.add(Offset(Random.nextFloat() * 260f, Random.nextFloat() * 200f))
    }
    while (timeLeft > 0) {
      delay(1000)
      timeLeft--
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(360.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(Color(0xFFECFEFF))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text("Score: $score 🫧", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SleekTextDark)
      Text("Time: ${timeLeft}s ⏰", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SleekPrimary)
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (timeLeft > 0) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(RoundedCornerShape(20.dp))
          .background(Color.White)
      ) {
        bubbles.forEachIndexed { index, pos ->
          Box(
            modifier = Modifier
              .offset(x = pos.x.dp, y = pos.y.dp)
              .size(54.dp)
              .clip(CircleShape)
              .background(Color(0xFFA5F3FC))
              .border(2.dp, Color(0xFF06B6D4), CircleShape)
              .clickable {
                score += 10
                bubbles[index] = Offset(Random.nextFloat() * 240f, Random.nextFloat() * 180f)
              },
            contentAlignment = Alignment.Center
          ) {
            Text("🫧", fontSize = 24.sp)
          }
        }
      }
    } else {
      val rewardCoins = (score / 2).coerceAtLeast(15)
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("Game Over! 🎉", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
        Text("Total Score: $score", fontSize = 16.sp, color = SleekTextMuted)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = { onWin(rewardCoins, 25) },
          colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
        ) {
          Text("Claim ✨ $rewardCoins Coins & 25 XP")
        }
      }
    }
  }
}

@Composable
private fun MemoryMatchGame(onWin: (Int, Int) -> Unit) {
  var score by remember { mutableIntStateOf(0) }
  var matchesFound by remember { mutableIntStateOf(0) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(360.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(Color(0xFFF8FAFC))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text("Memory Match Game 🃏", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
    Spacer(modifier = Modifier.height(12.dp))
    Text("Tap cards to flip & match treats!", fontSize = 14.sp, color = SleekTextMuted)
    Spacer(modifier = Modifier.height(20.dp))
    Button(
      onClick = { onWin(60, 30) },
      colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
    ) {
      Text("Complete Level & Claim ✨ 60 Coins")
    }
  }
}

@Composable
private fun GenericQuickGame(gameName: String, onWin: (Int, Int) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(360.dp)
      .clip(RoundedCornerShape(24.dp))
      .background(Color(0xFFF1F5F9))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(gameName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
    Spacer(modifier = Modifier.height(8.dp))
    Text("Tap to play and win rewards!", fontSize = 14.sp, color = SleekTextMuted)
    Spacer(modifier = Modifier.height(24.dp))
    Button(
      onClick = { onWin(50, 20) },
      colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
    ) {
      Text("Play & Claim Rewards ✨")
    }
  }
}
