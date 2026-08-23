package com.rick.animedb.feature.characters.domain.repository

import com.rick.animedb.feature.characters.domain.model.AnimeCharacter

interface CharacterRepository {
    suspend fun getRandomCharacters(count: Int): List<AnimeCharacter>
}
