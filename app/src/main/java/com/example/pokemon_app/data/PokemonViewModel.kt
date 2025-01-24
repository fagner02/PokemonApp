package com.example.pokemon_app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PokemonViewModel(context: Context) : ViewModel() {
    private val repository : PokemonRepository = PokemonRepository(PokemonDatabase.getDatabase(context.applicationContext).itemDao())
    var pokemons by mutableStateOf(listOf<EncounteredPokemon>())

    init {
        getPokemons()
    }

    fun getPokemons() {
        viewModelScope.launch {
            repository.getAllItems().collect {
                pokemons = it
            }
        }
    }
}