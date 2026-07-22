package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = SleekPrimary,
    onPrimary = Color.White,
    primaryContainer = SleekPrimaryContainer,
    onPrimaryContainer = SleekOnPrimaryContainer,
    secondary = SleekSecondary,
    secondaryContainer = SleekSecondaryContainer,
    background = SleekBg,
    onBackground = SleekTextDark,
    surface = SleekSurface,
    onSurface = SleekTextDark,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekTextMuted
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = SleekPrimary,
    onPrimary = Color.White,
    primaryContainer = SleekPrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = SleekSecondary,
    background = Color(0xFF0F172A),
    onBackground = Color.White,
    surface = Color(0xFF1E293B),
    onSurface = Color.White
  )

@Composable
fun MochiTheme(
  darkTheme: Boolean = false, // Sleek interface uses light lavender backdrop by default
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MochiTheme(darkTheme = darkTheme, content = content)
}

