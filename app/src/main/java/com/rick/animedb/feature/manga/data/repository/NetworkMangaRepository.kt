package com.rick.animedb.feature.manga.data.repository

import com.rick.animedb.feature.manga.data.mapper.toDomain
import com.rick.animedb.feature.manga.data.remote.MangaApi
import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.feature.manga.domain.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkMangaRepository @Inject constructor(
    private val api: MangaApi,
) : MangaRepository {

    override suspend fun getTopManga(): List<Manga> = withContext(Dispatchers.IO) {
        api.getManga().data.map { it.toDomain() }
    }
}
