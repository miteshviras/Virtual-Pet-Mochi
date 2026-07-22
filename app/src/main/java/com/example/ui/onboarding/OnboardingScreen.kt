package com.example.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PetEmotion
import com.example.data.PetEntity
import com.example.data.RoomType
import com.example.ui.components.MochiPetView
import com.example.ui.components.ParticleEffectOverlay
import com.example.ui.components.ParticleType
import com.example.ui.theme.CoinAmber
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekPrimaryContainer
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.util.SoundManager

private val RANDOM_PET_NAMES = listOf(
  "Mochi", "Boba", "Matcha", "Pudding", "Cookie", "Miso", "Taro", "Kiki", "Coco", "Peach", "Sesame", "Nori", "Chai", "Waffles"
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
  petState: PetEntity,
  onCompleteOnboarding: (String) -> Unit
) {
  var currentPage by remember { mutableIntStateOf(0) }
  var petNameInput by remember { mutableStateOf("Mochi") }
  val focusManager = LocalFocusManager.current

  val totalPages = 5

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFFFFFBEB),
            Color(0xFFF1F5F9)
          )
        )
      )
      .testTag("onboarding_screen")
  ) {
    // Particle burst effect on final screen
    if (currentPage == 4) {
      ParticleEffectOverlay(
        triggerKey = currentPage,
        type = ParticleType.CONFETTI,
        particleCount = 30
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Header: Logo & Skip Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(SleekPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text("🐾", fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Mochi Pet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = SleekPrimary
          )
        }

        if (currentPage < totalPages - 1) {
          TextButton(
            onClick = {
              SoundManager.playTapSound()
              currentPage = totalPages - 1
            },
            modifier = Modifier.testTag("onboarding_skip_button")
          ) {
            Text("Skip", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SleekTextMuted)
          }
        }
      }

      // Page Progress Indicators
      Row(
        modifier = Modifier.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        repeat(totalPages) { index ->
          val isSelected = index == currentPage
          Box(
            modifier = Modifier
              .height(8.dp)
              .width(if (isSelected) 28.dp else 8.dp)
              .clip(CircleShape)
              .background(if (isSelected) SleekPrimary else Color(0xFFCBD5E1))
          )
        }
      }

      // Animated Page Content Area
      AnimatedContent(
        targetState = currentPage,
        transitionSpec = {
          if (targetState > initialState) {
            slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
          } else {
            slideInHorizontally { width -> -width } + fadeIn() with slideOutHorizontally { width -> width } + fadeOut()
          }
        },
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) { page ->
        when (page) {
          0 -> OnboardingWelcomePage(petState)
          1 -> OnboardingMeetPetPage(petState)
          2 -> OnboardingHowToPlayPage()
          3 -> OnboardingNamePetPage(
            petName = petNameInput,
            onNameChange = { petNameInput = it },
            onRandomize = {
              SoundManager.playSelectionSound()
              petNameInput = RANDOM_PET_NAMES.random()
            },
            onDone = { focusManager.clearFocus() }
          )
          4 -> OnboardingFinalReadyPage(
            petName = petNameInput.ifBlank { "Mochi" },
            petState = petState
          )
        }
      }

      // Bottom Navigation Buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (currentPage > 0 && currentPage < totalPages - 1) {
          Button(
            onClick = {
              SoundManager.playTapSound()
              currentPage--
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
            modifier = Modifier
              .height(52.dp)
              .testTag("onboarding_back_button")
          ) {
            Text("Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
          }
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        if (currentPage < totalPages - 1) {
          Button(
            onClick = {
              SoundManager.playConfirmSound()
              currentPage++
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
            modifier = Modifier
              .height(52.dp)
              .testTag("onboarding_next_button")
          ) {
            Text("Next ➔", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        } else {
          Button(
            onClick = {
              SoundManager.playVictorySound()
              onCompleteOnboarding(petNameInput.ifBlank { "Mochi" })
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .shadow(8.dp, RoundedCornerShape(20.dp))
              .testTag("onboarding_start_adventure_button")
          ) {
            Text(
              "Start Adventure! 🚀",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

@Composable
private fun OnboardingWelcomePage(petState: PetEntity) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(180.dp)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      MochiPetView(
        pet = petState,
        room = RoomType.PLAY,
        emotion = PetEmotion.HAPPY,
        notificationMsg = null,
        onPetClick = {},
        modifier = Modifier.size(160.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "Welcome to Mochi!",
      fontSize = 28.sp,
      fontWeight = FontWeight.Black,
      color = SleekTextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Your adorable virtual companion is excited to meet you! Take care of Mochi, decorate rooms, play mini-games, and collect cute outfits.",
      fontSize = 15.sp,
      color = SleekTextMuted,
      textAlign = TextAlign.Center,
      lineHeight = 22.sp,
      modifier = Modifier.padding(horizontal = 16.dp)
    )
  }
}

@Composable
private fun OnboardingMeetPetPage(petState: PetEntity) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Meet Your Pet 💖",
      fontSize = 24.sp,
      fontWeight = FontWeight.Black,
      color = SleekTextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
      modifier = Modifier
        .size(140.dp),
      contentAlignment = Alignment.Center
    ) {
      MochiPetView(
        pet = petState,
        room = RoomType.PLAY,
        emotion = PetEmotion.EXCITED,
        notificationMsg = null,
        onPetClick = {},
        modifier = Modifier.size(130.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      NeedInfoCard(icon = "🍎", title = "Yummy Snacks", desc = "Feed Mochi when hunger gets low.")
      NeedInfoCard(icon = "🎮", title = "Playful Fun", desc = "Play mini-games to boost happiness & earn coins!")
      NeedInfoCard(icon = "🧼", title = "Bubble Bath", desc = "Give warm baths to keep Mochi squeaky clean.")
      NeedInfoCard(icon = "🛏️", title = "Cozy Sleep", desc = "Let Mochi rest in bedroom to recharge energy.")
    }
  }
}

@Composable
private fun NeedInfoCard(icon: String, title: String, desc: String) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(SleekPrimaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Text(icon, fontSize = 20.sp)
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
        Text(desc, fontSize = 12.sp, color = SleekTextMuted)
      }
    }
  }
}

@Composable
private fun OnboardingHowToPlayPage() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "How To Play 🌟",
      fontSize = 24.sp,
      fontWeight = FontWeight.Black,
      color = SleekTextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Column(
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      HowToCard("🛋️", "Decorate Rooms", "Customize 6 rooms with sofas, lamps, plants, & wallpapers!")
      HowToCard("👗", "Dress-Up Studio", "Style your pet with cute hats, glasses, shirts, and shoes!")
      HowToCard("💰", "Earn & Shop", "Earn coins from care actions & games to buy boutique items!")
      HowToCard("🏆", "Trophies & Quests", "Complete daily tasks and unlock achievements for rewards!")
    }
  }
}

@Composable
private fun HowToCard(icon: String, title: String, desc: String) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color(0xFFFEF3C7)),
        contentAlignment = Alignment.Center
      ) {
        Text(icon, fontSize = 22.sp)
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
        Text(desc, fontSize = 12.sp, color = SleekTextMuted, lineHeight = 16.sp)
      }
    }
  }
}

