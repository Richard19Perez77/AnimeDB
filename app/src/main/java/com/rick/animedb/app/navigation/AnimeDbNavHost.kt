package com.rick.animedb.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rick.animedb.feature.manga.presentation.mvvm.route.MangaDetailRoute
import com.rick.animedb.feature.manga.presentation.mvvm.state.MangaDetailViewModel
import com.rick.animedb.feature.manga.presentation.mvvm.route.MangaRoute

private const val MangaListRoute = "manga"
private const val MangaDetailRoutePattern =
    "manga/{${MangaDetailViewModel.MangaIdArg}}"

@Composable
fun AnimeDbNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = MangaListRoute,
    ) {
        composable(MangaListRoute) {
            MangaRoute(
                onMangaClick = { mangaId ->
                    navController.navigate("manga/$mangaId")
                },
            )
        }
        composable(
            route = MangaDetailRoutePattern,
            arguments = listOf(
                navArgument(MangaDetailViewModel.MangaIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            MangaDetailRoute(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
