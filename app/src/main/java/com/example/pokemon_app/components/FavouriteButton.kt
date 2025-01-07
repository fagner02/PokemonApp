package com.example.pokemon_app.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.example.pokemon_app.R
import com.example.pokemon_app.api.Pokemon
import com.example.pokemon_app.favList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavouriteButton(
    pokemon: Pokemon,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    color: Color) {
    var selecting by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (selecting) 1.5f else 1f, label = "scale")
    val scope = rememberCoroutineScope()
    with(sharedTransitionScope) {
        IconButton(onClick = {
            if (favList.contains(pokemon.name)) {
                scope.launch {
                    selecting = true
                    delay(200)
                    selecting=false
                }
                favList.remove(pokemon.name)
            } else {
                scope.launch {
                    selecting = true
                    delay(200)
                    selecting=false
                }
                favList.add(pokemon.name)
            }
        }) {
            Icon(
                if (favList.contains(pokemon.name)) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                "favoritar",
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "${pokemon.name}-fav"),
                    animatedVisibilityScope
                )
                    .scale(scale.value),
                tint = color
            )
        }
    }
}
