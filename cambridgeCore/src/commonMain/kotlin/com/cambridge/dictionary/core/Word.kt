package com.cambridge.dictionary.core

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val partsOfSpeech: List<PartsOfSpeech>,
    val text: String
)