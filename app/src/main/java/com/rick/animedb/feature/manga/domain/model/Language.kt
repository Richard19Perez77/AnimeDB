package com.rick.animedb.feature.manga.domain.model

import java.util.Locale

data class LanguageOption(
    val code: String,
    val name: String,
)

data class LocalizedText(
    val languageCode: String,
    val languageName: String,
    val value: String,
)

data class MangaTag(
    val names: List<LocalizedText>,
)

fun pickLanguage(
    availableCodes: Collection<String>,
    preferredTag: String = Locale.getDefault().toLanguageTag(),
): String? {
    val available = availableCodes.filter { it.isNotBlank() }
    if (available.isEmpty()) return null
    val preferred = preferredTag.lowercase(Locale.ROOT)
    val language = preferred.substringBefore('-')
    val priority = listOf(preferred, language, "en", "ja-ro", "ja", "ko-ro", "ko", "zh")
    fun match(predicate: (String) -> Boolean) = available.firstOrNull(predicate)
    priority.forEach { candidate ->
        match { it.equals(candidate, ignoreCase = true) }?.let { return it }
    }
    match { it.startsWith("$language-", ignoreCase = true) }?.let { return it }
    return available.first()
}

fun List<LocalizedText>.valueFor(languageCode: String): String? {
    firstOrNull { it.languageCode.equals(languageCode, ignoreCase = true) }
        ?.value
        ?.takeIf { it.isNotBlank() }
        ?.let { return it }
    val fallback = pickLanguage(map { it.languageCode }, languageCode) ?: return firstOrNull()?.value
    return firstOrNull { it.languageCode.equals(fallback, ignoreCase = true) }?.value
        ?: firstOrNull()?.value
}

fun List<LocalizedText>.valuesFor(languageCode: String): List<String> =
    filter { it.languageCode.equals(languageCode, ignoreCase = true) }
        .map { it.value }
        .filter { it.isNotBlank() }
        .distinct()

fun List<LocalizedText>.preferredValue(): String? {
    val code = pickLanguage(map { it.languageCode }) ?: return firstOrNull()?.value
    return valueFor(code)
}

fun Manga.selectableLanguages(): List<LanguageOption> =
    (titles + altTitles + descriptions)
        .distinctBy { it.languageCode.lowercase(Locale.ROOT) }
        .map { LanguageOption(it.languageCode, it.languageName) }
        .sortedBy { it.name.lowercase(Locale.ROOT) }

fun MangaTag.nameFor(languageCode: String): String? = names.valueFor(languageCode)
