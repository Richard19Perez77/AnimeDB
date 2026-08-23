package com.rick.animedb.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rick.animedb.feature.manga.presentation.mvvm.MangaRoute

private const val MangaRoutePattern = "manga"

@Composable
fun AnimeDbNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = MangaRoutePattern,
    ) {
        composable(MangaRoutePattern) {
            MangaRoute()
        }
    }
}
