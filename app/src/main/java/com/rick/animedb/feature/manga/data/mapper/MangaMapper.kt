package com.rick.animedb.feature.manga.data.mapper

import com.rick.animedb.feature.manga.data.remote.MangaDto
import com.rick.animedb.feature.manga.domain.model.Manga

private const val CoverCdn = "https://uploads.mangadex.org/covers"
private val TitleLanguagePriority = listOf("en", "ja-ro", "ja", "ko-ro", "ko", "zh")

fun MangaDto.toDomain(): Manga {
    val resolvedTitle = localizedValue(attributes.title)
        ?: attributes.altTitles.firstNotNullOfOrNull(::localizedValue)
        ?: "Untitled"
    val resolvedAlt = attributes.altTitles.firstNotNullOfOrNull { alt ->
        localizedValue(alt)?.takeIf { it != resolvedTitle }
    }
    val coverFileName = relationships
        .firstOrNull { it.type == "cover_art" }
        ?.attributes
        ?.fileName

    return Manga(
        id = id,
        title = resolvedTitle,
        altTitle = resolvedAlt,
        description = localizedValue(attributes.description)?.trim()?.takeIf { it.isNotBlank() },
        status = attributes.status?.takeIf { it.isNotBlank() },
        year = attributes.year,
        contentRating = attributes.contentRating?.takeIf { it.isNotBlank() },
        tags = attributes.tags.mapNotNull { tag ->
            localizedValue(tag.attributes?.name.orEmpty())
        }.distinct(),
        coverUrl = coverFileName?.let { fileName ->
            "$CoverCdn/$id/$fileName.256.jpg"
        },
    )
}

private fun localizedValue(values: Map<String, String>): String? {
    TitleLanguagePriority.forEach { language ->
        values[language]?.takeIf { it.isNotBlank() }?.let { return it }
    }
    return values.values.firstOrNull { it.isNotBlank() }
}
