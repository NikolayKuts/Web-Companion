package com.cambridgedictionary

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val partsOfSpeech: List<PartsOfSpeech>,
    val text: String
)