package com.sro.jcapp.pokemondetail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sro.jcapp.R
import com.sro.jcapp.data.remote.responses.Pokemon
import com.sro.jcapp.data.remote.responses.Type
import com.sro.jcapp.util.Resource
import java.util.Locale
import kotlin.math.round

@Composable
fun PokemonDetailsScreen(
    modifier: Modifier = Modifier, navController: NavController,
    pokemonName: String,
    viewModel: PokemonDetailViewModel = hiltViewModel(),
) {
    val pokemonInfo = remember { mutableStateOf<Resource<Pokemon>>(Resource.Loading()) }

    LaunchedEffect(pokemonName) {
        val details = viewModel.getPokemonDetails(pokemonName)
        pokemonInfo.value = details

    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red)
    ) {

        PokemonDetailTopSection(
            navController, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .offset(y = -10.dp)
        )
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo.value,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(12.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
        )

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (pokemonInfo.value is Resource.Success) {
                pokemonInfo.value.data?.sprites?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it.front_default),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.TopCenter)
                            .offset(y = 60.dp)
                    )
                }
            }
        }

    }


}

@Composable
fun PokemonDetailTopSection(navController: NavController, modifier: Modifier) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier.background(
            Brush.verticalGradient(listOf(Color.Black, Color.Transparent))
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(x = 16.dp, y = 60.dp)
                .clickable { navController.popBackStack() }
        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier,
    loadingModifier: Modifier
) {
    when (pokemonInfo) {
        is Resource.Success -> {
            PokemonDetailSection(
                modifier.offset(y = (-60).dp), pokemonInfo.data!!
            )
        }

        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier
            )
        }

        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun PokemonDetailSection(modifier: Modifier = Modifier, pokemon: Pokemon) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .offset(y = 140.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "#${pokemon.id} ${
                pokemon.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
            }",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        PokemonTypeSection(types = pokemon.types)
        Spacer(modifier = Modifier.height(10.dp))
        PokemonDetailDataSection(pokemon.weight, pokemon.height)

        PokemonBaseStats(pokemon = pokemon)
    }
}

@Composable
fun PokemonTypeSection(types: List<Type>) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        for (type in types) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(Color.Blue)
                    .height(36.dp)
            ) {


                Text(
                    text = type.type.name.capitalize(Locale.ROOT),
                    color = Color.White,
                    fontSize = 18.sp
                )

            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightInKg = remember {
        round(pokemonWeight * 100f) / 1000f
    }

    val pokemonHeightInMeters = remember {
        round(pokemonHeight * 100f) / 1000f
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "Kg",
            dataIcon = painterResource(id = R.drawable.weight),
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .size(1.dp, sectionHeight)
                .background(Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.height),
            modifier = Modifier.weight(1f)
        )
    }

}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            painter = dataIcon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "$dataValue$dataUnit", color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PokemonStat(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0

) {

    var animationPlayed = remember {
        mutableStateOf(false)
    }


    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed.value) {
            statValue / statMaxValue.toFloat()
        } else {
            0f
        },
        animationSpec = tween(
            animDuration,
            animDelay,
            easing = LinearEasing
        )

    )

    LaunchedEffect(key1 = true) {
        animationPlayed.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .clip(CircleShape)
                .background(Color.Magenta)
                .padding(horizontal = 8.dp)
        ) {
            Text(text = statName, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
fun PokemonBaseStats(pokemon: Pokemon, animationDelay: Int = 100) {

    val maxBaseStat = remember {
        pokemon.stats.maxOf { it.base_stat }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Base Stats:",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        for (i in pokemon.stats.indices) {
            val stat = pokemon.stats[i]
            PokemonStat(
                statName = pokemon.stats[i].stat.name,
                statValue = stat.base_stat,
                statMaxValue = maxBaseStat,
                animDelay = i * animationDelay
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }

}