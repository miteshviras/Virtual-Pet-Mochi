package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.FloatingStatPopup
import com.example.data.InteractiveProp
import com.example.data.PetEmotion
import com.example.data.PetEntity
import com.example.data.RoomRegistry
import com.example.data.RoomType
import com.example.util.SoundManager

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoomViewport(
  pet: PetEntity,
  currentRoom: RoomType,
  emotion: PetEmotion,
  notificationMsg: String?,
  popups: List<FloatingStatPopup> = emptyList(),
  onPetClick: () -> Unit,
  onPropInteract: (InteractiveProp) -> Unit,
  onRoomSwipe: (RoomType) -> Unit,
  modifier: Modifier = Modifier
) {
  val roomOrder = remember { RoomType.entries }
  val currentRoomConfig = RoomRegistry.rooms[currentRoom] ?: RoomRegistry.rooms[RoomType.PLAY]!!

  var totalDragAmount by remember { mutableFloatStateOf(0f) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(currentRoom) {
        detectHorizontalDragGestures(
          onDragStart = { totalDragAmount = 0f },
          onDragEnd = {
            val currentIndex = roomOrder.indexOf(currentRoom)
            if (totalDragAmount < -120f && currentIndex < roomOrder.size - 1) {
              // Swiped Left -> Next Room
              SoundManager.playRoomSwipeSound()
              onRoomSwipe(roomOrder[currentIndex + 1])
            } else if (totalDragAmount > 120f && currentIndex > 0) {
              // Swiped Right -> Previous Room
              SoundManager.playRoomSwipeSound()
              onRoomSwipe(roomOrder[currentIndex - 1])
            }
          },
          onHorizontalDrag = { _, dragAmount ->
            totalDragAmount += dragAmount
          }
        )
      }
  ) {
    // Smooth Room Camera Transition Animation
    AnimatedContent(
      targetState = currentRoom,
      transitionSpec = {
        val oldIndex = roomOrder.indexOf(initialState)
        val newIndex = roomOrder.indexOf(targetState)
        if (newIndex > oldIndex) {
          slideInHorizontally { width -> width } + fadeIn() with
              slideOutHorizontally { width -> -width } + fadeOut()
        } else {
          slideInHorizontally { width -> -width } + fadeIn() with
              slideOutHorizontally { width -> width } + fadeOut()
        }
      },
      label = "RoomCameraTransition"
    ) { room ->
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth
        val height = maxHeight

        // Base Room & Mochi Character
        MochiPetView(
          pet = pet,
          room = room,
          emotion = emotion,
          notificationMsg = notificationMsg,
          onPetClick = onPetClick,
          modifier = Modifier.fillMaxSize()
        )

        // Render Room Specific Interactive Props
        currentRoomConfig.props.forEach { prop ->
          val xOffset = width * prop.xFraction - 28.dp
          val yOffset = height * prop.yFraction - 28.dp

          InteractiveRoomProp(
            prop = prop,
            onInteract = onPropInteract,
            modifier = Modifier
              .align(Alignment.TopStart)
              .offset(x = xOffset, y = yOffset)
          )
        }

        // Overlay Floating Stat Gains (+15 Hunger, +10 XP)
        FloatingStatOverlay(popups = popups)
      }
    }
  }
}
