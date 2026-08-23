package com.rick.animedb.feature.characters.data.remote

import retrofit2.http.GET

interface CharacterApi {
    @GET("random/characters")
    suspend fun getRandomCharacter(): CharacterResponseDto
}
