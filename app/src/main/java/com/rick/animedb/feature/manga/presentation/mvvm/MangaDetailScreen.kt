package com.rick.animedb.feature.manga.presentation.mvvm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rick.animedb.R
import com.rick.animedb.feature.manga.domain.model.LabeledValue
import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.feature.manga.domain.model.MangaCredit
import com.rick.animedb.feature.manga.domain.model.MangaCreditRole
import com.rick.animedb.ui.theme.AnimeDBTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    uiState: MangaDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = when (uiState) {
        is MangaDetailUiState.Success -> uiState.manga.title
        else -> stringResource(R.string.manga_detail_title)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.manga_detail_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (uiState) {
            MangaDetailUiState.Loading -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.manga_detail_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            is MangaDetailUiState.Success -> DetailContent(
                manga = uiState.manga,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is MangaDetailUiState.Error -> {
                val message = when (uiState.reason) {
                    MangaError.RateLimit -> stringResource(R.string.manga_error_rate_limit)
                    MangaError.Unavailable -> stringResource(R.string.manga_error_unavailable)
                    MangaError.Network -> stringResource(R.string.manga_error_network)
                    MangaError.Unknown -> stringResource(R.string.manga_error_generic)
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.manga_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    manga: Manga,
    modifier: Modifier = Modifier,
) {
    val details = detailFields(manga)
    val coverFields = coverFields(manga)

    SelectionContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Header(manga)

            if (manga.descriptions.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_synopsis)) {
                    FieldList(manga.descriptions)
                }
            } else {
                manga.description?.let { synopsis ->
                    LabeledSection(title = stringResource(R.string.manga_detail_synopsis)) {
                        Text(text = synopsis, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            if (manga.titles.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_titles)) {
                    FieldList(manga.titles)
                }
            }

            if (manga.altTitles.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_alt_titles)) {
                    FieldList(manga.altTitles)
                }
            }

            if (details.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_details)) {
                    FieldList(details)
                }
            }

            if (manga.tags.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_tags)) {
                    Text(
                        text = manga.tags.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            if (coverFields.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_cover)) {
                    FieldList(coverFields)
                }
            }

            if (manga.links.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_links)) {
                    FieldList(manga.links)
                }
            }

            if (manga.credits.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_credits)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        manga.credits.forEach { credit ->
                            CreditBlock(credit)
                        }
                    }
                }
            }

            if (manga.related.isNotEmpty()) {
                LabeledSection(title = stringResource(R.string.manga_detail_related)) {
                    FieldList(manga.related)
                }
            }
        }
    }
}

@Composable
private fun Header(manga: Manga) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = manga.coverUrl,
            contentDescription = stringResource(R.string.manga_cover, manga.title),
            modifier = Modifier
                .size(width = 112.dp, height = 158.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = manga.title, style = MaterialTheme.typography.headlineSmall)
            manga.altTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val subtitle = listOfNotNull(
                manga.year?.toString(),
                manga.status,
                manga.contentRating,
            ).joinToString(" · ")
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun detailFields(manga: Manga): List<LabeledValue> = buildList {
    add(stringResource(R.string.manga_field_id), manga.id)
    add(stringResource(R.string.manga_field_type), manga.type)
    add(stringResource(R.string.manga_field_status), manga.status)
    add(stringResource(R.string.manga_field_year), manga.year?.toString())
    add(stringResource(R.string.manga_field_content_rating), manga.contentRating)
    add(stringResource(R.string.manga_field_demographic), manga.publicationDemographic)
    add(stringResource(R.string.manga_field_original_language), manga.originalLanguage)
    add(
        stringResource(R.string.manga_field_translated_languages),
        manga.availableTranslatedLanguages.takeIf { it.isNotEmpty() }?.joinToString(", "),
    )
    add(stringResource(R.string.manga_field_last_volume), manga.lastVolume)
    add(stringResource(R.string.manga_field_last_chapter), manga.lastChapter)
    add(stringResource(R.string.manga_field_latest_chapter), manga.latestUploadedChapter)
    add(stringResource(R.string.manga_field_state), manga.state)
    add(stringResource(R.string.manga_field_locked), manga.isLocked.toYesNo())
    add(
        stringResource(R.string.manga_field_chapter_reset),
        manga.chapterNumbersResetOnNewVolume.toYesNo(),
    )
    add(stringResource(R.string.manga_field_version), manga.version?.toString())
    add(stringResource(R.string.manga_field_created), manga.createdAt)
    add(stringResource(R.string.manga_field_updated), manga.updatedAt)
}

@Composable
private fun coverFields(manga: Manga): List<LabeledValue> = buildList {
    add(stringResource(R.string.manga_field_cover_file), manga.coverFileName)
    add(stringResource(R.string.manga_field_cover_locale), manga.coverLocale)
    add(stringResource(R.string.manga_field_cover_volume), manga.coverVolume)
    add(stringResource(R.string.manga_field_cover_description), manga.coverDescription)
}

@Composable
private fun Boolean?.toYesNo(): String? = this?.let {
    stringResource(if (it) R.string.manga_yes else R.string.manga_no)
}

private fun MutableList<LabeledValue>.add(label: String, value: String?) {
    if (!value.isNullOrBlank()) add(LabeledValue(label, value))
}

@Composable
private fun LabeledSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun FieldList(fields: List<LabeledValue>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        fields.forEachIndexed { index, field ->
            LabeledField(label = field.label, value = field.value)
            if (index < fields.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun CreditBlock(credit: MangaCredit) {
    val role = when (credit.role) {
        MangaCreditRole.Author -> stringResource(R.string.manga_credit_author)
        MangaCreditRole.Artist -> stringResource(R.string.manga_credit_artist)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabeledField(label = role, value = credit.name)
        credit.biography?.let { biography ->
            LabeledField(
                label = stringResource(R.string.manga_credit_biography),
                value = biography,
            )
        }
        credit.links.forEach { link ->
            LabeledField(label = link.label, value = link.value)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MangaDetailScreenPreview() {
    AnimeDBTheme {
        MangaDetailScreen(
            uiState = MangaDetailUiState.Success(
                Manga(
                    id = "preview",
                    title = "Solo Leveling",
                    altTitle = "Ore Dake Level Up na Ken",
                    description = "A hunter who was once the weakest begins to level up alone.",
                    status = "Completed",
                    year = 2018,
                    contentRating = "Safe",
                    tags = listOf("Action", "Adventure", "Fantasy"),
                    type = "Manga",
                    titles = listOf(
                        LabeledValue("English", "Solo Leveling"),
                        LabeledValue("Japanese", "俺だけレベルアップな件"),
                    ),
                    originalLanguage = "Japanese",
                    publicationDemographic = "Shounen",
                    lastVolume = "18",
                    lastChapter = "179",
                    credits = listOf(
                        MangaCredit(name = "Chugong", role = MangaCreditRole.Author),
                        MangaCredit(name = "Dubu", role = MangaCreditRole.Artist),
                    ),
                    links = listOf(
                        LabeledValue("MyAnimeList", "https://myanimelist.net/manga/121496"),
                    ),
                ),
            ),
            onBack = {},
            onRetry = {},
        )
    }
}
