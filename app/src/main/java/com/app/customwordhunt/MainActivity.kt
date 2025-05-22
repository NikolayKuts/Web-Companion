package com.app.customwordhunt

import com.cambridgedictionary.CambridgeClient
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.app.customwordhunt.ui.theme.CustomWordHuntTheme
import com.app.customwordhunt.yandexApi.entities.YandexWordInfoProvider
import com.lib.lokdroid.core.LoKdroid
import com.lib.lokdroid.data.default_implementation.FormaterBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    var textSpeaker: TextSpeaker? = null
    private val yandexProvider by lazy { YandexWordInfoProvider(context = application) }
    private val cambridgeClient by lazy { CambridgeClient }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrievedWord = retrieveSmartSelectedWord() ?: "welcome"

        Log.d("MainActivity", "retrievedWord: $retrievedWord")

        textSpeaker = TextSpeaker(context = application)

        enableEdgeToEdge()

        setContent {
            CustomWordHuntTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Screen(
//                        modifier = Modifier.padding(innerPadding),
//                        word = retrievedWord,
//                        selectionScript = selectionScript(),
//                        onWordSelected = {
//                            if (it.isNotEmpty()) {
//                                copyToClipboard(it)
//                            }
//                            Log.d("TAG", "word selected: $it")
//                        },
//                        onKlafButtonClick = { startKlafActivityWithCheck(selection = it) },
//                    )

                    SearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        yandexProvider = yandexProvider,
                        cambridgeClient = cambridgeClient,
                        finishRequest = { finish() },
                        onKlafButtonClick = { startKlafActivityWithCheck(selection = it) },
                        onAudioPlayButtonClick = { textSpeaker?.speak(it) },
                        onCambridgeButtonClick = {
                            lifecycleScope.launch(Dispatchers.IO) {
//                                val wordAsString = cambridgeClient.fetchWord(it.lowercase())
//                                Log.d( "TAG", "word as String: $wordAsString")

                                val word = cambridgeClient.fetchWordData(it.lowercase())
                                Log.d("TAG", "selection: $it; word: $word")
                            }
                        },
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

    private fun selectionScript(): String {
        return """
    document.addEventListener("selectionchange", function() {
        let selection = window.getSelection().toString();
        AndroidInterface.onTextSelected(selection);
    });
""".trimIndent()
//        return """
//                        document.addEventListener("selectionchange", function() {
//                            let selection = window.getSelection().toString();
//                            if (selection.length > 0) {
//                                AndroidInterface.onTextSelected(selection);
//                            }
//                        });
//                        """.trimIndent()
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