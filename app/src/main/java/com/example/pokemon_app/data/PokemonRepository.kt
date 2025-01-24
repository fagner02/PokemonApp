package com.example.pokemon_app.data

import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val pokemonDao: PokemonDao) {
     fun getAllItems(): Flow<List<EncounteredPokemon>> = pokemonDao.getAllItems()

     fun getCount(): Int = pokemonDao.getCount()
}