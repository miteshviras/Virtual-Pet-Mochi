package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import com.example.data.PetEntity
import com.example.data.PlayerStatsMeta
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileSheet(
  petState: PetEntity,
  metaStats: PlayerStatsMeta,
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
        .padding(horizontal = 18.dp, vertical = 12.dp)
        .testTag("player_profile_sheet")
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
              .size(44.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEC4899)))),
            contentAlignment = Alignment.Center
          ) {
            Text("👑", fontSize = 22.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Player & Pet Profile",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "Pet Caretaker Extraordinaire",
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
            .testTag("close_player_profile")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Card 1: Player & Pet Progress Summary
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${petState.name} (Lvl ${petState.level})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextDark
              )
              Text(
                text = "Friendship Level: ${petState.friendship.toInt()}% 💕",
                fontSize = 12.sp,
                color = Color(0xFFE11D48),
                fontWeight = FontWeight.Bold
              )
            }

            // Level Badge Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(SleekPrimary, Color(0xFF8B5CF6))))
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text("Level ${petState.level} ⭐", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // XP Progress Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Experience (XP)", fontSize = 12.sp, color = SleekTextMuted)
            Text("${petState.xp} / ${petState.maxXp} XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
          }
          Spacer(modifier = Modifier.height(4.dp))
          val xpProgress = (petState.xp.toFloat() / petState.maxXp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = xpProgress,
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(CircleShape),
            color = SleekPrimary,
            trackColor = Color(0xFFE2E8F0)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text("Lifetime Statistics 📊", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
      Spacer(modifier = Modifier.height(10.dp))

      val statItems = listOf(
        StatCardData("Lifetime Coins", "${metaStats.lifetimeCoinsEarned} 💰", Color(0xFFFFFBEB), Color(0xFFF59E0B)),
        StatCardData("Coins Spent", "${metaStats.lifetimeCoinsSpent} 🛍️", Color(0xFFEFF6FF), Color(0xFF3B82F6)),
        StatCardData("Foods Fed", "${metaStats.totalFoodsFed} 🍎", Color(0xFFFEF2F2), Color(0xFFEF4444)),
        StatCardData("Baths Given", "${metaStats.totalBathsGiven} 🧼", Color(0xFFECFDF5), Color(0xFF10B981)),
        StatCardData("Care Actions", "${metaStats.totalCareActionsPerformed} ❤️", Color(0xFFFDF2F8), Color(0xFFEC4899)),
        StatCardData("Games Played", "${metaStats.totalActivitiesPlayed} 🎮", Color(0xFFF5F3FF), Color(0xFF8B5CF6)),
        StatCardData("Furniture Placed", "${metaStats.totalFurniturePlaced} 🛋️", Color(0xFFFFF7ED), Color(0xFFEA580C)),
        StatCardData("Outfits Worn", "${metaStats.totalCustomizationsEquipped} 👗", Color(0xFFF0FDF4), Color(0xFF16A34A))
      )

      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(220.dp)
      ) {
        items(statItems) { stat ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(stat.bgColor)
              .border(1.dp, stat.accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
              .padding(12.dp)
          ) {
            Column {
              Text(stat.label, fontSize = 11.sp, color = SleekTextMuted)
              Spacer(modifier = Modifier.height(4.dp))
              Text(stat.value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
            }
          }
        }
      }
    }
  }
}

private data class StatCardData(
  val label: String,
  val value: String,
  val bgColor: Color,
  val accentColor: Color
)
