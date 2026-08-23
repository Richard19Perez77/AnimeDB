package com.rick.animedb.feature.characters.data.repository

import com.rick.animedb.feature.characters.data.mapper.toDomain
import com.rick.animedb.feature.characters.data.remote.CharacterApi
import com.rick.animedb.feature.characters.domain.model.AnimeCharacter
import com.rick.animedb.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class NetworkCharacterRepository @Inject constructor(
    private val api: CharacterApi,
) : CharacterRepository {

    override suspend fun getRandomCharacters(count: Int): List<AnimeCharacter> =
        withContext(Dispatchers.IO) {
            val characters = ArrayList<AnimeCharacter>(count)
            var lastError: Throwable? = null

            repeat(count) { index ->
                if (index > 0) delay(RequestSpacingMs)
                try {
                    characters += api.getRandomCharacter().data.toDomain()
                } catch (error: Throwable) {
                    if (error is CancellationException) throw error
                    lastError = error
                    if (characters.isEmpty()) throw error
                    return@withContext characters
                }
            }

            if (characters.isEmpty()) {
                throw lastError ?: IllegalStateException("No characters returned")
            }
            characters
        }

    private companion object {
        const val RequestSpacingMs = 400L
    }
}
