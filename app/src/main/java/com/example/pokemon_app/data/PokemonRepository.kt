package com.example.pokemon_app.data

import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val itemDao: ItemDao) {
     fun getAllItems(): Flow<List<EncounteredPokemon>> = itemDao.getAllItems()

     fun getItem(id: Int): Flow<EncounteredPokemon?> = itemDao.getItem(id)

     public suspend fun addItem(item: EncounteredPokemon) = itemDao.insert(item)

     suspend fun deleteItem(item: EncounteredPokemon) = itemDao.delete(item)

     suspend fun updateItem(item: EncounteredPokemon) = itemDao.update(item)
}