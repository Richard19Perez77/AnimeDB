package com.rick.animedb.feature.manga.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MangaListResponseDto(
    val result: String? = null,
    val data: List<MangaDto> = emptyList(),
)

@Serializable
data class MangaDto(
    val id: String,
    val attributes: MangaAttributesDto,
    val relationships: List<RelationshipDto> = emptyList(),
)

@Serializable
data class MangaAttributesDto(
    val title: Map<String, String> = emptyMap(),
    val altTitles: List<Map<String, String>> = emptyList(),
    val description: Map<String, String> = emptyMap(),
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String? = null,
    val tags: List<TagDto> = emptyList(),
)

@Serializable
data class TagDto(
    val attributes: TagAttributesDto? = null,
)

@Serializable
data class TagAttributesDto(
    val name: Map<String, String> = emptyMap(),
)

@Serializable
data class RelationshipDto(
    val id: String,
    val type: String,
    val attributes: CoverAttributesDto? = null,
)

@Serializable
data class CoverAttributesDto(
    val fileName: String? = null,
)
