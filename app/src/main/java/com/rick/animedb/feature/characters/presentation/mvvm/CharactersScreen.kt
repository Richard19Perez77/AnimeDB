package com.rick.animedb.feature.characters.presentation.mvvm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rick.animedb.R
import com.rick.animedb.feature.characters.domain.model.AnimeCharacter
import com.rick.animedb.ui.theme.AnimeDBTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    uiState: CharactersUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.characters_title)) },
                actions = {
                    IconButton(
                        onClick = onRetry,
                        enabled = uiState !is CharactersUiState.Loading,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.characters_refresh),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            CharactersUiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is CharactersUiState.Success -> CharacterList(
                characters = uiState.characters,
                contentPadding = innerPadding,
            )

            is CharactersUiState.Error -> ErrorContent(
                reason = uiState.reason,
                onRetry = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.characters_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorContent(
    reason: CharactersError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = when (reason) {
        CharactersError.RateLimit -> stringResource(R.string.characters_error_rate_limit)
        CharactersError.Unavailable -> stringResource(R.string.characters_error_unavailable)
        CharactersError.Network -> stringResource(R.string.characters_error_network)
        CharactersError.Unknown -> stringResource(R.string.characters_error_generic)
    }

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.characters_retry))
        }
    }
}

@Composable
private fun CharacterList(
    characters: List<AnimeCharacter>,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(
            characters,
            key = { index, character -> "${character.id}-$index" },
        ) { _, character ->
            CharacterCard(character)
        }
    }
}

@Composable
private fun CharacterCard(character: AnimeCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = stringResource(
                    R.string.characters_image,
                    character.name,
                ),
                modifier = Modifier
                    .size(width = 88.dp, height = 124.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                character.nameKanji?.let { kanji ->
                    Text(
                        text = kanji,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = stringResource(R.string.characters_mal_id, character.id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.characters_favorites, character.favorites),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                if (character.nicknames.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = character.nicknames.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                character.about?.let { about ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = about,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CharactersScreenSuccessPreview() {
    AnimeDBTheme {
        CharactersScreen(
            uiState = CharactersUiState.Success(
                listOf(
                    AnimeCharacter(
                        id = 1,
                        name = "Spike Spiegel",
                        nameKanji = "スパイク・スピーゲル",
                        nicknames = listOf("Spike"),
                        favorites = 42_000,
                        about = "A bounty hunter traveling on the spaceship Bebop.",
                        imageUrl = null,
                        url = "https://myanimelist.net/character/1/Spike_Spiegel",
                    ),
                ),
            ),
            onRetry = {},
        )
    }
}
