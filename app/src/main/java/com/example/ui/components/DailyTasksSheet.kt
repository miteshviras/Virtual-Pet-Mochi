package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyQuest
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTasksSheet(
  dailyQuests: List<DailyQuest>,
  onClaimQuest: (questId: String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .testTag("daily_tasks_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4)))),
            contentAlignment = Alignment.Center
          ) {
            Text("📜", fontSize = 22.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Mochi's Daily Missions",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "3 daily tasks from Mochi • Earn bonus Coins & XP! 🪙",
              fontSize = 11.sp,
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
            .testTag("close_daily_tasks")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Mochi Daily Banner
      val totalCompleted = dailyQuests.count { it.isClaimed || it.currentProgress >= it.targetProgress }
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFECFDF5))
          .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(16.dp))
          .padding(12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("🐾", fontSize = 26.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Mochi's Missions Progress",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF065F46)
            )
            Text(
              text = "$totalCompleted of ${dailyQuests.size} missions finished today!",
              fontSize = 11.sp,
              color = Color(0xFF047857)
            )
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF10B981))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "$totalCompleted/${dailyQuests.size} Done",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(dailyQuests) { quest ->
          DailyQuestCardItem(quest = quest, onClaim = { onClaimQuest(quest.id) })
        }
      }
    }
  }
}

@Composable
fun DailyQuestCardItem(
  quest: DailyQuest,
  onClaim: () -> Unit
) {
  val isCompleted = quest.currentProgress >= quest.targetProgress
  val isClaimed = quest.isClaimed

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(if (isClaimed) Color(0xFFF8FAFC) else Color.White)
      .border(
        1.dp,
        if (isCompleted && !isClaimed) Color(0xFF10B981) else Color(0xFFE2E8F0),
        RoundedCornerShape(18.dp)
      )
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(if (isCompleted) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
      ) {
        Text(quest.icon, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(quest.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
          Text("${quest.currentProgress}/${quest.targetProgress}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        Text(quest.description, fontSize = 11.sp, color = SleekTextMuted)
        Spacer(modifier = Modifier.height(6.dp))

        val progressFrac = (quest.currentProgress.toFloat() / quest.targetProgress.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
        LinearProgressIndicator(
          progress = progressFrac,
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CircleShape),
          color = if (isCompleted) Color(0xFF10B981) else SleekPrimary,
          trackColor = Color(0xFFE2E8F0)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      if (isClaimed) {
        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xFFDCFCE7))
            .padding(8.dp)
        ) {
          Icon(Icons.Default.Check, contentDescription = "Claimed", tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
        }
      } else if (isCompleted) {
        Button(
          onClick = onClaim,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Text("Claim ✨", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      } else {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("+${quest.rewardCoins}💰", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoinAmber)
        }
      }
    }
  }
}
