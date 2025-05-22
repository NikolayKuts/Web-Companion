package com.app.customwordhunt.yandexApi.entities

import com.example.englishpatterns.data.yandexApi.entities.Def
import kotlinx.serialization.Serializable

@Serializable
data class YandexWordInfo(
    val def: List<Def>?,
    val head: Head?
)