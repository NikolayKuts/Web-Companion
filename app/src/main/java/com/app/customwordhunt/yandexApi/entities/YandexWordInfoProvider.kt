package com.app.customwordhunt.yandexApi.entities


import android.content.Context
import android.util.Log
import com.app.customwordhunt.common.SecretConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class YandexWordInfoProvider(context: Context) {

    companion object {

        private const val BASE_URL = "https://dictionary.yandex.net/"
        private const val PATH = "api/v1/dicservice.json/lookup"
        private const val TIMEOUT = 10000L
        private const val YANDEX_CERTIFICATE_ALIAS = "yandex_certificate_alias"
        private const val CERTIFICATE_FACTORY_TYPE = "X.509"
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest { url(urlString = BASE_URL) }
    }

    fun fetchTextInfo(word: String): Flow<LoadingState<SelectedTextInfo>> = flow {
        emit(value = LoadingState.Loading)

        val apiKey = SecretConstants.YandexApi.YANDEX_WORD_INFO_API_KEY
        val url = buildUrl(apiKey = apiKey, word = word)
        val yandexWordInfoAsString = client.get(url).body<String>()
        val wordInfo = client.get(url).body<YandexWordInfo>().toDomainEntity()

        emit(value = LoadingState.Success(data = wordInfo))

    }.catch { throwable ->
        Log.e("TAG",("fetchWordInfo caught ERROR: ${throwable.stackTraceToString()}"))
    }

    private fun buildUrl(apiKey: String, word: String): String {
        return "$PATH?key=$apiKey&lang=en-ru&text=$word"
    }
}

fun YandexWordInfo.toDomainEntity(): SelectedTextInfo = SelectedTextInfo(
    transcription = def?.firstOrNull()?.ts ?: "",
    translations = def?.flatMap { def -> def.tr.map { tr -> tr.text } } ?: emptyList()
)

data class SelectedTextInfo(
    val transcription: String = "",
    val translations: List<String> = emptyList(),
)

sealed class LoadingState<out T> {

    data object Non : LoadingState<Nothing>()

    data class Success<T>(val data: T) : LoadingState<T>()

    data object Loading : LoadingState<Nothing>()

    class Error(val value: String? = null) : LoadingState<Nothing>()
}