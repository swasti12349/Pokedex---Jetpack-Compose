package com.sro.jcapp.pokemondetail

import androidx.lifecycle.ViewModel
import com.sro.jcapp.data.remote.responses.Pokemon
import com.sro.jcapp.repository.PokemonRepository
import com.sro.jcapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(private val pokemonRepository: PokemonRepository):ViewModel() {

    suspend fun getPokemonDetails(pokemonName: String):Resource<Pokemon> {
         return pokemonRepository.getPokemonInfo(pokemonName)
    }
}