package com.rick.animedb.feature.manga.data.mapper

import com.rick.animedb.feature.manga.data.remote.MangaDto
import com.rick.animedb.feature.manga.data.remote.RelationshipAttributesDto
import com.rick.animedb.feature.manga.data.remote.RelationshipDto
import com.rick.animedb.feature.manga.domain.model.LabeledValue
import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.feature.manga.domain.model.MangaCredit
import com.rick.animedb.feature.manga.domain.model.MangaCreditRole
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.Locale

private const val CoverCdn = "https://uploads.mangadex.org/covers"
private val TitleLanguagePriority = listOf("en", "ja-ro", "ja", "ko-ro", "ko", "zh")

private val LanguageOverrides = mapOf(
    "ja-ro" to "Japanese (romanized)",
    "ko-ro" to "Korean (romanized)",
    "zh-ro" to "Chinese (romanized)",
    "zh-hk" to "Chinese (Hong Kong)",
    "pt-br" to "Portuguese (Brazil)",
    "es-la" to "Spanish (Latin America)",
)

private val LinkLabels = mapOf(
    "al" to "AniList",
    "ap" to "Anime-Planet",
    "bw" to "BookWalker",
    "mu" to "MangaUpdates",
    "nu" to "NovelUpdates",
    "kt" to "Kitsu",
    "amz" to "Amazon",
    "ebj" to "eBookJapan",
    "mal" to "MyAnimeList",
    "cdj" to "CDJapan",
    "raw" to "Official raw",
    "engtl" to "Official English",
)

private val LinkUrls = mapOf(
    "al" to "https://anilist.co/manga/%s",
    "ap" to "https://www.anime-planet.com/manga/%s",
    "mal" to "https://myanimelist.net/manga/%s",
    "kt" to "https://kitsu.app/manga/%s",
    "mu" to "https://www.mangaupdates.com/series.html?id=%s",
)

fun MangaDto.toDomain(): Manga? {
    val mangaId = id ?: return null
    val attrs = attributes
    val resolvedTitle = localizedValue(attrs?.title)
        ?: attrs?.altTitles?.firstNotNullOfOrNull(::localizedValue)
        ?: "Untitled"
    val resolvedAlt = attrs?.altTitles?.firstNotNullOfOrNull { alt ->
        localizedValue(alt)?.takeIf { it != resolvedTitle }
    }
    val cover = relationships?.firstOrNull { it.type == "cover_art" }?.attributes
    val coverFileName = cover?.fileName

    return Manga(
        id = mangaId,
        title = resolvedTitle,
        altTitle = resolvedAlt,
        description = localizedValue(attrs?.description)?.trim()?.takeIf { it.isNotBlank() },
        status = humanizeToken(attrs?.status),
        year = attrs?.year,
        contentRating = humanizeToken(attrs?.contentRating),
        tags = attrs?.tags.orEmpty().mapNotNull { tag ->
            localizedValue(tag.attributes?.name)
        }.distinct(),
        coverUrl = coverFileName?.let { fileName ->
            "$CoverCdn/$mangaId/$fileName.256.jpg"
        },
        type = humanizeToken(type),
        titles = localizedFields(attrs?.title),
        altTitles = attrs?.altTitles.orEmpty().flatMap(::localizedFields).distinct(),
        descriptions = localizedFields(attrs?.description),
        originalLanguage = attrs?.originalLanguage?.let(::languageName),
        publicationDemographic = humanizeToken(attrs?.publicationDemographic),
        lastVolume = attrs?.lastVolume?.takeIf { it.isNotBlank() },
        lastChapter = attrs?.lastChapter?.takeIf { it.isNotBlank() },
        state = humanizeToken(attrs?.state),
        isLocked = attrs?.isLocked,
        chapterNumbersResetOnNewVolume = attrs?.chapterNumbersResetOnNewVolume,
        version = attrs?.version,
        createdAt = formatTimestamp(attrs?.createdAt),
        updatedAt = formatTimestamp(attrs?.updatedAt),
        latestUploadedChapter = attrs?.latestUploadedChapter?.takeIf { it.isNotBlank() },
        availableTranslatedLanguages = attrs?.availableTranslatedLanguages.orEmpty()
            .filter { it.isNotBlank() }
            .map(::languageName)
            .distinct(),
        links = buildLinks(attrs?.links, attrs?.officialLinks),
        credits = buildCredits(relationships),
        related = relationships.orEmpty()
            .filter { it.type == "manga" && !it.id.isNullOrBlank() }
            .map { rel ->
                LabeledValue(
                    label = humanizeToken(rel.related) ?: "Related",
                    value = rel.attributes?.name?.takeIf { it.isNotBlank() } ?: rel.id.orEmpty(),
                )
            },
        coverFileName = coverFileName,
        coverLocale = cover?.locale?.let(::languageName),
        coverVolume = cover?.volume?.takeIf { it.isNotBlank() },
        coverDescription = cover?.description?.takeIf { it.isNotBlank() },
    )
}

