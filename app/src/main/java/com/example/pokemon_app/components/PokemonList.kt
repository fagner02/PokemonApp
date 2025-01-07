package com.example.pokemon_app.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.pokemon_app.api.Pokemon
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
            val filtered = list.filter { it.name.contains(searchQuery) }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clipToBounds()
                    .padding(horizontal = 8.dp),
                state = state,
                userScrollEnabled = !isAnimating
            ) {
                items(
                    filtered.size + if (isLoading) (if (searchQuery=="") 50 else 5) else 0,
                    key = { if (it < filtered.size) filtered[it].name else "$it" },) {
                    if (it < filtered.size) {
                        val pokemon = filtered[it]
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
                                    onSelectPokemon(pokemon.name)
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
                            pokemon,
                            it,
                            scrollToItem = {
                                if (!isAnimating) {
                                    coroutine.launch {
                                        state.animateScrollToItem(index = it, -500)
                                    }
                                    selecting = true
                                }
                            },
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedTransitionScope = sharedTransitionScope,
                            modifier = Modifier.animateItem()
                        )
                    } else {
                        PokemonCardPlaceholder()
                    }
                }
            }
        }
    }
}

fun Modifier.shimmer(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")

    val x by transition.animateFloat(
        0f,
        size.width.toFloat() * 2,
        animationSpec = infiniteRepeatable(
            tween(500, easing = LinearEasing)
        ), label = "shimmer"
    )

    background(
        Brush.horizontalGradient(
            listOf(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                Color(0xff555555),
                MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            startX = x - size.width.toFloat(),
            endX = x
        ),
        shape = RoundedCornerShape(10.dp)
    ).onGloballyPositioned { size = it.size }
}

