package com.rick.animedb.feature.manga.domain.model

data class Manga(
    val id: String,
    val title: String,
    val altTitle: String?,
    val description: String?,
    val status: String?,
    val year: Int?,
    val contentRating: String?,
    val tags: List<String>,
    val coverUrl: String?,
    val rawJson: String,
)
