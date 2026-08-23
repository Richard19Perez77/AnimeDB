package com.rick.animedb.feature.manga.presentation.mvvm.screen

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.rick.animedb.feature.manga.domain.model.LanguageOption
import com.rick.animedb.feature.manga.domain.model.LocalizedText
import com.rick.animedb.feature.manga.domain.model.Manga
import com.rick.animedb.feature.manga.domain.model.MangaCredit
import com.rick.animedb.feature.manga.domain.model.MangaCreditRole
import com.rick.animedb.feature.manga.domain.model.MangaTag
import com.rick.animedb.feature.manga.domain.model.nameFor
import com.rick.animedb.feature.manga.domain.model.valueFor
import com.rick.animedb.feature.manga.domain.model.valuesFor
import com.rick.animedb.feature.manga.presentation.mvvm.state.MangaDetailUiState
import com.rick.animedb.feature.manga.presentation.mvvm.state.MangaError
import com.rick.animedb.ui.components.LinkifiedText
import com.rick.animedb.ui.theme.AnimeDBTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    uiState: MangaDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onLanguageSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val title = when (uiState) {
        is MangaDetailUiState.Success ->
            uiState.manga.titles.valueFor(uiState.selectedLanguageCode) ?: uiState.manga.title
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
                languages = uiState.languages,
                selectedLanguageCode = uiState.selectedLanguageCode,
                onLanguageSelected = onLanguageSelected,
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
    languages: List<LanguageOption>,
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = manga.titles.valueFor(selectedLanguageCode) ?: manga.title
    val altTitles = manga.altTitles.valuesFor(selectedLanguageCode).filter { it != title }
    val description = manga.descriptions.valueFor(selectedLanguageCode) ?: manga.description
    val tags = manga.tags.mapNotNull { it.nameFor(selectedLanguageCode) }.distinct()
    val details = detailFields(manga)
    val coverFields = coverFields(manga)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Header(
            coverUrl = manga.coverUrl,
            title = title,
            altTitle = altTitles.firstOrNull() ?: manga.altTitle?.takeIf { it != title },
            year = manga.year,
            status = manga.status,
            contentRating = manga.contentRating,
        )
        LanguageSelector(
            languages = languages,
            selectedLanguageCode = selectedLanguageCode,
            onLanguageSelected = onLanguageSelected,
        )
        SelectionContainer {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                description?.let { synopsis ->
                    LabeledSection(title = stringResource(R.string.manga_detail_synopsis)) {
                        LinkifiedText(text = synopsis)
                    }
                }

                val extraAltTitles = altTitles.drop(if (manga.altTitle != null || altTitles.isNotEmpty()) 1 else 0)
                if (extraAltTitles.isNotEmpty()) {
                    LabeledSection(title = stringResource(R.string.manga_detail_alt_titles)) {
                        Text(
                            text = extraAltTitles.joinToString("\n"),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                if (details.isNotEmpty()) {
                    LabeledSection(title = stringResource(R.string.manga_detail_details)) {
                        FieldList(details)
                    }
                }

                if (tags.isNotEmpty()) {
                    LabeledSection(title = stringResource(R.string.manga_detail_tags)) {
                        Text(
                            text = tags.joinToString(", "),
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
                                CreditBlock(credit, selectedLanguageCode)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(
    languages: List<LanguageOption>,
    selectedLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
) {
    if (languages.size <= 1) return
    var expanded by remember { mutableStateOf(false) }
    val selected = languages.firstOrNull { it.code.equals(selectedLanguageCode, ignoreCase = true) }
        ?: languages.first()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.manga_detail_language)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.name) },
                    onClick = {
                        onLanguageSelected(language.code)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Header(
    coverUrl: String?,
    title: String,
    altTitle: String?,
    year: Int?,
    status: String?,
    contentRating: String?,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = coverUrl,
            contentDescription = stringResource(R.string.manga_cover, title),
            modifier = Modifier
                .size(width = 112.dp, height = 158.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            altTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val subtitle = listOfNotNull(
                year?.toString(),
                status,
                contentRating,
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
    add(
        stringResource(R.string.manga_field_id),
        manga.id,
        url = "https://mangadex.org/title/${manga.id}",
    )
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
    add(
        stringResource(R.string.manga_field_latest_chapter),
        manga.latestUploadedChapter,
        url = manga.latestUploadedChapter?.let { "https://mangadex.org/chapter/$it" },
    )
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

private fun MutableList<LabeledValue>.add(label: String, value: String?, url: String? = null) {
    if (!value.isNullOrBlank()) add(LabeledValue(label, value, url))
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
            LabeledField(label = field.label, value = field.value, url = field.url)
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
    url: String? = null,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(2.dp))
        LinkifiedText(text = value, url = url)
    }
}

@Composable
private fun CreditBlock(
    credit: MangaCredit,
    languageCode: String,
) {
    val role = when (credit.role) {
        MangaCreditRole.Author -> stringResource(R.string.manga_credit_author)
        MangaCreditRole.Artist -> stringResource(R.string.manga_credit_artist)
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LabeledField(label = role, value = credit.name)
        credit.biographies.valueFor(languageCode)?.let { biography ->
            LabeledField(
                label = stringResource(R.string.manga_credit_biography),
                value = biography,
            )
        }
        credit.links.forEach { link ->
            LabeledField(label = link.label, value = link.value, url = link.url)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MangaDetailScreenPreview() {
    AnimeDBTheme {
        MangaDetailScreen(
            uiState = MangaDetailUiState.Success(
                manga = Manga(
                    id = "preview",
                    title = "Solo Leveling",
                    altTitle = "Ore Dake Level Up na Ken",
                    description = "A hunter who was once the weakest begins to level up alone.",
                    status = "Completed",
                    year = 2018,
                    contentRating = "Safe",
                    tags = listOf(
                        MangaTag(listOf(LocalizedText("en", "English", "Action"))),
                        MangaTag(listOf(LocalizedText("en", "English", "Adventure"))),
                    ),
                    type = "Manga",
                    titles = listOf(
                        LocalizedText("en", "English", "Solo Leveling"),
                        LocalizedText("ja", "Japanese", "俺だけレベルアップな件"),
                    ),
                    descriptions = listOf(
                        LocalizedText(
                            "en",
                            "English",
                            "A hunter who was once the weakest begins to level up alone.",
                        ),
                        LocalizedText("ja", "Japanese", "最弱だったハンターが一人でレベルアップしていく。"),
                    ),
                    originalLanguage = "Japanese",
                    publicationDemographic = "Shounen",
                    lastVolume = "18",
                    lastChapter = "179",
                    credits = listOf(
                        MangaCredit(
                            name = "Chugong",
                            role = MangaCreditRole.Author,
                            links = listOf(
                                LabeledValue(
                                    label = "Twitter",
                                    value = "@solo_leveling",
                                    url = "https://x.com/solo_leveling",
                                ),
                            ),
                        ),
                        MangaCredit(name = "Dubu", role = MangaCreditRole.Artist),
                    ),
                    links = listOf(
                        LabeledValue(
                            label = "MyAnimeList",
                            value = "https://myanimelist.net/manga/121496",
                            url = "https://myanimelist.net/manga/121496",
                        ),
                    ),
                ),
                languages = listOf(
                    LanguageOption("en", "English"),
                    LanguageOption("ja", "Japanese"),
                ),
                selectedLanguageCode = "en",
            ),
            onBack = {},
            onRetry = {},
        )
    }
}
