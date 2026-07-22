package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FloatingStatPopup
import com.example.data.InteractiveProp
import com.example.data.PetEmotion
import com.example.data.PetEntity
import com.example.data.PlacedFurniture
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
  isDecorationMode: Boolean = false,
  placedFurnitureList: List<PlacedFurniture> = emptyList(),
  selectedPlacedFurniture: PlacedFurniture? = null,
  onSelectPlacedFurniture: (PlacedFurniture?) -> Unit = {},
  onMovePlacedFurniture: (instanceId: String, newX: Float, newY: Float) -> Unit = { _, _, _ -> },
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
      .pointerInput(currentRoom, isDecorationMode) {
        if (!isDecorationMode) {
          detectHorizontalDragGestures(
            onDragStart = { totalDragAmount = 0f },
            onDragEnd = {
              val currentIndex = roomOrder.indexOf(currentRoom)
              if (totalDragAmount < -120f && currentIndex < roomOrder.size - 1) {
                SoundManager.playRoomSwipeSound()
                onRoomSwipe(roomOrder[currentIndex + 1])
              } else if (totalDragAmount > 120f && currentIndex > 0) {
                SoundManager.playRoomSwipeSound()
                onRoomSwipe(roomOrder[currentIndex - 1])
              }
            },
            onHorizontalDrag = { _, dragAmount ->
              totalDragAmount += dragAmount
            }
          )
        }
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

        // --- PHASE 7: PLACED FURNITURE ITEMS ---
        placedFurnitureList.forEach { furniture ->
          val isSelected = selectedPlacedFurniture?.instanceId == furniture.instanceId
          val xOffset = width * furniture.xFraction - 32.dp
          val yOffset = height * furniture.yFraction - 32.dp

          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .offset(x = xOffset, y = yOffset)
              .rotate(furniture.rotationDegrees.toFloat())
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) Color(0xFFC084FC).copy(alpha = 0.35f) else Color.Transparent)
              .border(
                width = if (isSelected) 2.5.dp else if (isDecorationMode) 1.dp else 0.dp,
                color = if (isSelected) Color(0xFF8B5CF6) else if (isDecorationMode) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
              )
              .clickable {
                if (isDecorationMode) {
                  onSelectPlacedFurniture(if (isSelected) null else furniture)
                }
              }
              .pointerInput(furniture.instanceId, isDecorationMode) {
                if (isDecorationMode) {
                  detectDragGestures { change, dragAmount ->
                    change.consume()
                    val pxWidth = size.width.toFloat()
                    val pxHeight = size.height.toFloat()
                    val newX = (furniture.xFraction + dragAmount.x / (pxWidth * 4f)).coerceIn(0.12f, 0.88f)
                    val newY = (furniture.yFraction + dragAmount.y / (pxHeight * 4f)).coerceIn(0.18f, 0.85f)
                    onMovePlacedFurniture(furniture.instanceId, newX, newY)
                  }
                }
              }
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = furniture.icon,
              fontSize = 32.sp
            )
          }
        }

        // Decoration Edit Grid Overlay when in Edit Mode
        if (isDecorationMode) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
            val gridColor = Color.White.copy(alpha = 0.25f)
            // Draw room grid guidelines
            for (i in 1..4) {
              val x = size.width * (i * 0.2f)
              drawLine(gridColor, start = Offset(x, 0f), end = Offset(x, size.height), pathEffect = stroke, strokeWidth = 2f)
            }
            for (j in 1..4) {
              val y = size.height * (j * 0.2f)
              drawLine(gridColor, start = Offset(0f, y), end = Offset(size.width, y), pathEffect = stroke, strokeWidth = 2f)
            }
          }
        }

        // Overlay Floating Stat Gains (+15 Hunger, +10 XP)
        FloatingStatOverlay(popups = popups)
      }
    }
  }
}
