package com.rick.animedb.feature.characters.data.mapper

import com.rick.animedb.feature.characters.data.remote.CharacterDto
import com.rick.animedb.feature.characters.domain.model.AnimeCharacter

fun CharacterDto.toDomain(): AnimeCharacter = AnimeCharacter(
    id = malId,
    name = name,
    nameKanji = nameKanji?.takeIf { it.isNotBlank() },
    nicknames = nicknames.filter { it.isNotBlank() },
    favorites = favorites,
    about = about?.trim()?.takeIf { it.isNotBlank() },
    imageUrl = images?.jpg?.imageUrl
        ?: images?.webp?.imageUrl
        ?: images?.jpg?.smallImageUrl
        ?: images?.webp?.smallImageUrl,
    url = url,
)
