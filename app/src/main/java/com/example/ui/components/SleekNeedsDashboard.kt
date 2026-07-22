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
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    // Mood Status Pill Header
    Box(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 6.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color.White.copy(alpha = 0.9f))
        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
        .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "${mood.icon} ${mood.label}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
        Text(text = " • Friendship ${pet.friendship.toInt()}% 💕", fontSize = 11.sp, color = Color(0xFFE11D48), modifier = Modifier.padding(start = 6.dp))
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      StatItem(
        label = "Hunger",
        value = pet.hunger,
        color = StatHunger,
        modifier = Modifier.weight(1f)
      )
      StatItem(
        label = "Energy",
        value = pet.energy,
        color = StatEnergy,
        modifier = Modifier.weight(1f)
      )
      StatItem(
        label = "Joy",
        value = pet.happiness,
        color = StatJoy,
        modifier = Modifier.weight(1f)
      )
      StatItem(
        label = "Clean",
        value = pet.cleanliness,
        color = StatClean,
        modifier = Modifier.weight(1f)
      )
      StatItem(
        label = "Health",
        value = pet.health,
        color = Color(0xFF10B981),
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun StatItem(
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
      .shadow(2.dp, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .background(if (isWarning) Color(0xFFFFF1F2) else Color.White)
      .border(1.dp, if (isWarning) Color(0xFFFECDD3) else Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
      .padding(horizontal = 4.dp, vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (isWarning) Color(0xFFE11D48) else SleekTextDark
    )

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .height(5.dp)
        .clip(CircleShape)
        .background(Color(0xFFE2E8F0))
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

