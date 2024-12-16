package com.example.myapplication.zooapp.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import com.example.myapplication.zooapp.api.Encounter
import com.example.myapplication.zooapp.api.Pokemon
import com.example.myapplication.zooapp.api.PokemonService
import com.example.myapplication.zooapp.favList
import java.util.Locale


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
    service: PokemonService
) {
    with(sharedTransitionScope) {
        val encounters = remember { emptyList<Encounter>().toMutableList() }
        LaunchedEffect(true) {
            encounters.addAll(service.getEncounters(pokemon.name))
        }
        Column(
            modifier = modifier
        ) {
            var animate by remember { mutableStateOf(false) }
            val offset by animateIntAsState(if (animate) (-80) else 0, label = "offset")
            val height by animateDpAsState(
                targetValue = if (animate) 0.dp else 30.dp,
                label = "height"
            )
            LaunchedEffect(true) {
                animate = true
            }
            TextField(
                value = searchQuery,
                onValueChange = { },
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "pesquisar") },
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(height)
                    .absoluteOffset { IntOffset(0, offset) }
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
                val state = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(state),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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
                    MusicPlayer(pokemon.cries.latest)
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "Habilidades:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        pokemon.abilities.forEach { slot ->
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    text = slot.ability.name.replace("-", " ")
                                        .replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.getDefault()
                                            ) else it.toString()
                                        }
                                )
                            }
                        }
                    }
                    val list = remember { mutableMapOf<String, MutableList<String>>() }
                    LaunchedEffect(encounters.size) {
                        encounters.forEach { encounter ->
                            encounter.version_details.forEach { version ->
                                if (list[version.version.name] == null)
                                    list[version.version.name] = mutableStateListOf()
                                val loc = service.getLocation(encounter.location_area.url)
                                if (loc.names.isEmpty()) {
                                    list[version.version.name]?.add(encounter.location_area.name.replace(
                                        "-",
                                        " "
                                    )
                                        .replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                        })
                                } else {
                                    list[version.version.name]?.add(loc.names[0].name)
                                }
                            }
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            "Localizações:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        list.forEach { x ->
                            ExpandableCard(x.key.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }, containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    x.value.forEach { value ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                        ) {
                                            Text(
                                                value, Modifier
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}