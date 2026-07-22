package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@Composable
fun SleekTopBar(
  level: Int,
  xp: Int,
  maxXp: Int,
  coins: Int,
  onOpenShop: () -> Unit,
  onOpenDailyRewards: () -> Unit = {},
  onOpenCustomization: () -> Unit = {},
  onOpenRoomDecoration: () -> Unit = {},
  onOpenPlayerProfile: () -> Unit = {},
  onOpenAchievements: () -> Unit = {},
  onOpenDailyTasks: () -> Unit = {},
  onOpenCollections: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val xpProgress = (xp.toFloat() / maxXp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
  val animatedXp by animateFloatAsState(targetValue = xpProgress, label = "XpProgress")

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left: Level Badge & XP bar (Clicking level badge opens Player Profile)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .clickable { onOpenPlayerProfile() }
        .padding(end = 4.dp)
        .testTag("player_level_badge_button")
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .shadow(6.dp, CircleShape)
          .clip(CircleShape)
          .background(SleekPrimary)
          .border(3.dp, SleekPrimaryContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$level",
          color = Color.White,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Text(
          text = "MOCHI EXPLORER",
          fontSize = 10.sp,
          fontWeight = FontWeight.ExtraBold,
          color = SleekPrimary,
          letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Box(
          modifier = Modifier
            .width(72.dp)
            .height(7.dp)
            .clip(CircleShape)
            .background(Color(0xFFE2E8F0))
        ) {
          Box(
            modifier = Modifier
              .fillMaxHeight()
              .fillMaxWidth(animatedXp)
              .clip(CircleShape)
              .background(SleekPrimary)
          )
        }
      }
    }

    // Right: Action icon triggers
    Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .testTag("trophies_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenAchievements() }
          .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "🏆", fontSize = 14.sp)
      }

      Box(
        modifier = Modifier
          .testTag("daily_tasks_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenDailyTasks() }
          .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "📜", fontSize = 14.sp)
      }

      Box(
        modifier = Modifier
          .testTag("collections_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenCollections() }
          .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "📚", fontSize = 14.sp)
      }

      Box(
        modifier = Modifier
          .testTag("customization_studio_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenCustomization() }
          .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "👗", fontSize = 14.sp)
      }

      Box(
        modifier = Modifier
          .testTag("room_decoration_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenRoomDecoration() }
          .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "🛋️", fontSize = 14.sp)
      }

      Row(
        modifier = Modifier
          .testTag("shop_coins_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenShop() }
          .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "✨",
          fontSize = 14.sp,
          color = CoinAmber
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
          text = String.format("%,d", coins),
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = SleekTextDark
        )
      }
    }
  }
}
