package com.rick.animedb.feature.manga.data.mapper

import com.rick.animedb.feature.manga.data.remote.MangaDto
import com.rick.animedb.feature.manga.domain.model.Manga
import kotlinx.serialization.json.Json

private const val CoverCdn = "https://uploads.mangadex.org/covers"
private val TitleLanguagePriority = listOf("en", "ja-ro", "ja", "ko-ro", "ko", "zh")

fun MangaDto.toDomain(json: Json): Manga? {
    val mangaId = id ?: return null
    val attrs = attributes
    val resolvedTitle = localizedValue(attrs?.title)
        ?: attrs?.altTitles?.firstNotNullOfOrNull(::localizedValue)
        ?: "Untitled"
    val resolvedAlt = attrs?.altTitles?.firstNotNullOfOrNull { alt ->
        localizedValue(alt)?.takeIf { it != resolvedTitle }
    }
    val coverFileName = relationships
        ?.firstOrNull { it.type == "cover_art" }
        ?.attributes
        ?.fileName

    return Manga(
        id = mangaId,
        title = resolvedTitle,
        altTitle = resolvedAlt,
        description = localizedValue(attrs?.description)?.trim()?.takeIf { it.isNotBlank() },
        status = attrs?.status?.takeIf { it.isNotBlank() },
        year = attrs?.year,
        contentRating = attrs?.contentRating?.takeIf { it.isNotBlank() },
        tags = attrs?.tags.orEmpty().mapNotNull { tag ->
            localizedValue(tag.attributes?.name)
        }.distinct(),
        coverUrl = coverFileName?.let { fileName ->
            "$CoverCdn/$mangaId/$fileName.256.jpg"
        },
        rawJson = json.encodeToString(MangaDto.serializer(), this),
    )
}

private fun localizedValue(values: Map<String, String>?): String? {
    if (values.isNullOrEmpty()) return null
    TitleLanguagePriority.forEach { language ->
        values[language]?.takeIf { it.isNotBlank() }?.let { return it }
    }
    return values.values.firstOrNull { it.isNotBlank() }
}
