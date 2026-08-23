package com.rick.animedb.feature.manga.domain.repository

import com.rick.animedb.feature.manga.domain.model.Manga

interface MangaRepository {
    suspend fun getTopManga(): List<Manga>
    suspend fun getManga(id: String): Manga
}
