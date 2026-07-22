package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementDefinition
import kotlinx.coroutines.delay

@Composable
fun AchievementUnlockedBanner(
  achievement: AchievementDefinition?,
  onDismiss: () -> Unit
) {
  LaunchedEffect(achievement) {
    if (achievement != null) {
      delay(3500)
      onDismiss()
    }
  }

  AnimatedVisibility(
    visible = achievement != null,
    enter = slideInVertically(initialOffsetY = { -it }),
    exit = slideOutVertically(targetOffsetY = { -it })
  ) {
    if (achievement != null) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("achievement_unlocked_banner"),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        shadowElevation = 8.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))),
            contentAlignment = Alignment.Center
          ) {
            Text(achievement.icon, fontSize = 22.sp)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "🏆 Achievement Unlocked!",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFF59E0B)
            )
            Text(
              text = achievement.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = achievement.description,
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFEF3C7))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("+${achievement.rewardCoins}💰", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
          }
        }
      }
    }
  }
}
