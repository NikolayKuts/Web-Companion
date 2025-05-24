package com.app.webCompanion

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.cambridge.dictionary.client.CambridgeClient
import com.app.webCompanion.translation.Translator
import com.app.webCompanion.ui.theme.CustomWordHuntTheme
import com.app.webCompanion.yandexApi.entities.YandexWordInfoProvider
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.data.default_implementation.FormaterBuilder

class MainActivity : ComponentActivity() {

    companion object {

        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
        const val KLAF_PACKAGE_NAME = "com.kuts.klaf"
    }

    init {
        LoKdroid.initialize(
            formatter = FormaterBuilder()
                .withPointer()
                .space()
                .withLineReference()
                .space()
                .message()
                .build()
        )
    }

    private var textSpeaker: TextSpeaker? = null
    private val yandexProvider by lazy { YandexWordInfoProvider(context = application) }
    private val cambridgeClient by lazy { CambridgeClient }
    private var translator: Translator? = Translator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrievedWord = retrieveSmartSelectedWord() ?: "welcome"

        Log.d("MainActivity", "retrievedWord: $retrievedWord")

        textSpeaker = TextSpeaker(context = application)

        enableEdgeToEdge()

        setContent {
            CustomWordHuntTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        yandexProvider = yandexProvider,
                        cambridgeClient = cambridgeClient,
                        finishRequest = { finish() },
                        onKlafButtonClick = { startKlafActivityWithCheck(selection = it) },
                        onAudioPlayButtonClick = { textSpeaker?.speak(it) },
                        onSelectionChange = { selection ->
                            translator?.translate(text = selection)
                        },
                        translation = translator?.translation?.collectAsState("")!!
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textSpeaker?.stop()
        textSpeaker = null
    }

    private fun retrieveSmartSelectedWord(): String? = this.intent.run {
        if (type?.startsWith(MIME_TYPE_TEXT_PLAIN) == true) {
            this.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else null
    }

    private fun startKlafActivityWithCheck(selection: String) {
        try {
            val intent = Intent(Intent.ACTION_PROCESS_TEXT).apply {
                type = MIME_TYPE_TEXT_PLAIN
                putExtra(Intent.EXTRA_PROCESS_TEXT, selection)
                putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
                setPackage(KLAF_PACKAGE_NAME)
            }
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Log.d("TAG", "Application not found")
        }
    }

    fun Context.copyToClipboard(text: String) {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)

        clipboard.setPrimaryClip(clip)
    }
}

class WebAppInterface(val onSelected: (String) -> Unit) {

    @JavascriptInterface
    fun onTextSelected(text: String) {
        onSelected(text)
    }
}