package com.rick.animedb.feature.characters.domain.model

data class AnimeCharacter(
    val id: Int,
    val name: String,
    val nameKanji: String?,
    val nicknames: List<String>,
    val favorites: Int,
    val about: String?,
    val imageUrl: String?,
    val url: String,
)
