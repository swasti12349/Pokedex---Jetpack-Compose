package com.sro.jcapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sro.jcapp.pokemonList.PokemonListScreen
import com.sro.jcapp.pokemondetail.PokemonDetailsScreen
import com.sro.jcapp.ui.theme.JetpackComposePokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposePokedexTheme {
                PokedexApp()
//                aasd()
            }
        }
    }
}


@Composable
fun PokedexApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "pokemon_list_screen") {
        composable("pokemon_list_screen") {
            PokemonListScreen(navController = navController)
        }
        composable(
            "pokemon_detail_screen/{dominantColor}/{pokemonName}",
            arguments = listOf(navArgument("dominantColor") {
                type = NavType.IntType
            }, navArgument("pokemonName") {
                type = NavType.StringType
            })
        ) {
            val dominantColor = remember {
                val color = it.arguments?.getInt("dominantColor")
                color?.let { Color(it) } ?: Color.White
            }
            val pokemonName = remember {
                it.arguments?.getString("pokemonName")
            }

            PokemonDetailsScreen(
                navController = navController,
                pokemonName = pokemonName?.lowercase(java.util.Locale.ROOT) ?: "",
            )
        }
    }


}