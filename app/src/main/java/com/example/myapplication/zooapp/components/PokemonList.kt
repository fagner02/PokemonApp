package com.example.myapplication.zooapp.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.myapplication.zooapp.api.Pokemon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonList(
    list: MutableList<Pokemon>,
    modifier: Modifier,
    onSelectPokemon: (String) -> Unit,
    onInputQuery: (String) -> Unit,
    searchQuery: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: LazyListState,
    isLoading: Boolean,
    isAnimating: Boolean,
    setIsAnimating: () -> Unit
) {
    with(sharedTransitionScope) {
        Column(modifier = modifier.fillMaxSize()) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    onInputQuery(it)
                },
                singleLine = true,
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Pesquisar") },
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Pesquisar") },
                enabled = true,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()

            )

            val coroutine = rememberCoroutineScope()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clipToBounds()
                    .padding(horizontal = 8.dp),
                state = state,
                userScrollEnabled = !isAnimating
            ) {
                itemsIndexed(list.filter { it.name.contains(searchQuery) }) { index, pokemonName ->
                    var selecting by remember { mutableStateOf(false) }
                    var timedout by remember { mutableStateOf(false) }
                    if (selecting && (state.isScrollInProgress && !timedout)) {
                        DisposableEffect(Unit) {
                            onDispose {
                                coroutine.launch {
                                    state.stopScroll()
                                }
                                selecting = false
                                timedout = false

                                setIsAnimating()
                                onSelectPokemon(pokemonName.name)
                            }
                        }
                    }
                    if (selecting) {
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(100.milliseconds)
                                timedout = true
                            }
                        }
                    }

                    PokemonCard(
                        pokemonName,
                        index,
                        scrollToItem = {
                            if(!isAnimating) {
                                coroutine.launch {
                                    state.animateScrollToItem(index = index, -500)
                                }
                                selecting = true
                            }
                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                    )
                }
                if (isLoading) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}


