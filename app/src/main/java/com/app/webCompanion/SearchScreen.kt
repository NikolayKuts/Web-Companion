package com.app.webCompanion

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cambridge.dictionary.client.CambridgeClient
import com.app.webCompanion.yandexApi.entities.LoadingState
import com.app.webCompanion.yandexApi.entities.YandexWordInfoProvider
import com.cambridge.dictionary.core.Meaning
import com.cambridge.dictionary.core.PartsOfSpeech
import com.cambridge.dictionary.core.Phrase
import com.cambridge.dictionary.core.Word
import com.lib.lokdroid.core.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    yandexProvider: YandexWordInfoProvider,
    cambridgeClient: CambridgeClient,
    translation: State<String>,
    finishRequest: () -> Unit,
    onKlafButtonClick: (String) -> Unit,
    onAudioPlayButtonClick: (String) -> Unit,
    onSelectionChange: (String) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )
    val scope = rememberCoroutineScope()
    var selectionInfo by remember { mutableStateOf("") }
    var word by remember { mutableStateOf<Word?>(null) }
    var isTranslationEnabled by remember { mutableStateOf(false) }
    val translationText by translation

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetContent = {
            word?.let { WordDetailsScreen(word = it) }
        },
        sheetPeekHeight = 0.dp
    ) {
        Column {
            var savedUrl = remember { "" }
            var selectedText by remember { mutableStateOf("") }
            var refWebView: WebView? by remember { mutableStateOf(null) }
            var isWordHuntEnable by remember { mutableStateOf(true) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                WebViewContent(
                    urlToLoad = "https://google.com",
                    onWebViewInitialized = { webView ->
                        if (refWebView == null) {
                            refWebView = webView
                        }
                    },
                    onSelectionChange = { selextion ->
                        selectedText = selextion
                    },
                    onPageStarted = { url ->
                        savedUrl = url ?: ""
                        isWordHuntEnable = (url?.contains("wooordhunt.ru")?.not() == true)
                    },
                )

                if (isTranslationEnabled && translationText.isNotEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xff353535))
                            .padding(8.dp),
                        text = translationText,
                    )
                }
            }

            Row {
                if (isWordHuntEnable) {
                    Button(
                        onClick = {
                            if (savedUrl != "https://wooordhunt.ru/word/$selectedText") {
                                refWebView?.loadUrl("https://wooordhunt.ru/word/$selectedText")
                            }
                        }
                    ) {
                        Text(text = "Hunt")
                    }
                }

                if (selectedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier,
                        onClick = {
                            onKlafButtonClick(selectedText)
                        },
                    ) {
                        Text("Klaf")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier,
                        onClick = { onAudioPlayButtonClick(selectedText) }
                    ) {
                        Text("Play")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffe7ad76)),
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                word = cambridgeClient.fetchWordData(word = selectedText)
                                word?.let { scaffoldState.bottomSheetState.expand() }
                            }
                        }
                    ) {
                        Text("Camb")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier,
                        onClick = { isTranslationEnabled = isTranslationEnabled.not() },
                    ) {
                        Text("Tr")
                    }
                }
            }

            LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
                if (scaffoldState.bottomSheetState.currentValue != SheetValue.PartiallyExpanded) {
                    refWebView?.evaluateRiddingSelection()
                }
            }

            LaunchedEffect(key1 = selectedText) {
                withContext(Dispatchers.IO) {
                    onSelectionChange(selectedText)
                    isTranslationEnabled = false

                    if (selectedText.isNotEmpty()) {
                        yandexProvider.fetchTextInfo(word = selectedText).collect { state ->
                            when (state) {
                                is LoadingState.Error -> {}
                                LoadingState.Loading -> {}
                                LoadingState.Non -> {}
                                is LoadingState.Success -> {
                                    selectionInfo = "[ ${state.data.transcription} ]\n${
                                        state.data.translations.joinToString("\n")
                                    }"
                                }
                            }
                            logD("yandexProvider.fetchTextInfo state: $state")
                        }
                    }
                }
            }

            BackHandler() {
                refWebView?.let { webView ->
                    if (webView.canGoBack()) {
                        logD { message("isVisible -> ${scaffoldState.bottomSheetState.isVisible}") }
                        logD { message("hasExpandedState -> ${scaffoldState.bottomSheetState.hasExpandedState}") }
                        logD { message("hasPartiallyExpandedState -> ${scaffoldState.bottomSheetState.hasPartiallyExpandedState}") }
                        logD { message("value -> ${scaffoldState.bottomSheetState.currentValue.name}") }

                        if (scaffoldState.bottomSheetState.currentValue != SheetValue.PartiallyExpanded) {
                            logD("ture")
//                            scaffoldState.bottomSheetState.
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        } else {
                            logD("false")
                            selectedText = ""
                            refWebView?.goBack()
                        }
                    } else {
                        finishRequest()
                    }
                }
            }
        }
    }
}

