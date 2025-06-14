package com.app.webCompanion.yandexApi

import android.util.Log
import com.yandex.dictionary.client.YandexDictionaryClient
import com.yandex.dictionary.core.entities.YandexWordInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class YandexDictionaryClientWrapper(
    private val yandexDictionaryClient: YandexDictionaryClient,
) {

    fun fetchTextInfo(word: String): Flow<LoadingState<SelectedTextInfo>> = flow {
        emit(value = LoadingState.Loading)
        val yandexWordInfo = yandexDictionaryClient.fetchWordInfo(word)
        val wordInfo = yandexWordInfo.toDomainEntity()

        emit(value = LoadingState.Success(data = wordInfo))

    }.catch { throwable ->
        Log.e("TAG",("fetchWordInfo caught ERROR: ${throwable.stackTraceToString()}"))
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