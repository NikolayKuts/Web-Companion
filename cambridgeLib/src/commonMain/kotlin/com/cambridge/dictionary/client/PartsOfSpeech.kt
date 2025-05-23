package com.cambridge.dictionary.client

import kotlinx.serialization.Serializable

@Serializable
data class PartsOfSpeech(
    val ipas: List<String>,
    val label: String,
    val meanings: List<Meaning>,
    val phrases: List<Phrase>,
    val pronunciations: List<String>,
    val text: String,
    val usage: String
)