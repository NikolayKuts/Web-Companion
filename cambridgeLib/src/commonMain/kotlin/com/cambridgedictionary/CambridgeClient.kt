package com.cambridgedictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object CambridgeClient {

    private const val BASE_URL = "http://4264181-lu28184.twc1.net:3000/words/"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }
    }

    suspend fun fetchWordData(word: String): Word? = try {
        val wordToFetch = word.lowercase()
        httpClient.get("$BASE_URL$wordToFetch").body<Word>()
    } catch (e: Exception) {
        println("Error fetching word data: ${e.message}")
        null
    }
}