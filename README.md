# AnimeDB

AnimeDB is a Jetpack Compose Android app for browsing manga from [MangaDex](https://mangadex.org/). It shows a ranked “top manga” list, then a detail screen with synopsis, tags, credits, related titles, and external links.

The project is a working example of **clean architecture with MVVM**: Compose screens stay dumb, ViewModels own UI state, use cases talk to a domain repository, and Retrofit + Hilt handle the MangaDex API.

Data comes from the public [MangaDex API](https://api.mangadex.org/). The app does not host manga files or chapter images.

## What you can do

- Browse top manga ordered by MangaDex follow count
- Open a title for cover art, synopsis, publication details, tags, authors, and artists
- Switch language when a title has localized titles or descriptions
- Follow links to MangaDex, MyAnimeList, AniList, and other sites
- Retry after network, rate-limit, or server errors

## Screens

| Screen | Route | What it shows |
| ------ | ----- | ------------- |
| Top manga | `manga` | Cover, title, year, status, tags, and a short synopsis |
| Manga details | `manga/{mangaId}` | Full metadata, language picker, credits, related titles, and links |

## Stack

- Kotlin 2.2, Jetpack Compose, Material 3
- Navigation Compose
- Hilt
- Retrofit, OkHttp, kotlinx.serialization
- Coil for cover images
- Coroutines + `StateFlow`

Requires **Android 7.0 (API 24)** or newer. Open the project in Android Studio and run the `app` configuration.

## Docs

See [ARCHITECTURE.md](ARCHITECTURE.md) for package layout, data flow, and how the manga feature is wired.
