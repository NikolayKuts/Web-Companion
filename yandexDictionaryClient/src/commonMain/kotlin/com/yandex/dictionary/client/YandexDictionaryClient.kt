package com.yandex.dictionary.client

import com.yandex.dictionary.core.entities.YandexWordInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class YandexDictionaryClient {

    companion object {

        private var instance: YandexDictionaryClient? = null
        private var apiKey: String? = null

        private const val BASE_URL = "https://dictionary.yandex.net/"
        private const val PATH = "api/v1/dicservice.json/lookup"
        private const val TIMEOUT = 10000L
        private const val YANDEX_CERTIFICATE_ALIAS = "yandex_certificate_alias"
        private const val CERTIFICATE_FACTORY_TYPE = "X.509"

        fun getInstance(apiKey: String): YandexDictionaryClient = synchronized(this) {
            instance ?: YandexDictionaryClient()
                .also {
                    instance = it
                    this.apiKey = apiKey
                }
        }
    }

    private val client = HttpClient {
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

    suspend fun fetchWordInfo(word: String): YandexWordInfo {
        val wordToFetch = word.lowercase()

        val key = apiKey ?: throw IllegalStateException("API key not set")
        val url = buildUrl(apiKey = key, word = wordToFetch)
        val wordInfo = client.get(url).body<YandexWordInfo>()

        return wordInfo
    }

    private fun buildUrl(apiKey: String, word: String): String {
        return "$PATH?key=$apiKey&lang=en-ru&text=$word"
    }
}