@Composable
private fun WebViewContent(
    urlToLoad: String,
    onWebViewInitialized: (WebView) -> Unit,
    onSelectionChange: (String) -> Unit,
    onPageStarted: (String?) -> Unit,
) {
    AndroidView(
        modifier = Modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT  // Use cached resources when available
                    domStorageEnabled = true              // Required for many modern sites

//                    useWideViewPort = true
//                    javaScriptEnabled = true
//                    builtInZoomControls = false
//                    cacheMode = WebSettings.LOAD_DEFAULT
                    domStorageEnabled = true
                    databaseEnabled = true
//                    textZoom = 100
                }

                setWebContentsDebuggingEnabled(true)

                addJavascriptInterface(
                    WebAppInterface { selextion -> onSelectionChange(selextion) },
                    "AndroidInterface"
                )

                webViewClient = object : WebViewClient() {

                    override fun onPageStarted(
                        view: WebView?,
                        url: String?,
                        favicon: Bitmap?
                    ) {
                        super.onPageStarted(view, url, favicon)
                        Log.d("TAG", "Page started: $url")
                        onPageStarted(url)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        Log.d("TAG", "onPageFinished $url")
                        view?.evaluateSelectionScript()
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url.toString()
                        Log.d("TAG", "Navigating to: $url")

                        // Return false to allow WebView to load the URL
                        return false
                    }
                }

                onWebViewInitialized(this)
            }
        },
        update = { webView ->
            Log.e("TAG", "current url: ${webView.url}, new: $urlToLoad")
            webView.loadUrl(urlToLoad)
        },
    )
}

private fun WebView.evaluateSelectionScript() {
    evaluateJavascript(selectionScript()) {
        logD("evaluateSelectionScript result -> $it")
    }
}

private fun selectionScript(): String {
    return """
    document.addEventListener("selectionchange", function() {
        let selection = window.getSelection().toString();
        AndroidInterface.onTextSelected(selection);
    });
""".trimIndent()
}

private fun WebView.evaluateRiddingSelection() {
    evaluateJavascript(
        "window.getSelection().removeAllRanges();"
    ) { selectedText ->
//        onSelectionChange(selectedText.trim('"'))
    }
}

@Composable
fun WordDetailsScreen(word: Word) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
            .padding(bottom = 50.dp, top = 8.dp)
            .padding(horizontal = 16.dp),
    ) {
        item {
            Text(
                text = word.text.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        word.partsOfSpeech.forEach { part ->
            item {
                PartOfSpeechSection(part)
            }
        }
    }
}

@Composable
fun PartOfSpeechSection(pos: PartsOfSpeech) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = pos.text + if (pos.label.isNotBlank()) " (${pos.label})" else "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFDD462)
        )

        if (pos.ipas.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = pos.ipas.joinToString(" / ", prefix = "[", postfix = "]"),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFAFDA7F)
            )
        }

        pos.meanings.forEach { meaning ->
            MeaningItem(meaning)
            Spacer(modifier = Modifier.height(3.dp))
        }

        if (pos.phrases.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Phrases:",
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFF6754B)
            )
            pos.phrases.forEach { phrase ->
                PhraseItem(phrase)
                Spacer(modifier = Modifier.height(3.dp))
            }
        }
    }
}

@Composable
fun MeaningItem(meaning: Meaning) {
    Column(
        modifier = Modifier
//            .border(3.dp, color = Color(0xFFC969DA))
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0x18ffffff))
            .padding(all = 4.dp)
    ) {
        Text(
            text = meaning.explanation,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = meaning.translation,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF59BEF3)
        )
        meaning.examples.forEach { example ->
            Text(
                text = "• $example",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
fun PhraseItem(phrase: Phrase) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0x1a3ef300))
            .padding(all = 4.dp)
            .padding(start = 8.dp)
    ) {
        Text(
            text = phrase.text,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = phrase.translation,
            style = MaterialTheme.typography.bodyMedium
        )
        phrase.examples.forEach {
            Text(
                text = "• $it",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )
        }
    }
}


