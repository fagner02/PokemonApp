package com.example.myapplication.zooapp.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import com.example.myapplication.zooapp.Encounter
import com.example.myapplication.zooapp.Pokemon
import com.example.myapplication.zooapp.Service
import com.example.myapplication.zooapp.favList


@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailsScreen(
    pokemon: Pokemon,
    modifier: Modifier,
    onBack: ()->Unit,
    searchQuery: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    service: Service
) {
    with(sharedTransitionScope) {
        val encounters = remember { emptyList<Encounter>().toMutableList() }
        LaunchedEffect(true) {
            encounters.addAll(service.getEncounters(pokemon.name))
            println(encounters.size)
        }
        Column(
            modifier = modifier
        ) {
            var animate by remember { mutableStateOf(false) }
            val offset by animateIntAsState(if (animate) (-80) else 0,label = "offset")
            val height by animateDpAsState(targetValue = if (animate) 0.dp else 30.dp, label = "height")
            LaunchedEffect(true) {
                animate=true
            }
            TextField(
                value = searchQuery,
                onValueChange = { },
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "pesquisar") },
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(height)
                    .absoluteOffset { IntOffset(0,offset) }
            )
            Card(
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = pokemon.name),
                        animatedVisibilityScope
                    )
                    .padding(16.dp)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, "voltar")
                        }
                        IconButton(onClick = {
                            if (favList.contains(pokemon.name)) {
                                favList.remove(pokemon.name)
                            } else {
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
                            )
                        }
                    }
                    AsyncImage(
                        pokemon.sprites.front_default,
                        filterQuality = FilterQuality.None,
                        contentDescription = "${pokemon.name} Image",
                        modifier = Modifier
                            .sharedElement(
                                rememberSharedContentState(key = "${pokemon.name}-image"),
                                animatedVisibilityScope
                            )
                            .size(200.dp)
                    )

                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "${pokemon.name}-name"),
                            animatedVisibilityScope
                        )

                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "${pokemon.name}-type"),
                            animatedVisibilityScope
                        )
                    ) {
                        TypesLabel(pokemon)
                    }
//                    MusicPlayer(pokemon.cries.latest)
//                    pokemon.abilities.forEach{
//                        slot->
//                        Text(
//                            slot.ability.name
//                        )
//                    }

                    encounters.forEach {encounter ->
//                        encounter.version_details.forEach{version->
//                            Row {
//                                Text(
//                                    version.version.name
//                                )
//                                Text(
//                                    encounter.location_area.name
//                                )
//                            }
//                        }
                        Text(encounter.location_area.name)
                    }
                }
            }
        }
    }
}