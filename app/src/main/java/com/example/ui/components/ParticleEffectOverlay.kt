package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import kotlin.random.Random

enum class ParticleType {
  CONFETTI, SPARKLES, HEARTS, COINS
}

data class OverlayParticle(
  val initialX: Float,
  val initialY: Float,
  val speedX: Float,
  val speedY: Float,
  val size: Float,
  val color: Color,
  val symbol: String? = null
)

@Composable
fun ParticleEffectOverlay(
  triggerKey: Any?,
  type: ParticleType = ParticleType.SPARKLES,
  particleCount: Int = 20,
  modifier: Modifier = Modifier
) {
  if (triggerKey == null) return

  val progress = remember(triggerKey) { Animatable(0f) }

  LaunchedEffect(triggerKey) {
    progress.snapTo(0f)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
    )
  }

  val particles = remember(triggerKey) {
    val colors = listOf(
      Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6),
      Color(0xFFEC4899), Color(0xFF8B5CF6), Color(0xFFEF4444)
    )
    List(particleCount) {
      OverlayParticle(
        initialX = 0.5f + Random.nextFloat() * 0.2f - 0.1f,
        initialY = 0.4f + Random.nextFloat() * 0.2f - 0.1f,
        speedX = (Random.nextFloat() - 0.5f) * 600f,
        speedY = -200f - Random.nextFloat() * 400f,
        size = Random.nextFloat() * 12f + 8f,
        color = colors[Random.nextInt(colors.size)],
        symbol = when (type) {
          ParticleType.HEARTS -> "❤️"
          ParticleType.COINS -> "✨"
          ParticleType.CONFETTI -> null
          ParticleType.SPARKLES -> "⭐"
        }
      )
    }
  }

  if (progress.value < 1f) {
    Canvas(
      modifier = modifier
        .fillMaxSize()
        .testTag("particle_effect_canvas")
    ) {
      val t = progress.value
      val width = size.width
      val height = size.height

      particles.forEach { p ->
        val currentX = p.initialX * width + p.speedX * t
        val currentY = p.initialY * height + p.speedY * t + 300f * t * t // Gravity curve
        val alpha = (1f - t).coerceIn(0f, 1f)

        if (p.symbol != null) {
          // Render lightweight symbol
          drawCircle(
            color = p.color.copy(alpha = alpha * 0.8f),
            radius = p.size * (1f - t * 0.3f),
            center = Offset(currentX, currentY)
          )
        } else {
          // Render confetti square
          withTransform({
            rotate(t * 360f, Offset(currentX, currentY))
          }) {
            drawRect(
              color = p.color.copy(alpha = alpha),
              topLeft = Offset(currentX - p.size / 2, currentY - p.size / 2),
              size = androidx.compose.ui.geometry.Size(p.size, p.size * 1.5f)
            )
          }
        }
      }
    }
  }
}

