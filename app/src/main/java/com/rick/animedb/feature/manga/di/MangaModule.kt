package com.rick.animedb.feature.manga.di

import com.rick.animedb.feature.manga.data.repository.NetworkMangaRepository
import com.rick.animedb.feature.manga.domain.repository.MangaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MangaModule {

    @Binds
    @Singleton
    abstract fun bindMangaRepository(
        impl: NetworkMangaRepository,
    ): MangaRepository
}
