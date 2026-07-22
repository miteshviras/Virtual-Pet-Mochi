package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DailyRewardRegistry
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted

@Composable
fun DailyRewardDialog(
  currentStreakDay: Int,
  hasClaimedToday: Boolean,
  onDismiss: () -> Unit,
  onClaimReward: () -> Unit
) {
  val rewards = DailyRewardRegistry.sevenDayRewards

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .shadow(12.dp, RoundedCornerShape(28.dp))
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Daily Care Rewards 📅",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark
            )
            Text(
              text = "Streak: Day $currentStreakDay / 7 🔥",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CoinAmber
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_daily_reward_dialog")
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SleekTextDark)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7-day horizontal reward cards
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(rewards) { reward ->
            val isCurrentDay = reward.day == currentStreakDay
            val isPassedDay = reward.day < currentStreakDay

            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = when {
                  isCurrentDay -> Color(0xFFEFF6FF)
                  isPassedDay -> Color(0xFFF1F5F9)
                  else -> Color.White
                }
              ),
              border = androidx.compose.foundation.BorderStroke(
                width = if (isCurrentDay) 2.dp else 1.dp,
                color = when {
                  isCurrentDay -> SleekPrimary
                  isPassedDay -> Color(0xFF10B981)
                  else -> Color(0xFFE2E8F0)
                }
              ),
              modifier = Modifier.width(100.dp)
            ) {
              Column(
                modifier = Modifier
                  .padding(10.dp)
                  .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Day ${reward.day}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrentDay) SleekPrimary else SleekTextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = reward.icon, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "+${reward.rewardCoins}🪙",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = CoinAmber
                )

                if (reward.rewardItem != null) {
                  Text(
                    text = reward.rewardItem.displayName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekTextDark,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = when {
                    isPassedDay -> "Claimed ✓"
                    isCurrentDay && hasClaimedToday -> "Done ✓"
                    isCurrentDay -> "Ready! ✨"
                    else -> "Locked 🔒"
                  },
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = when {
                    isPassedDay || (isCurrentDay && hasClaimedToday) -> Color(0xFF10B981)
                    isCurrentDay -> SleekPrimary
                    else -> SleekTextMuted
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Claim Button
        val currentReward = rewards.getOrNull((currentStreakDay - 1).coerceIn(0, 6))
        val canClaim = !hasClaimedToday && currentReward != null

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("claim_daily_reward_button")
            .clip(CircleShape)
            .background(if (canClaim) SleekPrimary else Color(0xFFCBD5E1))
            .clickable {
              if (canClaim) {
                onClaimReward()
              }
            }
            .padding(vertical = 14.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (hasClaimedToday) "Claimed Today's Reward! 🎉" else "Claim Day $currentStreakDay Bonus! 🎁",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
