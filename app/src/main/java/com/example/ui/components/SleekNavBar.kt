package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoomType
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekTextMuted

@Composable
fun SleekNavBar(
  currentRoom: RoomType,
  onRoomSelect: (RoomType) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.White)
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .navigationBarsPadding(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    RoomType.entries.forEach { room ->
      val isSelected = room == currentRoom

      if (isSelected) {
        // Active Pill Capsule
        Box(
          modifier = Modifier
            .testTag("nav_tab_${room.name.lowercase()}")
            .clip(RoundedCornerShape(20.dp))
            .background(SleekPrimaryContainer)
            .clickable { onRoomSelect(room) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = room.icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = room.displayName,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = SleekPrimary
            )
          }
        }
      } else {
        // Unselected Tab
        Column(
          modifier = Modifier
            .testTag("nav_tab_${room.name.lowercase()}")
            .clickable { onRoomSelect(room) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(text = room.icon, fontSize = 22.sp)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = room.displayName,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = SleekTextMuted
          )
        }
      }
    }
  }
}
