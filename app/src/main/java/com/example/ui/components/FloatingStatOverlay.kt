package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FloatingStatPopup

@Composable
fun FloatingStatOverlay(
  popups: List<FloatingStatPopup>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.offset(y = (-60).dp)
    ) {
      popups.takeLast(3).forEachIndexed { index, popup ->
        FloatingStatItem(popup = popup, index = index)
      }
    }
  }
}

@Composable
private fun FloatingStatItem(
  popup: FloatingStatPopup,
  index: Int
) {
  val yOffset = remember { Animatable(0f) }
  val alpha = remember { Animatable(1f) }

  LaunchedEffect(popup.id) {
    yOffset.animateTo(-40f, animationSpec = tween(durationMillis = 1800))
    alpha.animateTo(0f, animationSpec = tween(durationMillis = 600))
  }

  Box(
    modifier = Modifier
      .offset(y = (yOffset.value + (index * 28)).dp)
      .alpha(alpha.value)
      .shadow(4.dp, RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .background(Color.White)
      .border(2.dp, Color(popup.colorHex), RoundedCornerShape(16.dp))
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    Text(
      text = popup.text,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = Color(popup.colorHex)
    )
  }
}
