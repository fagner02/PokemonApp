package com.example.pokemon_app.data

import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val pokemonDao: PokemonDao) {
     fun getAllItems(): Flow<List<EncounteredPokemon>> = pokemonDao.getAllItems()

     fun getCount(): Int = pokemonDao.getCount()

     fun getItem(id: Int): Flow<EncounteredPokemon?> = pokemonDao.getItem(id)

     public suspend fun addItem(item: EncounteredPokemon) = pokemonDao.insert(item)

     suspend fun deleteItem(item: EncounteredPokemon) = pokemonDao.delete(item)

     suspend fun updateItem(item: EncounteredPokemon) = pokemonDao.update(item)
}