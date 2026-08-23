package com.rick.animedb.feature.manga.domain.usecase

import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.feature.manga.domain.repository.MangaRepository
import javax.inject.Inject

class GetTopMangaUseCase @Inject constructor(
    private val repository: MangaRepository,
) {
    suspend operator fun invoke(): List<Manga> = repository.getTopManga()
}
