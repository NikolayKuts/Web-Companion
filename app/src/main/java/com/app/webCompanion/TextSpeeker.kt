package com.app.webCompanion

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextSpeaker(context: Context) {

    companion object {

        const val EN_US_X_IOM_VOICE_NAME = "en-us-x-iom-network"
        const val EN_US_X_IOL_VOICE_NAME = "en-us-x-iol-network"
        const val EN_US_X_TPC_VOICE_NAME = "en-us-x-tpc-network"
    }

    private val listener: TextToSpeech.OnInitListener = createListener()
    private val textToSpeech = TextToSpeech(context, listener)

    private fun createListener(): TextToSpeech.OnInitListener {
        return TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                } else {
                    val voices = textToSpeech.voices

                    textToSpeech.voice = voices.firstOrNull { it.name == EN_US_X_IOM_VOICE_NAME }
                        ?: voices.firstOrNull { it.name == EN_US_X_IOL_VOICE_NAME }
                                ?: voices.firstOrNull { it.name == EN_US_X_TPC_VOICE_NAME }
                                ?: return@OnInitListener
                }
            } else {
            }
        }
    }

    fun speak(text: String) {
        textToSpeech.stop()
        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    fun stop() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}