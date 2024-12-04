package com.sro.jcapp.pokemonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.sro.jcapp.R
import com.sro.jcapp.data.remote.responses.Result
import com.sro.jcapp.ui.theme.RobotoCondensed


@Composable
fun PokemonListScreen(navController: NavController) {
    val viewModel: PokemonListViewModel = hiltViewModel()
    val pokemonFetchList = viewModel.pager.collectAsLazyPagingItems()
    val filteredList = pokemonFetchList.itemSnapshotList.items.toList()

    var searchQuery by remember { mutableStateOf("") }
    val filteredPokemonList = if (searchQuery.isEmpty()) {
        filteredList
    } else {
        filteredList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.pokemon_logo),
                contentDescription = "pokemon_logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                hint = "Search Pokemon"
            ) { query ->
                searchQuery = query
            }

            PokedexEntryGridView(
                navController = navController,
                pokemonFetchList = pokemonFetchList,
                pokemonFilteredList = filteredPokemonList,
                isSearching = searchQuery.isNotEmpty()
            )
        }
    }
}


@Composable
fun PokemonListEntry(
    modifier: Modifier = Modifier, navController: NavController, entry: Result
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor, defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    route = "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.name}"
                )
            }) {

        Column {
            val number = entry.url.trimEnd('/').takeLastWhile { it.isDigit() }
            val url =
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$number.png"

            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.name.capitalize(java.util.Locale.ROOT),
                fontSize = 20.sp,
                fontFamily = RobotoCondensed,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }

}




@Composable
fun PokedexEntryGridView(
    navController: NavController,
    pokemonFetchList: LazyPagingItems<Result>,
    pokemonFilteredList: List<Result>,
    isSearching: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (isSearching) {
            items(pokemonFilteredList.size) { index ->
                val entry = pokemonFilteredList[index]
                PokemonListEntry(navController = navController, entry = entry)
            }
        } else {
            items(pokemonFetchList.itemCount) { index ->
                val entry = pokemonFetchList[index]
                if (entry != null) {
                    PokemonListEntry(navController = navController, entry = entry)
                }
            }

            pokemonFetchList.apply {
                when (loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    is LoadState.Error -> {
                        item {
                            Text(
                                text = "Failed to load more items",
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }

                    is LoadState.NotLoading -> {}
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    var isHintDisplayed by remember { mutableStateOf(hint.isNotEmpty()) }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 10.dp, vertical = 12.dp)
                .onFocusChanged { isHintDisplayed = it.isFocused.not() }
        )

        if (isHintDisplayed && text.isEmpty()) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}
