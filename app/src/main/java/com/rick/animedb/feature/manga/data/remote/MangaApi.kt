package com.rick.animedb.feature.manga.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MangaApi {
    @GET("manga")
    suspend fun getManga(
        @Query("limit") limit: Int = 20,
        @Query("order[followedCount]") followedCountOrder: String = "desc",
        @Query("includes[]") includes: List<String> = listOf("cover_art"),
        @Query("contentRating[]") contentRatings: List<String> = listOf("safe", "suggestive"),
    ): MangaListResponseDto
}
