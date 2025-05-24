package com.app.webCompanion.translation

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions.*
import com.lib.lokdroid.core.logD
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class Translator(
    from: String = TranslateLanguage.ENGLISH,
    to: String = TranslateLanguage.RUSSIAN
) {

    private var options = Builder()
        .setSourceLanguage(from)
        .setTargetLanguage(to)
        .build()

    private val englishGermanTranslator = Translation.getClient(options)

    private val _translation = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 1)
    val translation: SharedFlow<String> = _translation

    init {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        englishGermanTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                logD("Translator model downloaded")
            }.addOnFailureListener { exception ->
                logD("Error downloading translator model: $exception")
            }
    }

    fun translate(text: String) {
        logD("translate() called")
        if (text.trim().isEmpty()) {
            _translation.tryEmit("")
        }

        englishGermanTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                _translation.tryEmit(translatedText)
            }
            .addOnFailureListener { e ->

            }
    }

    fun close() {
////        val options = nul
//        val translator = Translation.getClient(options)
//        getLifecycle().addObserver(translator)
    }
}