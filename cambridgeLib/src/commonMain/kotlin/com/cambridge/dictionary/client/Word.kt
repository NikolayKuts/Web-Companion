package com.cambridge.dictionary.client

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val partsOfSpeech: List<PartsOfSpeech>,
    val text: String
)