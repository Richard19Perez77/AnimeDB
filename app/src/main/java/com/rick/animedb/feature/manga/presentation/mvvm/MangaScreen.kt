package com.rick.animedb.feature.manga.presentation.mvvm

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.ui.theme.AnimeDBTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaScreen(
    uiState: MangaUiState,
    onRetry: () -> Unit,
    onMangaClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manga_title)) },
                actions = {
                    IconButton(
                        onClick = onRetry,
                        enabled = uiState !is MangaUiState.Loading,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.manga_refresh),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            MangaUiState.Loading -> LoadingContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is MangaUiState.Success -> MangaList(
                manga = uiState.manga,
                contentPadding = innerPadding,
                onMangaClick = onMangaClick,
            )

            is MangaUiState.Error -> ErrorContent(
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
            text = stringResource(R.string.manga_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorContent(
    reason: MangaError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val message = when (reason) {
        MangaError.RateLimit -> stringResource(R.string.manga_error_rate_limit)
        MangaError.Unavailable -> stringResource(R.string.manga_error_unavailable)
        MangaError.Network -> stringResource(R.string.manga_error_network)
        MangaError.Unknown -> stringResource(R.string.manga_error_generic)
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
            Text(stringResource(R.string.manga_retry))
        }
    }
}

@Composable
private fun MangaList(
    manga: List<Manga>,
    contentPadding: PaddingValues,
    onMangaClick: (String) -> Unit,
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
        items(manga, key = { it.id }) { item ->
            MangaCard(
                manga = item,
                onClick = { onMangaClick(item.id) },
            )
        }
        item {
            Text(
                text = stringResource(R.string.manga_credit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun MangaCard(
    manga: Manga,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            AsyncImage(
                model = manga.coverUrl,
                contentDescription = stringResource(R.string.manga_cover, manga.title),
                modifier = Modifier
                    .size(width = 88.dp, height = 124.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                manga.altTitle?.let { alt ->
                    Text(
                        text = alt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = listOfNotNull(
                        manga.year?.toString(),
                        manga.status,
                        manga.contentRating,
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (manga.tags.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = manga.tags.take(8).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                manga.description?.let { description ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MangaScreenSuccessPreview() {
    AnimeDBTheme {
        MangaScreen(
            uiState = MangaUiState.Success(
                listOf(
                    Manga(
                        id = "preview",
                        title = "Solo Leveling",
                        altTitle = "Ore Dake Level Up na Ken",
                        description = "A hunter who was once the weakest begins to level up alone.",
                        status = "Completed",
                        year = 2018,
                        contentRating = "Safe",
                        tags = listOf("Action", "Adventure", "Fantasy"),
                    ),
                ),
            ),
            onRetry = {},
            onMangaClick = {},
        )
    }
}
