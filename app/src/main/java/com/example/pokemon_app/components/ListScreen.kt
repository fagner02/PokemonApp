package com.example.pokemon_app.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.pokemon_app.api.Pokemon
import com.example.pokemon_app.api.PokemonService

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ListScreen(
    list: MutableList<Pokemon>,
    searchInput: (String)->Unit,
    searchQuery: String,
    listState: LazyListState,
    isLoading: Boolean,
    isAnimating: Boolean,
    setIsAnimating: ()->Unit,
    service: PokemonService,
    modifier: Modifier,
    emptyListContent: (@Composable () -> Unit)? = null
) {
    var selected by remember { mutableStateOf("") }
    SharedTransitionLayout {
        AnimatedContent(selected, label = "hero") { state ->
            if (state == "") {
                if (emptyListContent != null && list.isEmpty()){
                    emptyListContent()
                }
                if(list.isNotEmpty() || emptyListContent == null) {
                    PokemonList(
                        list,
                        modifier = modifier,
                        onSelectPokemon = { name ->
                            selected = name
                        },
                        onInputQuery = searchInput,
                        searchQuery = searchQuery,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                        state = listState,
                        isLoading = isLoading,
                        isAnimating = isAnimating,
                        setIsAnimating = setIsAnimating
                    )
                }
            } else {
                val pokemon: Pokemon? by remember {
                    mutableStateOf(list.find { it.name == state })
                }
                if (pokemon != null) {
                    DetailsScreen(
                        pokemon!!,
                        modifier = modifier,
                        { selected = "" },
                        searchQuery,
                        this@SharedTransitionLayout,
                        this@AnimatedContent,
                        service,
                        isAnimating,
                        setIsAnimating = setIsAnimating
                    )
                }
            }
        }
    }
}