@Composable
private fun OnboardingNamePetPage(
  petName: String,
  onNameChange: (String) -> Unit,
  onRandomize: () -> Unit,
  onDone: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Name Your Pet! 🏷️",
      fontSize = 24.sp,
      fontWeight = FontWeight.Black,
      color = SleekTextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Give your new companion a lovely name.",
      fontSize = 14.sp,
      color = SleekTextMuted,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
      value = petName,
      onValueChange = { if (it.length <= 16) onNameChange(it) },
      label = { Text("Pet Name") },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SleekPrimary,
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
      ),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = { onDone() }),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("onboarding_pet_name_input")
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = onRandomize,
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7)),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("onboarding_randomize_name_button")
    ) {
      Text("🎲 Randomize Name", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
    }
  }
}

@Composable
private fun OnboardingFinalReadyPage(
  petName: String,
  petState: PetEntity
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Everything is Ready! ✨",
      fontSize = 26.sp,
      fontWeight = FontWeight.Black,
      color = SleekTextDark,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "$petName is waiting for you in the playroom!",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = SleekPrimary,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    Box(
      modifier = Modifier.size(160.dp),
      contentAlignment = Alignment.Center
    ) {
      MochiPetView(
        pet = petState,
        room = RoomType.PLAY,
        emotion = PetEmotion.CELEBRATING,
        notificationMsg = null,
        onPetClick = {},
        modifier = Modifier.size(150.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("🎉 Welcome Gift Package", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SleekTextDark)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          horizontalArrangement = Arrangement.SpaceAround,
          modifier = Modifier.fillMaxWidth()
        ) {
          GiftBadge("💰 2,450 Coins", "Starter Balance")
          GiftBadge("🍎 Yummy Foods", "Free Snacks")
          GiftBadge("👗 Outfits", "Unlocked")
        }
      }
    }
  }
}

@Composable
private fun GiftBadge(title: String, desc: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CoinAmber)
    Text(desc, fontSize = 11.sp, color = SleekTextMuted)
  }
}
