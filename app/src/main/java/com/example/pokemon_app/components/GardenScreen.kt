package com.example.pokemon_app.components


import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pokemon_app.R
import com.example.pokemon_app.api.Pokemon
import com.example.pokemon_app.api.PokemonService
import com.example.pokemon_app.data.PokemonViewModel
import com.example.pokemon_app.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

@Composable
fun GardenScreen(pokemonModel: PokemonViewModel, context: Context) {
    val pokemonList: SnapshotStateMap<Int, Pokemon> = remember { mutableStateMapOf() }
    val timerFlow = remember {
        context.dataStore.data.map {
            it[timer] ?: 0
        }
    }
    val timer by timerFlow.collectAsState(initial = 0)
    LaunchedEffect(pokemonModel.pokemons.size) {
        pokemonModel.getPokemons()
        pokemonModel.pokemons.forEach {
            if (pokemonList.contains(it.id)) return@forEach
            val res = PokemonService().getPokemonById(it.num)
            if (res != null) {
                pokemonList[it.id] = res
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        var currentDiff by remember { mutableLongStateOf(0L) }
        var min by remember { mutableLongStateOf(0L) }
        var seconds by remember { mutableLongStateOf(0L) }
        LaunchedEffect(timer) {
            currentDiff=timer-Calendar.getInstance().timeInMillis
        }
        LaunchedEffect(currentDiff){
            min = (currentDiff)/(1000L*60L)
            seconds = (currentDiff-min*60L*1000L)/1000L
            delay(1000L)
            currentDiff = (timer-Calendar.getInstance().timeInMillis)
        }
        Text("Um novo pokemon aparecerá em ${min}m ${seconds}s")
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(modifier = Modifier.wrapContentSize()) {
                val back = painterResource(R.drawable.background)

                val ratio = back.intrinsicSize.height / back.intrinsicSize.width

                val pad by remember { mutableStateOf(16.dp) }
                Image(
                    ImageBitmap.imageResource(R.drawable.background),
                    "",
                    modifier = Modifier
                        .padding(pad)
                        .fillMaxWidth()
                        .height((LocalDensity.current.run { (ratio * (maxWidth.toPx() - pad.toPx() * 2)).toDp() }))
                        .clip(RoundedCornerShape(10.dp)),
                    filterQuality = FilterQuality.None,
                    contentScale = ContentScale.FillBounds,
                )

                pokemonList.forEach {
                    PokemonSprite(maxWidth, pad, ratio, it.value.sprites.front_default)
                }
            }
        }
    }
}

@Composable
fun PokemonSprite(maxWidth: Dp, pad: Dp, ratio: Float, url: String?){
    val jumpHeight by remember { mutableFloatStateOf(10f) }
    val offset = remember { Animatable(jumpHeight) }
    val posx = remember { Animatable(0f) }
    val posy = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var moving by remember { mutableStateOf(true) }
    val spriteWidth = LocalDensity.current.run { (maxWidth.toPx()*0.45f).toDp() }
    val widthPx =
        LocalDensity.current.run { (maxWidth.toPx() - pad.toPx() * 2 - spriteWidth.toPx()).toDp().value }
    val heightPx = ratio * widthPx

    DisposableEffect(moving) {
        onDispose {
            scope.launch {
                offset.animateTo(
                    jumpHeight,
                    tween(200, easing = LinearEasing),
                )
                delay((500..1000).random().toLong())
                moving = true
            }
        }
    }
    LaunchedEffect(moving) {
        if (moving) {
            val newx = widthPx * ((0..1000).random() / 1000f)
            val newy = heightPx * ((0..1000).random() / 1000f)
            val timex = abs(newx - posx.value) / widthPx
            val timey = abs(newy - posy.value) / heightPx
            var donex = false
            var doney = false
            scope.launch {
                offset.animateTo(
                    0f,
                    infiniteRepeatable(
                        tween(200),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            scope.launch {
                posx.animateTo(
                    newx,
                    tween((5000.0 * timex).toInt(), easing = LinearEasing)
                )
                donex = true
                if (doney)
                    moving = false
            }
            scope.launch {
                posy.animateTo(
                    newy,
                    tween((5000.0 * timey).toInt(), easing = LinearEasing)
                )
                doney = true
                if (donex)
                    moving = false
            }
        }
    }

    AsyncImage(
        url,
        "pokemon",
        contentScale = ContentScale.Fit,
        filterQuality = FilterQuality.None,
        modifier = Modifier
            .size(spriteWidth + pad)
            .padding(pad)
            .offset { IntOffset(posx.value.dp.toPx().toInt(), (posy.value.dp.toPx() + offset.value.dp.toPx()).toInt()) }
    )
}