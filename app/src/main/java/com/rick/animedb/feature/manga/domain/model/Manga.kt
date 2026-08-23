package com.rick.animedb.feature.manga.domain.model

data class Manga(
    val id: String,
    val title: String,
    val altTitle: String? = null,
    val description: String? = null,
    val status: String? = null,
    val year: Int? = null,
    val contentRating: String? = null,
    val tags: List<MangaTag> = emptyList(),
    val coverUrl: String? = null,
    val type: String? = null,
    val titles: List<LocalizedText> = emptyList(),
    val altTitles: List<LocalizedText> = emptyList(),
    val descriptions: List<LocalizedText> = emptyList(),
    val originalLanguage: String? = null,
    val publicationDemographic: String? = null,
    val lastVolume: String? = null,
    val lastChapter: String? = null,
    val state: String? = null,
    val isLocked: Boolean? = null,
    val chapterNumbersResetOnNewVolume: Boolean? = null,
    val version: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val latestUploadedChapter: String? = null,
    val availableTranslatedLanguages: List<String> = emptyList(),
    val links: List<LabeledValue> = emptyList(),
    val credits: List<MangaCredit> = emptyList(),
    val related: List<LabeledValue> = emptyList(),
    val coverFileName: String? = null,
    val coverLocale: String? = null,
    val coverVolume: String? = null,
    val coverDescription: String? = null,
)

data class LabeledValue(
    val label: String,
    val value: String,
    val url: String? = null,
)

data class MangaCredit(
    val name: String,
    val role: MangaCreditRole,
    val biographies: List<LocalizedText> = emptyList(),
    val links: List<LabeledValue> = emptyList(),
)

enum class MangaCreditRole {
    Author,
    Artist,
}
