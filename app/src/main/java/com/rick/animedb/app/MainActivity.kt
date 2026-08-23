package com.rick.animedb.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rick.animedb.app.navigation.AnimeDbNavHost
import com.rick.animedb.ui.theme.AnimeDBTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeDBTheme {
                AnimeDbNavHost()
            }
        }
    }
}
