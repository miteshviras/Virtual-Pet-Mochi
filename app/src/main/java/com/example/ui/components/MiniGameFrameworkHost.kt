package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameSession
import com.example.data.GameSessionStatus
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark

@Composable
fun MiniGameFrameworkHost(
  session: GameSession,
  onPause: () -> Unit,
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier,
  gameContent: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(420.dp)
      .clip(RoundedCornerShape(28.dp))
      .background(Color(0xFF0F172A))
      .border(2.dp, Color(0xFF334155), RoundedCornerShape(28.dp))
      .testTag("mini_game_framework_host")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. TOP GAME HUD BAR
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E293B))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(session.icon, fontSize = 24.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = session.activityName,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "${session.difficulty.displayName} • Best: ${session.highScore}",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Combo multiplier badge
          if (session.combo > 1) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444))))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${session.multiplier}x (${session.combo}🔥)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          // Score badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF334155))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Score: ${session.currentScore}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = CoinAmber
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = onPause,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFF475569))
              .testTag("pause_game_button")
          ) {
            Icon(
              imageVector = Icons.Default.Pause,
              contentDescription = "Pause Game",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Timer Bar
      val timerProgress = (session.timeRemainingSeconds.toFloat() / session.maxTimeSeconds.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
      LinearProgressIndicator(
        progress = timerProgress,
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp),
        color = if (timerProgress < 0.25f) Color(0xFFEF4444) else SleekPrimary,
        trackColor = Color(0xFF334155)
      )

      // 2. ACTIVE GAME VIEWPORT CONTENT
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        gameContent()

        // 3. PAUSE OVERLAY
        androidx.compose.animation.AnimatedVisibility(
          visible = session.status == GameSessionStatus.PAUSED,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.82f))
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text("Game Paused ⏸️", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
              Spacer(modifier = Modifier.height(8.dp))
              Text("Current Score: ${session.currentScore} | Max Combo: ${session.maxCombo}x", fontSize = 13.sp, color = Color.LightGray)

              Spacer(modifier = Modifier.height(24.dp))

              Button(
                onClick = onResume,
                colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                modifier = Modifier
                  .fillMaxWidth(0.8f)
                  .testTag("resume_game_button"),
                shape = RoundedCornerShape(16.dp)
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Resume Game", fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(16.dp)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Restart Level", fontWeight = FontWeight.Bold)
              }

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(16.dp)
              ) {
                Text("Quit to Hub", fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // 4. VICTORY / COMPLETED OVERLAY
        androidx.compose.animation.AnimatedVisibility(
          visible = session.status == GameSessionStatus.COMPLETED,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.88f))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              if (session.isNewHighScore) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEC4899))))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                  Text("🏆 NEW HIGH SCORE! 🏆", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
              }

              Text(
                text = "Victory! 🎉",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Star Rating Row
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                for (i in 1..3) {
                  val isFilled = i <= session.starsEarned
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star $i",
                    tint = if (isFilled) Color(0xFFFBBF24) else Color(0xFF475569),
                    modifier = Modifier.size(36.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Score Breakdown Card
              Surface(
                modifier = Modifier.fillMaxWidth(0.9f),
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Final Score", fontSize = 13.sp, color = Color.Gray)
                    Text("${session.currentScore} pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Max Combo", fontSize = 13.sp, color = Color.Gray)
                    Text("${session.maxCombo}x 🔥", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Coins Earned", fontSize = 13.sp, color = Color.Gray)
                    Text("+${session.coinsEarned} ✨", fontSize = 14.sp, fontWeight = FontWeight.Black, color = CoinAmber)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("XP Earned", fontSize = 13.sp, color = Color.Gray)
                    Text("+${session.xpEarned} ⚡", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF3B82F6))
                  }
                }
              }

              Spacer(modifier = Modifier.height(20.dp))

              Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = onRestart,
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(14.dp)
                ) {
                  Text("Play Again", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = onExit,
                  colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("exit_game_button"),
                  shape = RoundedCornerShape(14.dp)
                ) {
                  Text("Collect Rewards ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        // 5. FAILED OVERLAY
        androidx.compose.animation.AnimatedVisibility(
          visible = session.status == GameSessionStatus.FAILED,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.85f))
              .padding(20.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text("Time's Up! ⏰", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
              Spacer(modifier = Modifier.height(8.dp))
              Text("Score achieved: ${session.currentScore}", fontSize = 14.sp, color = Color.LightGray)

              Spacer(modifier = Modifier.height(20.dp))

              Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = onRestart,
                  colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(14.dp)
                ) {
                  Text("Try Again", fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = onExit,
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(14.dp)
                ) {
                  Text("Exit", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
