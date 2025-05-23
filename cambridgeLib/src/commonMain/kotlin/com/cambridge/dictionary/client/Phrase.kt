package com.cambridge.dictionary.client

import kotlinx.serialization.Serializable

@Serializable
data class Phrase(
    val examples: List<String>,
    val explanation: String,
    val level: String,
    val text: String,
    val translation: String,
    val usage: String
)