package com.cambridge.dictionary.client

import kotlinx.serialization.Serializable

@Serializable
data class Meaning(
    val examples: List<String>,
    val explanation: String,
    val guidWord: String,
    val level: String,
    val translation: String
)