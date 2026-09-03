# Architecture

AnimeDB is a single-module Android app (`:app`) organized as **clean architecture + MVVM** around one feature: manga from MangaDex.

```
UI (Compose screens)
        ↑ StateFlow
ViewModel
        ↓ use cases
Domain (models + repository contract)
        ↑ implementation
Data (Retrofit + mapper)
        ↓ HTTPS
MangaDex API
```

Presentation never talks to Retrofit. Domain never imports Android or networking types. Data maps JSON DTOs into domain models before they reach the UI.

## Layers

| Layer | Package | Responsibility |
| ----- | ------- | -------------- |
| App | `com.rick.animedb.app` | `Application`, `MainActivity`, navigation graph |
| Core | `com.rick.animedb.core.network` | Shared Retrofit / OkHttp / JSON setup |
| Feature data | `feature.manga.data` | API, DTOs, mapper, repository implementation |
| Feature domain | `feature.manga.domain` | `Manga` models, `MangaRepository`, use cases |
| Feature presentation | `feature.manga.presentation.mvvm` | Routes, screens, ViewModels, UI state |
| UI kit | `com.rick.animedb.ui` | Theme and shared composables such as `LinkifiedText` |

## Package layout

```
com.rick.animedb
├── app
│   ├── AnimeDbApp.kt                 # @HiltAndroidApp
│   ├── MainActivity.kt               # Compose host
│   └── navigation/AnimeDbNavHost.kt
├── core/network/NetworkModule.kt     # Retrofit singleton
├── feature/manga
│   ├── data
│   │   ├── remote/MangaApi.kt        # GET /manga, GET /manga/{id}
│   │   ├── remote/MangaDto.kt
│   │   ├── mapper/MangaMapper.kt     # DTO → Manga
│   │   └── repository/NetworkMangaRepository.kt
│   ├── di/MangaModule.kt             # binds MangaRepository
│   ├── domain
│   │   ├── model/                    # Manga, language helpers
│   │   ├── repository/MangaRepository.kt
│   │   └── usecase/                  # GetTopManga, GetMangaById
│   └── presentation/mvvm
│       ├── route/                    # collect state, call ViewModel
│       ├── screen/                   # stateless Compose UI
│       └── state/                    # ViewModels + sealed UI state
└── ui
    ├── components/LinkifiedText.kt
    └── theme/
```

## Navigation

`AnimeDbNavHost` is the only navigation graph.

```
manga  ──tap card──►  manga/{mangaId}
                          │
                     back / pop
```

- List: `MangaRoute` → `MangaViewModel` → `MangaScreen`
- Detail: `MangaDetailRoute` → `MangaDetailViewModel` → `MangaDetailScreen`

Routes exist so screens stay previewable and free of Hilt. They collect `StateFlow` with `collectAsStateWithLifecycle()` and forward events (`retry`, `selectLanguage`, `onMangaClick`).

`MangaDetailViewModel` reads `mangaId` from `SavedStateHandle` and also stores the selected language there so rotation keeps the same locale.

## Data flow

### Top manga

1. `MangaViewModel` calls `GetTopMangaUseCase`.
2. `NetworkMangaRepository.getTopManga()` hits `GET /manga` with `order[followedCount]=desc`, cover art included, and content ratings `safe` + `suggestive`.
3. Each `MangaDto` is mapped with `toDomain()`. Invalid entries are dropped.
4. Success becomes `MangaUiState.Success`; failures become `MangaError` (rate limit, unavailable, network, unknown).

### Detail

1. `MangaDetailViewModel` calls `GetMangaByIdUseCase(id)`.
2. Repository hits `GET /manga/{id}` with `cover_art`, `author`, and `artist` includes.
3. Mapper expands localized titles/descriptions, cover CDN URL, credits, related titles, and external site links.
4. Available languages are derived from titles, alt titles, and descriptions. The first pick prefers the device locale, then English, then Japanese.

Cover images use MangaDex uploads: `https://uploads.mangadex.org/covers/{id}/{fileName}.256.jpg`, loaded with Coil.

## Dependency injection

Hilt owns the graph.

- `AnimeDbApp` is `@HiltAndroidApp`; `MainActivity` is `@AndroidEntryPoint`.
- `NetworkModule` (`SingletonComponent`) provides `Json`, `OkHttpClient`, `Retrofit`, and `MangaApi`.
  - Base URL: `https://api.mangadex.org/`
  - 30s connect/read timeouts
  - `User-Agent: AnimeDB/1.0 (Android; educational)`
  - kotlinx.serialization converter with `ignoreUnknownKeys`
- `MangaModule` binds `NetworkMangaRepository` to `MangaRepository`.
- ViewModels are `@HiltViewModel` and receive use cases (and `SavedStateHandle` on detail).

```
NetworkModule ──► MangaApi
                      │
MangaModule ──► NetworkMangaRepository ──► MangaRepository
                                                │
                              GetTopMangaUseCase / GetMangaByIdUseCase
                                                │
                              MangaViewModel / MangaDetailViewModel
```

## UI state

Both screens use sealed interfaces rather than a single data class with flags:

```
Loading
Success(...)
Error(MangaError)
```

`Throwable.toMangaError()` maps:

| Cause | UI |
| ----- | -- |
| HTTP 429 | Rate limit — wait and retry |
| HTTP 403 or 5xx | MangaDex unavailable |
| `IOException` | No internet |
| Anything else | Generic error |

Screens render loading, list/detail content, or a retry button. They do not launch coroutines or know about Retrofit.

## Domain model

`Manga` is the only screen-facing model. It holds display strings plus the localized collections the detail screen needs:

- `titles`, `altTitles`, `descriptions` as `List<LocalizedText>`
- `credits` (author/artist + biographies + social links)
- `links` and `related` as `LabeledValue` (optional URL for `LinkifiedText`)

`pickLanguage()` / `valueFor()` choose a locale without the UI hard-coding `"en"`.

The data mapper is where MangaDex quirks stay: language-tag display names, MAL/AniList URL expansion, cover filename → CDN URL, and humanized tokens (`ongoing` → `Ongoing`).

## Why this shape

- **Use cases** keep ViewModels thin and make a second data source (cache, fake) a repository swap instead of a UI rewrite.
- **Routes vs screens** keep Compose previews working without a fake Hilt graph.
- **Mapper in data** keeps JSON:API relationships (`cover_art`, `author`, `manga`) out of domain and UI.
- **Single feature module inside `:app`** is enough for now; a second catalog (anime, people) would copy the `feature/<name>/{data,domain,presentation}` split.
