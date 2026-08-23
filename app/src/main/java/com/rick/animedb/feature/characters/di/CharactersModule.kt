package com.rick.animedb.feature.characters.di

import com.rick.animedb.feature.characters.data.repository.NetworkCharacterRepository
import com.rick.animedb.feature.characters.domain.repository.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CharactersModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: NetworkCharacterRepository,
    ): CharacterRepository
}
