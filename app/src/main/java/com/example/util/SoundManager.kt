package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import java.util.concurrent.Executors

enum class PetVoiceType {
  CHIRP,      // Tap / Pet
  GREETING,   // Hello / Mo-chi!
  GIGGLE,     // Joyful laughter / Play
  PURR,       // Cuddle / Praise
  YAWN,       // Bedroom / Sleepy
  YUMMY,      // Kitchen / Feeding
  SAD         // Low stats / Hungry
}

object SoundManager {
  private var toneGenerator: ToneGenerator? = null
  var isSoundEnabled: Boolean = true
  var isHapticsEnabled: Boolean = true
  private val audioExecutor = Executors.newSingleThreadExecutor()

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

  fun playPetVoice(type: PetVoiceType = PetVoiceType.CHIRP) {
    if (!isSoundEnabled) return
    audioExecutor.execute {
      try {
        val sampleRate = 22050
        val numSamples: Int

        when (type) {
          PetVoiceType.CHIRP -> {
            // Cute rising chirp ("Nya!")
            numSamples = (sampleRate * 0.22).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val freq = 850f + (750f * Math.sin(t * Math.PI / 2).toFloat())
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = (Math.sin(phase.toDouble()) * 0.7 + Math.sin((phase * 2f).toDouble()) * 0.3)
              val envelope = Math.sin(t * Math.PI).toFloat()
              buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.6f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
          PetVoiceType.GREETING -> {
            // Two syllable chirp ("Mo-chi!")
            numSamples = (sampleRate * 0.35).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val freq = if (t < 0.4f) {
                800f + 300f * (t / 0.4f)
              } else {
                1200f + 400f * Math.sin((t - 0.4f) / 0.6f * Math.PI).toFloat()
              }
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = (Math.sin(phase.toDouble()) * 0.75 + Math.sin((phase * 2f).toDouble()) * 0.25)
              val envelope = Math.sin(t * Math.PI).toFloat()
              buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.6f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
          PetVoiceType.GIGGLE -> {
            // Staccato 3-pulse giggle ("Hee-hee-hee!")
            numSamples = (sampleRate * 0.32).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val pulse = (t * 3f).toInt().coerceIn(0, 2)
              val pulseT = (t * 3f) - pulse
              val baseFreq = 1350f + (pulse * 200f)
              val freq = baseFreq + 150f * Math.sin(pulseT * Math.PI).toFloat()
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = Math.sin(phase.toDouble())
              val envelope = Math.sin(pulseT * Math.PI).toFloat().coerceAtLeast(0f)
              buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.55f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
          PetVoiceType.PURR -> {
            // Loving cute purr with warm tremolo
            numSamples = (sampleRate * 0.4).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val tremolo = 0.7f + 0.3f * Math.sin(t * 30.0 * Math.PI).toFloat()
              val freq = 900f + 250f * Math.sin(t * Math.PI).toFloat()
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = Math.sin(phase.toDouble())
              val envelope = Math.sin(t * Math.PI).toFloat()
              buffer[i] = (sample * tremolo * envelope * Short.MAX_VALUE * 0.6f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
          PetVoiceType.YAWN -> {
            // Gentle cute yawn (descending pitch sweep)
            numSamples = (sampleRate * 0.45).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val freq = 1200f - 550f * Math.sin(t * Math.PI / 2).toFloat()
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = Math.sin(phase.toDouble())
              val envelope = Math.sin(t * Math.PI).toFloat()
              buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.5f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
          PetVoiceType.YUMMY -> {
            // Cute eating voice ("Nom nom!")
            numSamples = (sampleRate * 0.3).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val pulse = (t * 2f).toInt().coerceIn(0, 1)
              val pulseT = (t * 2f) - pulse
              val freq = 950f + 350f * Math.sin(pulseT * Math.PI).toFloat()
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = Math.sin(phase.toDouble())
              val envelope = Math.sin(pulseT * Math.PI).toFloat().coerceAtLeast(0f)
              buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.6f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
          PetVoiceType.SAD -> {
            // Soft gentle sad meow
            numSamples = (sampleRate * 0.38).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
              val t = i.toFloat() / numSamples
              val freq = 800f + 200f * Math.sin(t * Math.PI).toFloat() - (100f * t)
              val phase = 2f * Math.PI.toFloat() * freq * (i.toFloat() / sampleRate)
              val sample = Math.sin(phase.toDouble())
              val envelope = Math.sin(t * Math.PI).toFloat()
              buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.5f).toInt().toShort()
            }
            playPcmBuffer(buffer, sampleRate)
          }
        }
      } catch (e: Exception) {
        Log.e("SoundManager", "Error playing pet voice", e)
      }
    }
  }

  private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
    try {
      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      Thread.sleep((buffer.size.toFloat() / sampleRate * 1000).toLong() + 30)
      track.stop()
      track.release()
    } catch (e: Exception) {
      Log.e("SoundManager", "AudioTrack playback error", e)
    }
  }
}

