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
  modifier: Modifier = Modifier
) {
  val xpProgress = (xp.toFloat() / maxXp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
  val animatedXp by animateFloatAsState(targetValue = xpProgress, label = "XpProgress")

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left: Level Badge & XP bar
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .shadow(6.dp, CircleShape)
          .clip(CircleShape)
          .background(SleekPrimary)
          .border(4.dp, SleekPrimaryContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$level",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = "MOCHI EXPLORER",
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold,
          color = SleekPrimary,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
          modifier = Modifier
            .width(90.dp)
            .height(8.dp)
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

    // Right: Dress-Up Studio, Daily Rewards & Coins Badge
    Row(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .testTag("customization_studio_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenCustomization() }
          .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "👗", fontSize = 16.sp)
      }

      Box(
        modifier = Modifier
          .testTag("daily_reward_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenDailyRewards() }
          .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(text = "🎁", fontSize = 16.sp)
      }

      Row(
        modifier = Modifier
          .testTag("shop_coins_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable { onOpenShop() }
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "✨",
          fontSize = 16.sp,
          color = CoinAmber
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = String.format("%,d", coins),
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = SleekTextDark
        )
      }
    }
  }
}
