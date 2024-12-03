package com.sro.jcapp.di

import com.sro.jcapp.data.remote.PokeApi
import com.sro.jcapp.repository.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getPokeApi() = Retrofit.Builder().baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create()).build().create(
        PokeApi::class.java
    )

    @Provides
    @Singleton
    fun getPokemonRepository(api: PokeApi) = PokemonRepository(api)
}