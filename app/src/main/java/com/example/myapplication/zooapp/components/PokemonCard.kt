package com.example.myapplication.zooapp.components

import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.zooapp.api.Pokemon
import com.example.myapplication.zooapp.favList
import com.example.myapplication.zooapp.ui.theme.typeColors

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    index: Int,
    scrollToItem: (Int)->Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = pokemon.name),
                    animatedVisibilityScope
                )
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    scrollToItem(index)
                },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            pokemon.sprites.front_default,
                            filterQuality = FilterQuality.None,
                            contentDescription = "${pokemon.name} Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-image"),
                                    animatedVisibilityScope
                                )
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = pokemon.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-name"),
                                    animatedVisibilityScope
                                )
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "${pokemon.name}-type"),
                                    animatedVisibilityScope
                                )) {
                                TypesLabel(pokemon)
                            }
                        }
                    }
                    TextButton(onClick = {
                        if (favList.contains(pokemon.name)) {
                            favList.remove(pokemon.name)
                        } else {
                            favList.add(pokemon.name)
                        }
                    }) {
                        Icon(
                            painterResource(if (favList.contains(pokemon.name)) R.drawable.favfill else R.drawable.favout),
                            contentDescription = "favoritar",
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "${pokemon.name}-fav"),
                                animatedVisibilityScope
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypesLabel(pokemon: Pokemon) {
    pokemon.types.forEach { type ->
        Box(
            modifier = Modifier
                .wrapContentSize()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(25)
                )
                .clip(RoundedCornerShape(25))
                .background(
                    color = Color(
                        parseColor(
                            typeColors[type.type.name] ?: "#000000"
                        )
                    )
                )
        ) {
            Text(
                text = type.type.name,
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
