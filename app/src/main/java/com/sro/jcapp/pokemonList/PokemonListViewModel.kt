package com.sro.jcapp.pokemonList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sro.jcapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    var searchQuery = MutableStateFlow("")

    val pager = searchQuery.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { repository.getPokemonPagingSource(query) }
        ).flow.cachedIn(viewModelScope)
    }

//    val pager = Pager(
//        config = PagingConfig(
//            pageSize = 10, prefetchDistance = 10
//        ),
//        pagingSourceFactory = { repository }
//    ).flow.cachedIn(viewModelScope)


}