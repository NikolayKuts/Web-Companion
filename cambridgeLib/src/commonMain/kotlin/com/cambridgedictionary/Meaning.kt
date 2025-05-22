package com.cambridgedictionary

import kotlinx.serialization.Serializable

@Serializable
data class Meaning(
    val examples: List<String>,
    val explanation: String,
    val guidWord: String,
    val level: String,
    val translation: String
)