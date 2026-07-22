package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InteractiveProp
import com.example.ui.theme.SleekTextDark
import com.example.util.SoundManager
import kotlinx.coroutines.launch

@Composable
fun InteractiveRoomProp(
  prop: InteractiveProp,
  onInteract: (InteractiveProp) -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val scale = remember { Animatable(1f) }

  Box(
    modifier = modifier
      .testTag("room_prop_${prop.id}")
      .scale(scale.value)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        SoundManager.playPropInteractionSound()
        scope.launch {
          scale.animateTo(1.25f, spring())
          scale.animateTo(0.9f, spring())
          scale.animateTo(1.0f, spring())
        }
        onInteract(prop)
      },
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .shadow(4.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.9f))
          .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(text = prop.icon, fontSize = 28.sp)
      }

      Box(
        modifier = Modifier
          .offset(y = (-4).dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E1B4B).copy(alpha = 0.7f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = prop.name,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
