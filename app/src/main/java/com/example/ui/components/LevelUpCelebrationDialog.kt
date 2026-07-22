package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SleekPrimary

@Composable
fun LevelUpCelebrationDialog(
  newLevel: Int,
  onDismiss: () -> Unit
) {
  val scaleAnim = remember { Animatable(0.5f) }
  val pulseAnim = remember { Animatable(1.0f) }

  LaunchedEffect(Unit) {
    scaleAnim.animateTo(
      targetValue = 1.0f,
      animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    pulseAnim.animateTo(
      targetValue = 1.15f,
      animationSpec = infiniteRepeatable(
        animation = tween(600, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      )
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .scale(scaleAnim.value)
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("level_up_dialog"),
      shape = RoundedCornerShape(28.dp),
      color = Color.White
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Sparkling Badge
        Box(
          modifier = Modifier
            .size(90.dp)
            .scale(pulseAnim.value)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))),
          contentAlignment = Alignment.Center
        ) {
          Text("🌟", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "LEVEL UP!",
          fontSize = 28.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFFD97706)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Mochi reached Level $newLevel! 🎉",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Level Up Bonus Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFEF3C7))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Coin Bonus", fontSize = 11.sp, color = Color(0xFFB45309))
              Text("+200 💰", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(Color(0xFFFCD34D))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("New Items Unlocked", fontSize = 11.sp, color = Color(0xFFB45309))
              Text("Shop & Decor 🛍️", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("level_up_ok_button")
        ) {
          Text("Awesome! ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
