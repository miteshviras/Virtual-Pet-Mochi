package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.data.PetEntity
import com.example.data.PetMoodState
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.StatClean
import com.example.ui.theme.StatEnergy
import com.example.ui.theme.StatHunger
import com.example.ui.theme.StatJoy

@Composable
fun SleekNeedsDashboard(
  pet: PetEntity,
  mood: PetMoodState = PetMoodState.HAPPY,
  modifier: Modifier = Modifier
) {
  // Single unified card containing mood badge and all 5 pet stats
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .shadow(4.dp, RoundedCornerShape(22.dp))
      .clip(RoundedCornerShape(22.dp))
      .background(Color.White)
      .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(22.dp))
      .padding(horizontal = 12.dp, vertical = 10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Row: Mood Status & Friendship
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${mood.icon} ${mood.label}",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SleekTextDark
      )
      Text(
        text = " • Friendship ${pet.friendship.toInt()}% 💕",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFE11D48),
        modifier = Modifier.padding(start = 6.dp)
      )
    }

    // Single Unified Row containing all 5 pet stats
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically
    ) {
      StatColumnItem(
        label = "Hunger",
        value = pet.hunger,
        color = StatHunger,
        modifier = Modifier.weight(1f)
      )
      StatColumnItem(
        label = "Energy",
        value = pet.energy,
        color = StatEnergy,
        modifier = Modifier.weight(1f)
      )
      StatColumnItem(
        label = "Joy",
        value = pet.happiness,
        color = StatJoy,
        modifier = Modifier.weight(1f)
      )
      StatColumnItem(
        label = "Clean",
        value = pet.cleanliness,
        color = StatClean,
        modifier = Modifier.weight(1f)
      )
      StatColumnItem(
        label = "Health",
        value = pet.health,
        color = Color(0xFF10B981),
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun StatColumnItem(
  label: String,
  value: Float,
  color: Color,
  modifier: Modifier = Modifier
) {
  val animatedValue by animateFloatAsState(
    targetValue = (value / 100f).coerceIn(0f, 1f),
    label = "StatAnim"
  )

  val isWarning = value < 25f

  Column(
    modifier = modifier
      .testTag("stat_$label")
      .padding(horizontal = 2.dp, vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (isWarning) Color(0xFFE11D48) else SleekTextDark,
      maxLines = 1
    )

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth(0.85f)
        .height(6.dp)
        .clip(CircleShape)
        .background(Color(0xFFF1F5F9))
    ) {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(animatedValue)
          .clip(CircleShape)
          .background(if (isWarning) Color(0xFFE11D48) else color)
      )
    }
  }
}

