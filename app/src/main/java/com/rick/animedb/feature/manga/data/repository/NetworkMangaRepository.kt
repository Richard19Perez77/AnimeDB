package com.rick.animedb.feature.manga.data.repository

import com.rick.animedb.feature.manga.data.mapper.toDomain
import com.rick.animedb.feature.manga.data.remote.MangaApi
import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.feature.manga.domain.repository.MangaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NetworkMangaRepository @Inject constructor(
    private val api: MangaApi,
    private val json: Json,
) : MangaRepository {

    override suspend fun getTopManga(): List<Manga> = withContext(Dispatchers.IO) {
        api.getManga().data.orEmpty().mapNotNull { it.toDomain(json) }
    }

    override suspend fun getManga(id: String): Manga = withContext(Dispatchers.IO) {
        api.getMangaById(id).data?.toDomain(json)
            ?: error("MangaDex returned no data for $id")
    }
}
