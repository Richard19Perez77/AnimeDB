package com.rick.animedb.feature.characters.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponseDto(
    val data: CharacterDto,
)

@Serializable
data class CharacterDto(
    @SerialName("mal_id") val malId: Int,
    val url: String = "",
    val images: CharacterImagesDto? = null,
    val name: String,
    @SerialName("name_kanji") val nameKanji: String? = null,
    val nicknames: List<String> = emptyList(),
    val favorites: Int = 0,
    val about: String? = null,
)

@Serializable
data class CharacterImagesDto(
    val jpg: CharacterImageUrlsDto? = null,
    val webp: CharacterImageUrlsDto? = null,
)

@Serializable
data class CharacterImageUrlsDto(
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("small_image_url") val smallImageUrl: String? = null,
)
