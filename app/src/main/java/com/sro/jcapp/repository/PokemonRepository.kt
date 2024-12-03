package com.sro.jcapp.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sro.jcapp.data.remote.PokeApi
import com.sro.jcapp.data.remote.responses.Pokemon
import com.sro.jcapp.data.remote.responses.Result
import com.sro.jcapp.util.Resource
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {

    fun getPokemonPagingSource(query: String): PokemonPagingSource {
        return PokemonPagingSource(api, query)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occured.")
        }

        return Resource.Success(response)
    }

}

class PokemonPagingSource(
    private val api: PokeApi,
    private val query: String
) : PagingSource<Int, Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        return try {
            val offset = params.key ?: 0 // Start at offset 0 if no key provided
            val response = api.getPokemonList(limit = params.loadSize, offset = offset)

            // Filter results based on the search query
            val filteredResults = if (query.isNotEmpty()) {
                response.results.filter { it.name.contains(query, ignoreCase = true) }
            } else {
                response.results
            }

            val nextKey = if (filteredResults.isEmpty()) null else offset + params.loadSize
            val prevKey = if (offset == 0) null else offset - params.loadSize

            LoadResult.Page(
                data = filteredResults,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.e("PokemonPagingSource", "Error loading data", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        return state.anchorPosition
    }


}