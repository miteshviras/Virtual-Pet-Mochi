package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CareAction
import com.example.data.CareActionRegistry
import com.example.data.RoomType
import com.example.ui.theme.SleekTextDark

@Composable
fun CareActionsPanel(
  currentRoom: RoomType,
  cooldowns: Map<String, Long>,
  onExecuteAction: (CareAction) -> Unit,
  modifier: Modifier = Modifier
) {
  val actions = CareActionRegistry.actionsByRoom[currentRoom] ?: emptyList()
  val now = System.currentTimeMillis()

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    actions.forEach { action ->
      val cooldownEnd = cooldowns[action.id] ?: 0L
      val isCoolingDown = now < cooldownEnd
      val remainingSec = if (isCoolingDown) ((cooldownEnd - now) / 1000).toInt() + 1 else 0

      CareActionButton(
        action = action,
        isCoolingDown = isCoolingDown,
        remainingSec = remainingSec,
        onExecute = { onExecuteAction(action) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun CareActionButton(
  action: CareAction,
  isCoolingDown: Boolean,
  remainingSec: Int,
  onExecute: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .testTag("care_action_${action.id}")
      .height(62.dp)
      .shadow(3.dp, RoundedCornerShape(20.dp))
      .clip(RoundedCornerShape(20.dp))
      .background(if (isCoolingDown) Color(0xFFF1F5F9) else Color.White)
      .border(1.5.dp, if (isCoolingDown) Color(0xFFCBD5E1) else Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        if (!isCoolingDown) {
          onExecute()
        }
      }
      .padding(horizontal = 10.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (isCoolingDown) Color(0xFFE2E8F0) else Color(0xFFEEF2FF)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = action.icon, fontSize = 18.sp)
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = action.name,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCoolingDown) Color.Gray else SleekTextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          if (isCoolingDown) {
            Text(
              text = " (${remainingSec}s)",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE11D48),
              modifier = Modifier.padding(start = 2.dp),
              maxLines = 1
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = action.statBoostText,
          fontSize = 9.5.sp,
          fontWeight = FontWeight.Medium,
          color = if (isCoolingDown) Color.Gray else Color(0xFF6366F1),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

