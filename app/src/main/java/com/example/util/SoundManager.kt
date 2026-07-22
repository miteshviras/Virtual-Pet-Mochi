package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundManager {
  private var toneGenerator: ToneGenerator? = null
  var isSoundEnabled: Boolean = true
  var isHapticsEnabled: Boolean = true

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (e: Exception) {
      Log.e("SoundManager", "Failed to initialize ToneGenerator", e)
    }
  }

  private fun playTone(toneType: Int, durationMs: Int) {
    if (!isSoundEnabled) return
    try {
      toneGenerator?.startTone(toneType, durationMs)
    } catch (e: Exception) {
      Log.e("SoundManager", "Error playing tone", e)
    }
  }

  fun playTapSound() {
    playTone(ToneGenerator.TONE_PROP_BEEP, 80)
  }

  fun playPropInteractionSound() {
    playTone(ToneGenerator.TONE_DTMF_A, 120)
  }

  fun playRoomSwipeSound() {
    playTone(ToneGenerator.TONE_PROP_PROMPT, 60)
  }

  fun playCoinCollectSound() {
    playTone(ToneGenerator.TONE_DTMF_0, 100)
  }

  fun playCoinSound() {
    playCoinCollectSound()
  }

  fun playLevelUpSound() {
    playTone(ToneGenerator.TONE_DTMF_S, 250)
  }

  fun playBathSplashSound() {
    playTone(ToneGenerator.TONE_SUP_PIP, 100)
  }

  fun playEatingSound() {
    playTone(ToneGenerator.TONE_DTMF_1, 100)
  }

  fun playDrinkingSound() {
    playTone(ToneGenerator.TONE_DTMF_2, 100)
  }

  fun playBrushSound() {
    playTone(ToneGenerator.TONE_DTMF_3, 90)
  }

  fun playCuddleSound() {
    playTone(ToneGenerator.TONE_DTMF_4, 120)
  }

  fun playPraiseSound() {
    playTone(ToneGenerator.TONE_DTMF_5, 140)
  }

  fun playMedicineSound() {
    playTone(ToneGenerator.TONE_DTMF_6, 110)
  }

  fun playPurchaseSound() {
    playTone(ToneGenerator.TONE_DTMF_B, 150)
  }

  fun playRewardSound() {
    playTone(ToneGenerator.TONE_DTMF_C, 200)
  }

  fun playErrorSound() {
    playTone(ToneGenerator.TONE_PROP_NACK, 180)
  }

  fun playCategorySwitchSound() {
    playTone(ToneGenerator.TONE_PROP_ACK, 50)
  }

  fun playEquipSound() {
    playTone(ToneGenerator.TONE_DTMF_3, 120)
  }

  fun playRemoveSound() {
    playTone(ToneGenerator.TONE_PROP_PROMPT, 90)
  }

  fun playSelectionSound() {
    playTone(ToneGenerator.TONE_PROP_BEEP, 50)
  }

  fun playConfirmSound() {
    playTone(ToneGenerator.TONE_DTMF_S, 200)
  }

  fun playPlacementSound() {
    playTone(ToneGenerator.TONE_DTMF_A, 140)
  }

  fun playMoveSound() {
    playTone(ToneGenerator.TONE_PROP_BEEP, 60)
  }

  fun playRotateSound() {
    playTone(ToneGenerator.TONE_DTMF_2, 80)
  }

  fun playCancelSound() {
    playTone(ToneGenerator.TONE_PROP_NACK, 100)
  }

  fun playStartGameSound() {
    playTone(ToneGenerator.TONE_DTMF_D, 200)
  }

  fun playPauseSound() {
    playTone(ToneGenerator.TONE_PROP_PROMPT, 100)
  }

  fun playResumeSound() {
    playTone(ToneGenerator.TONE_PROP_BEEP, 100)
  }

  fun playComboSound() {
    playTone(ToneGenerator.TONE_DTMF_A, 150)
  }

  fun playVictorySound() {
    playTone(ToneGenerator.TONE_DTMF_S, 350)
  }

  fun playDefeatSound() {
    playTone(ToneGenerator.TONE_PROP_NACK, 300)
  }
}

