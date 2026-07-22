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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AchievementCategory
import com.example.data.AchievementDefinition
import com.example.data.AchievementProgress
import com.example.data.MetaRegistry
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsSheet(
  achievementsMap: Map<String, AchievementProgress>,
  onClaimReward: (achievementId: String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp)
        .testTag("achievements_sheet")
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
              .size(40.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))),
            contentAlignment = Alignment.Center
          ) {
            Text("🏆", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Trophies & Milestones",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "Complete care & game goals to earn coin prizes!",
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
            .testTag("close_achievements")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category filter pills
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          val isSelected = selectedCategory == null
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) SleekPrimary else Color(0xFFF1F5F9))
              .clickable { selectedCategory = null }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = "All Categories",
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else Color(0xFF64748B)
            )
          }
        }

        items(AchievementCategory.values()) { cat ->
          val isSelected = selectedCategory == cat
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) SleekPrimary else Color(0xFFF1F5F9))
              .clickable { selectedCategory = cat }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(cat.icon, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = cat.displayName,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      val filteredDefs = MetaRegistry.achievements.filter { def ->
        selectedCategory == null || def.category == selectedCategory
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(360.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredDefs) { def ->
          val prog = achievementsMap[def.id] ?: AchievementProgress(def.id)
          AchievementCardItem(def = def, prog = prog, onClaim = { onClaimReward(def.id) })
        }
      }
    }
  }
}

@Composable
fun AchievementCardItem(
  def: AchievementDefinition,
  prog: AchievementProgress,
  onClaim: () -> Unit
) {
  val isCompleted = prog.currentProgress >= def.maxProgress
  val isClaimed = prog.isClaimed

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(if (isClaimed) Color(0xFFF8FAFC) else Color.White)
      .border(
        1.dp,
        if (isCompleted && !isClaimed) Color(0xFFF59E0B) else Color(0xFFE2E8F0),
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
          .background(if (isCompleted) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
      ) {
        Text(def.icon, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(def.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
          Text("${prog.currentProgress}/${def.maxProgress}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        Text(def.description, fontSize = 11.sp, color = SleekTextMuted)
        Spacer(modifier = Modifier.height(6.dp))

        val progressFrac = (prog.currentProgress.toFloat() / def.maxProgress.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
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
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
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
          Text("+${def.rewardCoins}💰", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CoinAmber)
        }
      }
    }
  }
}
