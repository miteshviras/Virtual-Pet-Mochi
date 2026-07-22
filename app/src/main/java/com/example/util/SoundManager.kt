package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

object SoundManager {
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (e: Exception) {
      Log.e("SoundManager", "Failed to initialize ToneGenerator", e)
    }
  }

  fun playTapSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
  }

  fun playPropInteractionSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 120)
  }

  fun playRoomSwipeSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 60)
  }

  fun playCoinCollectSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 100)
  }

  fun playCoinSound() {
    playCoinCollectSound()
  }

  fun playLevelUpSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_S, 250)
  }

  fun playBathSplashSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_SUP_PIP, 100)
  }

  fun playEatingSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 100)
  }

  fun playDrinkingSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_2, 100)
  }

  fun playBrushSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 90)
  }

  fun playCuddleSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_4, 120)
  }

  fun playPraiseSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 140)
  }

  fun playMedicineSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_6, 110)
  }

  fun playPurchaseSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_B, 150)
  }

  fun playRewardSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_C, 200)
  }

  fun playErrorSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
  }

  fun playCategorySwitchSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 50)
  }

  fun playEquipSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 120)
  }

  fun playRemoveSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 90)
  }

  fun playSelectionSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
  }

  fun playConfirmSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_S, 200)
  }

  fun playPlacementSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 140)
  }

  fun playMoveSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
  }

  fun playRotateSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_2, 80)
  }

  fun playCancelSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 100)
  }

  fun playStartGameSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 200)
  }

  fun playPauseSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
  }

  fun playResumeSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
  }

  fun playComboSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 150)
  }

  fun playVictorySound() {
    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_S, 350)
  }

  fun playDefeatSound() {
    toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 300)
  }
}
