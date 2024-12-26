package com.example.pokemon_app.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokemon_app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Preview
@Composable
fun GardenScreen() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val painter: Painter = painterResource(id = R.drawable.poke)
        val back = painterResource(R.drawable.background)

        val ratio = back.intrinsicSize.height / back.intrinsicSize.width
        val pokeRatio = painter.intrinsicSize.height / painter.intrinsicSize.width

        val offset = remember { Animatable(50f) }
        val posx = remember { Animatable(0f) }
        val posy = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()
        var moving by remember { mutableStateOf(true) }
        val pad by remember { mutableStateOf(16.dp) }
        val widthPx = LocalDensity.current.run { (maxWidth.toPx() - pad.toPx()*2-painter.intrinsicSize.width).toDp().value }
        val heightPx = ratio*widthPx
        val hipt by remember { mutableStateOf(sqrt(widthPx.pow(2)+heightPx.pow(2))) }

        DisposableEffect(moving) {
            onDispose {
                scope.launch {
                    offset.animateTo(
                        50f,
                        tween(200, easing = LinearEasing),
                    )
                    delay((500..1000).random().toLong())
                    moving = true
                }
            }
        }
        LaunchedEffect(moving) {
            if(moving){
                val newx = widthPx * ((0..1000).random()/1000f);
                val newy = heightPx * ((0..1000).random()/1000f)
                val timex = abs(newx-posx.value)/widthPx
                val timey = abs(newy-posy.value)/heightPx
                val time = sqrt((posx.value-newx).pow(2) + (posy.value-newy).pow(2))/hipt
                scope.launch {
                    try{
                        offset.animateTo(
                            0f,
                            infiniteRepeatable(
                                tween(200),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                    }catch (e: Throwable){
                        println("erro")
                    }
                }
                var donex = false
                var doney = false
                scope.launch {
                    posx.animateTo(newx,
                        tween((5000.0*timex).toInt(), easing = LinearEasing))
                    donex =true
                    if(donex && doney)
                        moving=false
                }
                scope.launch {
                    posy.animateTo(newy,
                        tween((5000.0*timey).toInt(), easing = LinearEasing)
                    )
                    doney=true
                    if(donex&&doney)
                        moving=false
                }
            }
        }
//        var idling by remember { mutableStateOf(false) }
//        LaunchedEffect(idling) {
//            scope.launch {
//                if (!idling) {
//                    idling = true
//
//                    delay(1000)
//
//                    delay(1000)
//                    idling = false
//                }
//            }
//        }

        Image(
            ImageBitmap.imageResource(R.drawable.background),
            "",
            modifier = Modifier
                .padding(pad)
                .fillMaxWidth()
                .height((LocalDensity.current.run { (ratio * (maxWidth.toPx() - pad.toPx() * 2)).toDp() }))
                ,
            filterQuality = FilterQuality.None,
            contentScale = ContentScale.FillBounds,
        )
        Image(ImageBitmap.imageResource(R.drawable.poke), "",
            filterQuality = FilterQuality.None,
            modifier = Modifier
                .padding(pad)
                .offset(posx.value.dp, posy.value.dp + offset.value.dp))
    }
}