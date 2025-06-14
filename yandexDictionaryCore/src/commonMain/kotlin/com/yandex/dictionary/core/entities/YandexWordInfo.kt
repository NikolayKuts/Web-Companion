package com.yandex.dictionary.core.entities

import kotlinx.serialization.Serializable

@Serializable
data class YandexWordInfo(
    val def: List<Def>?,
    val head: Head?
)