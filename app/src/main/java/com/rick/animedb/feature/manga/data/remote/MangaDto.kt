package com.rick.animedb.feature.manga.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MangaListResponseDto(
    val result: String? = null,
    val response: String? = null,
    val data: List<MangaDto>? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val total: Int? = null,
)

@Serializable
data class MangaEntityResponseDto(
    val result: String? = null,
    val response: String? = null,
    val data: MangaDto? = null,
)

@Serializable
data class MangaDto(
    val id: String? = null,
    val type: String? = null,
    val attributes: MangaAttributesDto? = null,
    val relationships: List<RelationshipDto>? = null,
)

@Serializable
data class MangaAttributesDto(
    val title: Map<String, String>? = null,
    val altTitles: List<Map<String, String>>? = null,
    val description: Map<String, String>? = null,
    val isLocked: Boolean? = null,
    val links: Map<String, String>? = null,
    val officialLinks: JsonElement? = null,
    val originalLanguage: String? = null,
    val lastVolume: String? = null,
    val lastChapter: String? = null,
    val publicationDemographic: String? = null,
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String? = null,
    val tags: List<TagDto>? = null,
    val state: String? = null,
    val chapterNumbersResetOnNewVolume: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val version: Int? = null,
    val availableTranslatedLanguages: List<String>? = null,
    val latestUploadedChapter: String? = null,
)

@Serializable
data class TagDto(
    val id: String? = null,
    val type: String? = null,
    val attributes: TagAttributesDto? = null,
    val relationships: List<RelationshipDto>? = null,
)

@Serializable
data class TagAttributesDto(
    val name: Map<String, String>? = null,
    val description: Map<String, String>? = null,
    val group: String? = null,
    val version: Int? = null,
)

@Serializable
data class RelationshipDto(
    val id: String? = null,
    val type: String? = null,
    val related: String? = null,
    val attributes: RelationshipAttributesDto? = null,
)

@Serializable
data class RelationshipAttributesDto(
    val fileName: String? = null,
    val description: String? = null,
    val volume: String? = null,
    val locale: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val biography: Map<String, String>? = null,
    val twitter: String? = null,
    val pixiv: String? = null,
    val melonBook: String? = null,
    val fanBox: String? = null,
    val booth: String? = null,
    val namicomi: String? = null,
    val nicoVideo: String? = null,
    val skeb: String? = null,
    val fantia: String? = null,
    val tumblr: String? = null,
    val youtube: String? = null,
    val weibo: String? = null,
    val naver: String? = null,
    val website: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val version: Int? = null,
)
