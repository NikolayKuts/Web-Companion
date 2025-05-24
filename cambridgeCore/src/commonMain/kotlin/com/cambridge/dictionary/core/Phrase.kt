package com.cambridge.dictionary.core

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