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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.util.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
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
  onOpenGuide: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val xpProgress = (xp.toFloat() / maxXp.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
  val animatedXp by animateFloatAsState(targetValue = xpProgress, label = "XpProgress")
  var showActivityHub by remember { mutableStateOf(false) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left: Level Badge & XP bar
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .clickable {
          SoundManager.playTapSound()
          onOpenPlayerProfile()
        }
        .padding(4.dp)
        .testTag("player_level_badge_button")
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .shadow(4.dp, CircleShape)
          .clip(CircleShape)
          .background(SleekPrimary)
          .border(2.5.dp, SleekPrimaryContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$level",
          color = Color.White,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Text(
          text = "MOCHI EXPLORER",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = SleekPrimary,
          letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Box(
          modifier = Modifier
            .width(88.dp)
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

    // Right: Coins Display and Single Activity Hub Icon
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Coin Display Badge
      Row(
        modifier = Modifier
          .testTag("shop_coins_button")
          .shadow(2.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White)
          .border(1.dp, Color(0xFFF1F5F9), CircleShape)
          .clickable {
            SoundManager.playTapSound()
            onOpenShop()
          }
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "✨",
          fontSize = 14.sp,
          color = CoinAmber
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = String.format("%,d", coins),
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = SleekTextDark,
          maxLines = 1,
          softWrap = false
        )
      }

      // SINGLE ICON containing all activities (Trophies, Quests, Codex, Wardrobe, Decor, Shop)
      Box(
        modifier = Modifier
          .testTag("activity_hub_button")
          .size(42.dp)
          .shadow(3.dp, CircleShape)
          .clip(CircleShape)
          .background(SleekPrimary)
          .border(2.dp, SleekPrimaryContainer, CircleShape)
          .clickable {
            SoundManager.playTapSound()
            showActivityHub = true
          },
        contentAlignment = Alignment.Center
      ) {
        Text(text = "🎒", fontSize = 20.sp)
      }
    }
  }

  // Single Hub Sheet containing all feature activities
  if (showActivityHub) {
    ModalBottomSheet(
      onDismissRequest = { showActivityHub = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = Color.White,
      shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
          .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Drag Indicator
        Box(
          modifier = Modifier
            .width(40.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(Color(0xFFCBD5E1))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Activity Hub 🎒",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = SleekTextDark
          )
          Text(
            text = "Explore & Customize",
            fontSize = 12.sp,
            color = SleekTextMuted
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of all 6 feature items inside the single hub icon!
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            HubItemCard(
              icon = "🏆",
              title = "Trophies",
              subtitle = "Achievements",
              tag = "trophies_button",
              modifier = Modifier.weight(1f),
              onClick = {
                showActivityHub = false
                onOpenAchievements()
              }
            )
            HubItemCard(
              icon = "📜",
              title = "Quests",
              subtitle = "Daily Tasks",
              tag = "daily_tasks_button",
              modifier = Modifier.weight(1f),
              onClick = {
                showActivityHub = false
                onOpenDailyTasks()
              }
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            HubItemCard(
              icon = "📚",
              title = "Codex",
              subtitle = "Collections",
              tag = "collections_button",
              modifier = Modifier.weight(1f),
              onClick = {
                showActivityHub = false
                onOpenCollections()
              }
            )
            HubItemCard(
              icon = "👗",
              title = "Wardrobe",
              subtitle = "Dress-Up Studio",
              tag = "customization_studio_button",
              modifier = Modifier.weight(1f),
              onClick = {
                showActivityHub = false
                onOpenCustomization()
              }
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            HubItemCard(
              icon = "🛋️",
              title = "Decoration",
              subtitle = "Room Studio",
              tag = "room_decoration_button",
              modifier = Modifier.weight(1f),
              onClick = {
                showActivityHub = false
                onOpenRoomDecoration()
              }
            )
            HubItemCard(
              icon = "🛍️",
              title = "Pet Shop",
              subtitle = "Boutique Market",
              tag = "shop_button",
              modifier = Modifier.weight(1f),
              onClick = {
                showActivityHub = false
                onOpenShop()
              }
            )
          }

          // Full width Guide button
          HubItemCard(
            icon = "💡",
            title = "How to Play Guide",
            subtitle = "Pet Care & Mini-Game Tutorial",
            tag = "how_to_play_guide_button",
            modifier = Modifier.fillMaxWidth(),
            onClick = {
              showActivityHub = false
              onOpenGuide()
            }
          )
        }
      }
    }
  }
}

@Composable
private fun HubItemCard(
  icon: String,
  title: String,
  subtitle: String,
  tag: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = Color(0xFFF8FAFC),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
      .testTag(tag)
      .clickable {
        SoundManager.playConfirmSound()
        onClick()
      }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(SleekPrimaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(text = icon, fontSize = 20.sp)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = SleekTextDark
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = SleekTextMuted
        )
      }
    }
  }
}

