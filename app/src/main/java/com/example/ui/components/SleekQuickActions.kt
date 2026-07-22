package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoomType
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer

@Composable
fun SleekQuickActions(
  currentRoom: RoomType,
  onLeftAction: () -> Unit,
  onCenterAction: () -> Unit,
  onRightAction: () -> Unit,
  modifier: Modifier = Modifier
) {
  val (leftIcon, centerIcon, rightIcon) = when (currentRoom) {
    RoomType.KITCHEN -> Triple("🍎", "🍳", "🍰")
    RoomType.BATH -> Triple("🧼", "🛁", "🧴")
    RoomType.PLAY -> Triple("🍎", "⚽", "🧤")
    RoomType.SLEEP -> Triple("🌙", "🛏️", "🎶")
    RoomType.GARDEN -> Triple("🌱", "🌳", "🦋")
  }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left Action Button (64dp x 64dp)
    Box(
      modifier = Modifier
        .testTag("action_left_button")
        .size(64.dp)
        .shadow(6.dp, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(Color.White)
        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
        .clickable { onLeftAction() },
      contentAlignment = Alignment.Center
    ) {
      Text(text = leftIcon, fontSize = 28.sp)
    }

    // Center Main Hero Button (80dp x 80dp)
    Box(
      modifier = Modifier
        .testTag("action_center_hero_button")
        .padding(horizontal = 16.dp)
        .size(80.dp)
        .shadow(12.dp, RoundedCornerShape(32.dp))
        .clip(RoundedCornerShape(32.dp))
        .background(SleekPrimary)
        .border(4.dp, SleekPrimaryContainer, RoundedCornerShape(32.dp))
        .clickable { onCenterAction() },
      contentAlignment = Alignment.Center
    ) {
      Text(text = centerIcon, fontSize = 36.sp)
    }

    // Right Action Button (64dp x 64dp)
    Box(
      modifier = Modifier
        .testTag("action_right_button")
        .size(64.dp)
        .shadow(6.dp, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(Color.White)
        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
        .clickable { onRightAction() },
      contentAlignment = Alignment.Center
    ) {
      Text(text = rightIcon, fontSize = 28.sp)
    }
  }
}
