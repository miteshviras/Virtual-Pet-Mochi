package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PetEmotion
import com.example.data.PetEntity
import com.example.data.RoomType
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Particle(val id: Long, val x: Float, val y: Float, val emoji: String)

@Composable
fun MochiPetView(
  pet: PetEntity,
  room: RoomType,
  emotion: PetEmotion,
  notificationMsg: String?,
  onPetClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val squishScaleY = remember { Animatable(1f) }
  val squishScaleX = remember { Animatable(1f) }

  // Idle continuous breathing animation
  val infiniteTransition = rememberInfiniteTransition(label = "IdleBreathing")
  val breathY by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "BreathY"
  )

  // Particle explosion on tap
  val particles = remember { mutableStateListOf<Particle>() }

  val roomGradient = when (room) {
    RoomType.PLAY -> listOf(Color(0xFFEFF6FF), Color(0xFFC7D2FE))
    RoomType.KITCHEN -> listOf(Color(0xFFFFF7ED), Color(0xFFFED7AA))
    RoomType.BATH -> listOf(Color(0xFFECFEFF), Color(0xFFA5F3FC))
    RoomType.SLEEP -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
    RoomType.GARDEN -> listOf(Color(0xFFF7FEE7), Color(0xFFA3E635))
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
      .clip(RoundedCornerShape(48.dp))
      .background(Brush.verticalGradient(roomGradient))
      .border(4.dp, Color.White, RoundedCornerShape(48.dp))
      .shadow(8.dp, RoundedCornerShape(48.dp))
  ) {
    // Room Ambient Decorations
    Canvas(modifier = Modifier.fillMaxSize()) {
      if (room == RoomType.SLEEP) {
        // Moon & Stars
        drawCircle(
          color = Color(0xFFFDE047).copy(alpha = 0.4f),
          radius = 60f,
          center = Offset(size.width * 0.8f, size.height * 0.2f)
        )
      } else if (room == RoomType.BATH) {
        // Water Bubbles
        drawCircle(Color.White.copy(alpha = 0.4f), radius = 20f, center = Offset(size.width * 0.2f, size.height * 0.3f))
        drawCircle(Color.White.copy(alpha = 0.3f), radius = 35f, center = Offset(size.width * 0.75f, size.height * 0.25f))
      } else if (room == RoomType.GARDEN) {
        // Sun glow
        drawCircle(Color(0xFFFACC15).copy(alpha = 0.3f), radius = 120f, center = Offset(size.width * 0.85f, size.height * 0.15f))
      } else {
        // Soft ambient circles
        drawCircle(Color.White.copy(alpha = 0.3f), radius = 100f, center = Offset(size.width * 0.15f, size.height * 0.2f))
        drawCircle(Color(0xFF818CF8).copy(alpha = 0.2f), radius = 140f, center = Offset(size.width * 0.8f, size.height * 0.7f))
      }
    }

    // Main Pet Container (Center)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .testTag("mochi_character_area")
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) {
          onPetClick()
          // Trigger Squish Animation
          scope.launch {
            squishScaleY.animateTo(0.8f, spring())
            squishScaleY.animateTo(1.1f, spring())
            squishScaleY.animateTo(1f, spring())
          }
          scope.launch {
            squishScaleX.animateTo(1.15f, spring())
            squishScaleX.animateTo(0.9f, spring())
            squishScaleX.animateTo(1f, spring())
          }
          // Spawn hearts particle
          val p = Particle(
            id = System.currentTimeMillis(),
            x = (Random.nextFloat() - 0.5f) * 80f,
            y = -40f,
            emoji = listOf("❤️", "✨", "💖", "🌸").random()
          )
          particles.add(p)
        },
      contentAlignment = Alignment.Center
    ) {

      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top headwear overlay (Hat, Hair, Glasses, Face)
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .scale(
              scaleX = squishScaleX.value,
              scaleY = squishScaleY.value * breathY
            )
        ) {
          // Mochi Shadow
          Canvas(
            modifier = Modifier
              .width(140.dp)
              .height(24.dp)
              .offset(y = 75.dp)
          ) {
            drawOval(
              color = Color(0xFF1E1B4B).copy(alpha = 0.15f)
            )
          }

          // Back & Tail Layer
          Canvas(
            modifier = Modifier.size(170.dp, 150.dp)
          ) {
            // Back accessory (wings/backpack)
            if (pet.equippedBack != "none") {
              val backColor = Color(0xFFFBBF24)
              when (pet.equippedBack) {
                "back_wings" -> {
                  drawCircle(Color(0xFFFEF08A).copy(alpha = 0.7f), radius = 28f, center = Offset(size.width * 0.12f, size.height * 0.35f))
                  drawCircle(Color(0xFFFEF08A).copy(alpha = 0.7f), radius = 28f, center = Offset(size.width * 0.88f, size.height * 0.35f))
                }
                "back_backpack" -> {
                  drawRoundRect(Color(0xFF854D0E), topLeft = Offset(size.width * 0.22f, size.height * 0.45f), size = androidx.compose.ui.geometry.Size(24f, 32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                }
              }
            }

            // Tail accessory
            if (pet.equippedTail != "none") {
              drawCircle(Color(0xFFEC4899), radius = 10f, center = Offset(size.width * 0.88f, size.height * 0.65f))
            }
          }

          // Mochi Body Canvas
          Canvas(
            modifier = Modifier.size(170.dp, 150.dp)
          ) {
            // Dynamic Skin Gradient
            val skinOpt = com.example.data.CustomizationRegistry.skinColors.firstOrNull { it.id == pet.equippedColor }
            val colorList = skinOpt?.colors?.map { Color(it) } ?: listOf(Color(0xFFF472B6), Color(0xFFC084FC), Color(0xFF818CF8))

            val bodyGradient = Brush.linearGradient(
              colors = colorList,
              start = Offset(0f, 0f),
              end = Offset(size.width, size.height)
            )

            val path = Path().apply {
              moveTo(size.width * 0.15f, size.height * 0.5f)
              cubicTo(
                size.width * 0.1f, size.height * 0.05f,
                size.width * 0.9f, size.height * 0.05f,
                size.width * 0.85f, size.height * 0.5f
              )
              cubicTo(
                size.width * 0.95f, size.height * 0.95f,
                size.width * 0.05f, size.height * 0.95f,
                size.width * 0.15f, size.height * 0.5f
              )
              close()
            }

            drawPath(path = path, brush = bodyGradient)

            // Body Pattern Overlay
            when (pet.bodyPattern) {
              "spots" -> {
                drawCircle(Color.White.copy(alpha = 0.35f), radius = 12f, center = Offset(size.width * 0.3f, size.height * 0.25f))
                drawCircle(Color.White.copy(alpha = 0.35f), radius = 16f, center = Offset(size.width * 0.72f, size.height * 0.32f))
                drawCircle(Color.White.copy(alpha = 0.35f), radius = 10f, center = Offset(size.width * 0.5f, size.height * 0.2f))
              }
              "hearts" -> {
                drawCircle(Color(0xFFFB7185).copy(alpha = 0.4f), radius = 10f, center = Offset(size.width * 0.28f, size.height * 0.3f))
                drawCircle(Color(0xFFFB7185).copy(alpha = 0.4f), radius = 12f, center = Offset(size.width * 0.75f, size.height * 0.28f))
              }
              "stardust" -> {
                drawCircle(Color(0xFFFEF08A).copy(alpha = 0.6f), radius = 6f, center = Offset(size.width * 0.25f, size.height * 0.25f))
                drawCircle(Color(0xFFFEF08A).copy(alpha = 0.6f), radius = 8f, center = Offset(size.width * 0.7f, size.height * 0.22f))
                drawCircle(Color(0xFFFEF08A).copy(alpha = 0.6f), radius = 5f, center = Offset(size.width * 0.48f, size.height * 0.18f))
              }
            }

            // Clothing: Pants/Lower Outfit
            if (pet.equippedPants != "none") {
              val pantsColor = when (pet.equippedPants) {
                "pants_shorts" -> Color(0xFF2563EB)
                "pants_skirt" -> Color(0xFFEC4899)
                else -> Color(0xFF3B82F6)
              }
              drawArc(
                color = pantsColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(size.width * 0.18f, size.height * 0.62f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.28f)
              )
            }

            // Clothing: Shirt / Upper
            if (pet.equippedShirt != "none") {
              val shirtColor = Color(0xFF38BDF8)
              drawArc(
                color = shirtColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(size.width * 0.22f, size.height * 0.5f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.56f, size.height * 0.25f)
              )
            }

            // Clothing: Jacket / Coat
            if (pet.equippedJacket != "none") {
              val jacketColor = Color(0xFFDC2626)
              drawRoundRect(
                color = jacketColor,
                topLeft = Offset(size.width * 0.16f, size.height * 0.48f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.68f, size.height * 0.32f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
              )
            }

            // Clothing: Shoes
            if (pet.equippedShoes != "none") {
              drawOval(Color(0xFF1E293B), topLeft = Offset(size.width * 0.22f, size.height * 0.82f), size = androidx.compose.ui.geometry.Size(28f, 16f))
              drawOval(Color(0xFF1E293B), topLeft = Offset(size.width * 0.62f, size.height * 0.82f), size = androidx.compose.ui.geometry.Size(28f, 16f))
            }

            // Neck Accessory (Scarf, Bowtie)
            if (pet.equippedNeck != "none") {
              val neckColor = Color(0xFFEF4444)
              drawRoundRect(
                color = neckColor,
                topLeft = Offset(size.width * 0.32f, size.height * 0.52f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.36f, 18f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
              )
            }

            // Cheek Blush
            val blushColor = Color(0xFFF43F5E).copy(alpha = 0.35f)
            drawOval(
              color = blushColor,
              topLeft = Offset(size.width * 0.18f, size.height * 0.55f),
              size = androidx.compose.ui.geometry.Size(28f, 16f)
            )
            drawOval(
              color = blushColor,
              topLeft = Offset(size.width * 0.68f, size.height * 0.55f),
              size = androidx.compose.ui.geometry.Size(28f, 16f)
            )

            // Face Accessory (Mask, Stars)
            if (pet.equippedFace == "face_mask") {
              drawRoundRect(
                color = Color.White,
                topLeft = Offset(size.width * 0.36f, size.height * 0.54f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.28f, 24f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
              )
            }

            // Dynamic Eye Color & Eye Style
            val eyeColorOpt = com.example.data.CustomizationRegistry.eyeColors.firstOrNull { it.id == pet.eyeColor }
            val dynamicEyeColor = if (eyeColorOpt != null) Color(eyeColorOpt.colorHex) else Color(0xFF0F172A)

            when (emotion) {
              PetEmotion.SLEEPING, PetEmotion.TIRED -> {
                // Closed happy/sleepy eyes arcs
                drawArc(
                  color = dynamicEyeColor,
                  startAngle = 0f, sweepAngle = 180f, useCenter = false,
                  topLeft = Offset(size.width * 0.32f, size.height * 0.42f),
                  size = androidx.compose.ui.geometry.Size(20f, 16f),
                  style = Stroke(width = 5f)
                )
                drawArc(
                  color = dynamicEyeColor,
                  startAngle = 0f, sweepAngle = 180f, useCenter = false,
                  topLeft = Offset(size.width * 0.58f, size.height * 0.42f),
                  size = androidx.compose.ui.geometry.Size(20f, 16f),
                  style = Stroke(width = 5f)
                )
              }
              PetEmotion.SICK, PetEmotion.LONELY -> {
                // Droopy eyes
                drawArc(
                  color = dynamicEyeColor,
                  startAngle = 180f, sweepAngle = 180f, useCenter = false,
                  topLeft = Offset(size.width * 0.32f, size.height * 0.42f),
                  size = androidx.compose.ui.geometry.Size(20f, 16f),
                  style = Stroke(width = 5f)
                )
                drawArc(
                  color = dynamicEyeColor,
                  startAngle = 180f, sweepAngle = 180f, useCenter = false,
                  topLeft = Offset(size.width * 0.58f, size.height * 0.42f),
                  size = androidx.compose.ui.geometry.Size(20f, 16f),
                  style = Stroke(width = 5f)
                )
                val sadMouth = Path().apply {
                  moveTo(size.width * 0.44f, size.height * 0.65f)
                  quadraticTo(
                    size.width * 0.5f, size.height * 0.55f,
                    size.width * 0.56f, size.height * 0.65f
                  )
                }
                drawPath(path = sadMouth, color = dynamicEyeColor, style = Stroke(width = 5f))
              }
              else -> {
                // Eye style rendering
                when (pet.eyeStyle) {
                  "anime_star" -> {
                    drawCircle(color = dynamicEyeColor, radius = 12f, center = Offset(size.width * 0.36f, size.height * 0.44f))
                    drawCircle(color = Color.White, radius = 5f, center = Offset(size.width * 0.34f, size.height * 0.42f))
                    drawCircle(color = dynamicEyeColor, radius = 12f, center = Offset(size.width * 0.64f, size.height * 0.44f))
                    drawCircle(color = Color.White, radius = 5f, center = Offset(size.width * 0.62f, size.height * 0.42f))
                  }
                  "cute_arc" -> {
                    drawArc(color = dynamicEyeColor, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(size.width * 0.32f, size.height * 0.4f), size = androidx.compose.ui.geometry.Size(22f, 18f), style = Stroke(width = 5f))
                    drawArc(color = dynamicEyeColor, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(size.width * 0.58f, size.height * 0.4f), size = androidx.compose.ui.geometry.Size(22f, 18f), style = Stroke(width = 5f))
                  }
                  else -> {
                    // Standard Sparkle Round
                    drawCircle(color = dynamicEyeColor, radius = 9f, center = Offset(size.width * 0.36f, size.height * 0.44f))
                    drawCircle(color = Color.White, radius = 3f, center = Offset(size.width * 0.34f, size.height * 0.42f))
                    drawCircle(color = dynamicEyeColor, radius = 9f, center = Offset(size.width * 0.64f, size.height * 0.44f))
                    drawCircle(color = Color.White, radius = 3f, center = Offset(size.width * 0.62f, size.height * 0.42f))
                  }
                }

                // Mouth style rendering
                when (pet.mouthStyle) {
                  "cat_w" -> {
                    val catMouth = Path().apply {
                      moveTo(size.width * 0.42f, size.height * 0.58f)
                      quadraticTo(size.width * 0.46f, size.height * 0.66f, size.width * 0.5f, size.height * 0.58f)
                      quadraticTo(size.width * 0.54f, size.height * 0.66f, size.width * 0.58f, size.height * 0.58f)
                    }
                    drawPath(path = catMouth, color = dynamicEyeColor, style = Stroke(width = 4f))
                  }
                  "tongue_blep" -> {
                    val mouthPath = Path().apply {
                      moveTo(size.width * 0.44f, size.height * 0.58f)
                      quadraticTo(size.width * 0.5f, size.height * 0.68f, size.width * 0.56f, size.height * 0.58f)
                    }
                    drawPath(path = mouthPath, color = dynamicEyeColor, style = Stroke(width = 5f))
                    drawCircle(Color(0xFFF43F5E), radius = 6f, center = Offset(size.width * 0.5f, size.height * 0.66f))
                  }
                  else -> {
                    val mouthPath = Path().apply {
                      moveTo(size.width * 0.44f, size.height * 0.58f)
                      quadraticTo(
                        size.width * 0.5f, size.height * 0.68f,
                        size.width * 0.56f, size.height * 0.58f
                      )
                    }
                    drawPath(path = mouthPath, color = dynamicEyeColor, style = Stroke(width = 5f))
                  }
                }
              }
            }

            // Glasses
            if (pet.equippedGlasses != "none") {
              val glassColor = Color(0xFF1E293B)
              drawRoundRect(glassColor, topLeft = Offset(size.width * 0.28f, size.height * 0.38f), size = androidx.compose.ui.geometry.Size(32f, 22f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f), style = Stroke(width = 4f))
              drawRoundRect(glassColor, topLeft = Offset(size.width * 0.56f, size.height * 0.38f), size = androidx.compose.ui.geometry.Size(32f, 22f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f), style = Stroke(width = 4f))
              drawLine(glassColor, start = Offset(size.width * 0.47f, size.height * 0.44f), end = Offset(size.width * 0.56f, size.height * 0.44f), strokeWidth = 4f)
            }
          }

          // Top Accessories Text overlays for Hats, Hair, Hand
          Box(modifier = Modifier.size(170.dp, 150.dp)) {
            // Hair / Ear decor
            if (pet.equippedHair != "none") {
              val hairIcon = when (pet.equippedHair) {
                "hair_ribbon" -> "🎀"
                "hair_flower" -> "🌸"
                else -> "✨"
              }
              Text(
                text = hairIcon,
                fontSize = 26.sp,
                modifier = Modifier
                  .align(Alignment.TopStart)
                  .offset(x = 24.dp, y = 10.dp)
              )
            }

            // Hat
            if (pet.equippedHat != "none") {
              val hatIcon = when (pet.equippedHat) {
                "hat_crown" -> "👑"
                "hat_party" -> "🥳"
                "hat_wizard" -> "🧙"
                else -> "🎩"
              }
              Text(
                text = hatIcon,
                fontSize = 36.sp,
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .offset(y = (-20).dp)
              )
            }

            // Hand Prop
            if (pet.equippedHand != "none") {
              val handIcon = when (pet.equippedHand) {
                "hand_wand" -> "🪄"
                else -> "🌻"
              }
              Text(
                text = handIcon,
                fontSize = 28.sp,
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .offset(x = (-12).dp, y = (-20).dp)
              )
            }
          }
        }
      }

      // Mood Ambient Floating Particle (Zzz, Bubbles, Stars, bandaid)
      val ambientMoodEmoji = when (emotion) {
        PetEmotion.SLEEPING -> "💤"
        PetEmotion.DIRTY -> "🫧"
        PetEmotion.HUNGRY -> "💭🍎"
        PetEmotion.SICK -> "🩹"
        PetEmotion.EXCITED -> "✨"
        PetEmotion.LONELY -> "💧"
        else -> null
      }
      if (ambientMoodEmoji != null) {
        Text(
          text = ambientMoodEmoji,
          fontSize = 22.sp,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-40).dp, y = (40).dp)
        )
      }

      // Tap Heart Particles Overlay
      particles.forEach { p ->
        Text(
          text = p.emoji,
          fontSize = 24.sp,
          modifier = Modifier
            .offset(x = p.x.dp, y = p.y.dp)
        )
      }
    }


    // Floating Notification Capsule (Top Right)
    val msg = notificationMsg
    if (!msg.isNullOrEmpty()) {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 20.dp, end = 20.dp)
          .shadow(6.dp, CircleShape)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.92f))
          .border(1.dp, Color.White, CircleShape)
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Text(
          text = msg,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = SleekTextDark
        )
      }
    }

    // Mini Room Title Pill (Bottom Center)
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
        .shadow(4.dp, RoundedCornerShape(20.dp))
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFF1E1B4B).copy(alpha = 0.85f))
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Text(
        text = room.displayName.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        letterSpacing = 1.2.sp,
        maxLines = 1,
        softWrap = false
      )
    }
  }
}