private fun buildLinks(
    links: Map<String, String>?,
    officialLinks: JsonElement?,
): List<LabeledValue> {
    val fromLinks = links.orEmpty()
        .filter { it.value.isNotBlank() }
        .map { (key, value) ->
            LabeledValue(
                label = LinkLabels[key] ?: humanizeKey(key),
                value = LinkUrls[key]?.let { pattern ->
                    if (value.startsWith("http")) value else pattern.format(value)
                } ?: value,
            )
        }
    val fromOfficial = officialLinks.toLabeledValues("Official link")
    return (fromLinks + fromOfficial).distinct()
}

private fun buildCredits(relationships: List<RelationshipDto>?): List<MangaCredit> {
    return relationships.orEmpty().mapNotNull { rel ->
        val role = when (rel.type) {
            "author" -> MangaCreditRole.Author
            "artist" -> MangaCreditRole.Artist
            else -> return@mapNotNull null
        }
        val name = rel.attributes?.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        MangaCredit(
            name = name,
            role = role,
            biography = localizedValue(rel.attributes.biography)?.trim()?.takeIf { it.isNotBlank() },
            links = rel.attributes.toSocialLinks(),
        )
    }
}

private fun RelationshipAttributesDto.toSocialLinks(): List<LabeledValue> = listOfNotNull(
    labeled("Twitter", twitter),
    labeled("Pixiv", pixiv),
    labeled("Melonbooks", melonBook),
    labeled("Fanbox", fanBox),
    labeled("Booth", booth),
    labeled("Namicomi", namicomi),
    labeled("Niconico", nicoVideo),
    labeled("Skeb", skeb),
    labeled("Fantia", fantia),
    labeled("Tumblr", tumblr),
    labeled("YouTube", youtube),
    labeled("Weibo", weibo),
    labeled("Naver", naver),
    labeled("Website", website),
)

private fun labeled(label: String, value: String?): LabeledValue? =
    value?.takeIf { it.isNotBlank() }?.let { LabeledValue(label, it) }

private fun JsonElement?.toLabeledValues(fallbackLabel: String): List<LabeledValue> = when (this) {
    null -> emptyList()
    is JsonPrimitive -> listOfNotNull(
        content.takeIf { it.isNotBlank() }?.let { LabeledValue(fallbackLabel, it) },
    )
    is JsonObject -> entries.flatMap { (key, value) ->
        value.toLabeledValues(LinkLabels[key] ?: humanizeKey(key))
    }
    is JsonArray -> mapIndexed { index, element ->
        val label = if (size == 1) fallbackLabel else "$fallbackLabel ${index + 1}"
        element.toLabeledValues(label)
    }.flatten()
    else -> emptyList()
}

private fun localizedFields(values: Map<String, String>?): List<LabeledValue> =
    values.orEmpty()
        .filter { it.value.isNotBlank() }
        .map { (language, text) ->
            LabeledValue(languageName(language), text.trim())
        }

private fun localizedValue(values: Map<String, String>?): String? {
    if (values.isNullOrEmpty()) return null
    TitleLanguagePriority.forEach { language ->
        values[language]?.takeIf { it.isNotBlank() }?.let { return it }
    }
    return values.values.firstOrNull { it.isNotBlank() }
}

private fun languageName(code: String): String {
    LanguageOverrides[code]?.let { return it }
    val locale = Locale.forLanguageTag(code)
    val name = locale.getDisplayName(Locale.getDefault())
    return name.takeIf { it.isNotBlank() && !it.equals(code, ignoreCase = true) } ?: code
}

private fun humanizeToken(value: String?): String? =
    value?.takeIf { it.isNotBlank() }?.let(::humanizeKey)

private fun humanizeKey(value: String): String =
    value
        .replace('_', ' ')
        .replace('-', ' ')
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { token ->
            token.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
            }
        }

private fun formatTimestamp(value: String?): String? {
    if (value.isNullOrBlank()) return null
    val match = Regex("""(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2})""").find(value) ?: return value
    return "${match.groupValues[1]} ${match.groupValues[2]} UTC"
}
