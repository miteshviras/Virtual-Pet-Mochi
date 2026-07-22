package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.util.SoundManager

data class GuidePage(
  val icon: String,
  val title: String,
  val highlightBadge: String,
  val description: String,
  val tip: String
)

private val guidePages = listOf(
  GuidePage(
    icon = "🍳",
    title = "Kitchen & Meals",
    highlightBadge = "Feed & Nourish",
    description = "When Mochi gets hungry, visit the Kitchen! Choose delicious snacks or meals from your inventory to boost Hunger & Energy.",
    tip = "💡 Tip: Feed Mochi regularly to earn bonus XP and maintain high health!"
  ),
  GuidePage(
    icon = "🛁",
    title = "Bath & Hygiene",
    highlightBadge = "Clean & Wash",
    description = "A clean pet is a happy pet! Head to the Bathroom and give Mochi a warm bubble bath using soap when cleanliness drops.",
    tip = "💡 Tip: Regular baths prevent Mochi from getting dirty or sick!"
  ),
  GuidePage(
    icon = "🛏️",
    title = "Bedroom & Rest",
    highlightBadge = "Sleep & Energy",
    description = "When Mochi's energy is low, tuck your pet into the Bedroom. Tap the sleep button to let Mochi nap and recover full stamina.",
    tip = "💡 Tip: Turn off the bedroom lights to help Mochi sleep faster!"
  ),
  GuidePage(
    icon = "🧸",
    title = "Playroom & Mini-Games",
    highlightBadge = "Games & Coins",
    description = "Play exciting mini-games like Bubble Pop, Fruit Catcher, and Memory Match in the Playroom to earn Coins 🪙 and XP!",
    tip = "💡 Tip: Higher scores and combo streaks give massive coin rewards!"
  ),
  GuidePage(
    icon = "🎒",
    title = "Activity Hub",
    highlightBadge = "All Features in 1",
    description = "Tap the backpack icon 🎒 in the top header to access Dress-Up 👗, Room Decor 🛋️, Daily Quests 📜, Trophies 🏆, and the Pet Shop 🛍️!",
    tip = "💡 Tip: Complete Daily Quests every day to claim bonus coins!"
  ),
  GuidePage(
    icon = "📈",
    title = "Level Up & Customization",
    highlightBadge = "Rank Up & Unlock",
    description = "Every care action awards XP! Level up your Mochi Explorer rank to unlock cool outfits, wallpaper, furniture, and rare items.",
    tip = "💡 Tip: Check out the Pet Shop to spend your hard-earned coins!"
  )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HowToPlayGuideDialog(
  onDismiss: () -> Unit
) {
  var currentPageIndex by remember { mutableIntStateOf(0) }
  val page = guidePages[currentPageIndex]

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = Color.White,
      shadowElevation = 12.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("how_to_play_guide_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Header Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "💡", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "How to Play Guide",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = SleekTextDark
            )
          }

          TextButton(onClick = {
            SoundManager.playTapSound()
            onDismiss()
          }) {
            Text("Skip", color = SleekTextMuted, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Page Indicator Dots
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          guidePages.indices.forEach { index ->
            Box(
              modifier = Modifier
                .height(8.dp)
                .width(if (index == currentPageIndex) 24.dp else 8.dp)
                .clip(CircleShape)
                .background(if (index == currentPageIndex) SleekPrimary else Color(0xFFE2E8F0))
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Content Area
        AnimatedContent(
          targetState = page,
          transitionSpec = { fadeIn() with fadeOut() },
          label = "GuidePageTransition"
        ) { targetPage ->
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFFF8FAFC))
              .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Hero Icon Box
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(SleekPrimary.copy(alpha = 0.12f))
                .border(2.dp, SleekPrimary.copy(alpha = 0.3f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(text = targetPage.icon, fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Highlight Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SleekPrimary)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = targetPage.highlightBadge.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = targetPage.title,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = SleekTextDark,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = targetPage.description,
              fontSize = 13.sp,
              color = SleekTextDark.copy(alpha = 0.85f),
              textAlign = TextAlign.Center,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tip Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEF3C7))
                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                .padding(10.dp)
            ) {
              Text(
                text = targetPage.tip,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF92400E),
                textAlign = TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (currentPageIndex > 0) {
            Button(
              onClick = {
                SoundManager.playTapSound()
                currentPageIndex--
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = SleekTextDark),
              shape = RoundedCornerShape(16.dp)
            ) {
              Text("⬅️ Prev", fontWeight = FontWeight.Bold)
            }
          } else {
            Spacer(modifier = Modifier.width(80.dp))
          }

          if (currentPageIndex < guidePages.size - 1) {
            Button(
              onClick = {
                SoundManager.playTapSound()
                currentPageIndex++
              },
              colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
              shape = RoundedCornerShape(16.dp)
            ) {
              Text("Next ➡️", fontWeight = FontWeight.Bold, color = Color.White)
            }
          } else {
            Button(
              onClick = {
                SoundManager.playVictorySound()
                onDismiss()
              },
              colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
              shape = RoundedCornerShape(16.dp)
            ) {
              Text("Start Playing! 🚀", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    }
  }
}
