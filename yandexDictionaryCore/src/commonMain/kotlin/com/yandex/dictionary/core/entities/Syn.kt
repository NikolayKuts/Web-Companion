package com.yandex.dictionary.core.entities

import kotlinx.serialization.Serializable

@Serializable
data class Syn(
    val asp: String? = null,
    val fr: Int,
    val pos: String,
    val text: String
)