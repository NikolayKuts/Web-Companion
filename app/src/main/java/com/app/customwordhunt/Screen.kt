package com.app.customwordhunt

import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    word: String,
    selectionScript: String,
    onWordSelected: (String) -> Unit,
    onKlafButtonClick: (String) -> Unit,
) {
    Box(modifier) {
        var klafButtonEnabled by remember { mutableStateOf(false) }
        var selectedWord by remember { mutableStateOf("") }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
//                webViewClient = CustomWebViewClient()
//                webChromeClient = CustomWebChromeClient()

                    settings.apply {
                        javaScriptEnabled = true
                    }

                    setWebContentsDebuggingEnabled(true)
                    loadUrl("https://wooordhunt.ru/word/$word")

                    addJavascriptInterface(
                        WebAppInterface { selectedText ->
                            onWordSelected(selectedText)
                            selectedWord = selectedText
                            klafButtonEnabled = selectedText.isNotEmpty()
                        },
                        "AndroidInterface"
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            view?.evaluateJavascript(
                                selectionScript,
                                null
                            )
                        }
                    }
                }
            },
            update = { webView -> },
        )

        if (klafButtonEnabled) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onClick = { onKlafButtonClick(selectedWord) },
            ) {
                Text("To Klaf")
            }
        }
    }
}