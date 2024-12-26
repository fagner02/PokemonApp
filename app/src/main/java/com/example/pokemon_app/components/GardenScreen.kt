package com.example.pokemon_app.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokemon_app.R
import kotlinx.coroutines.delay

@Preview
@Composable
fun GardenScreen() {
    BoxWithConstraints {
        val painter: Painter = painterResource(id = R.drawable.poke)
        val back = painterResource(R.drawable.background)

        val ratio = back.intrinsicSize.height / back.intrinsicSize.width
        val pokeRatio = painter.intrinsicSize.height / painter.intrinsicSize.width

        var state by remember { mutableStateOf("idle-up") }

        val offset by animateFloatAsState(
            when(state){
                "idle-up"-> 10f
                "idle-down"->0f
                "stop"-> 0f
                else->0f
            },
            tween(200)
            , label = "up"
        )
        val scope = rememberCoroutineScope()

        var idling by remember { mutableStateOf(false) }
        LaunchedEffect(idling) {
            if (!idling) {
                idling = true
                state = "idle-up"
                delay(1000)
                state = "stop"
                delay(1000)
                idling = false
            }
        }
        LaunchedEffect(state) {
            if(state=="idle-up"){
                delay(200)
                state="idle-down"
            }
            if(state=="idle-down"){
                delay(200)
                state="idle-up"
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            with(back) {
                draw(size = Size(maxWidth.toPx(), ratio * maxWidth.toPx()))
            }
            val width = maxWidth.toPx() * 0.1f
            translate(0f, offset) {
                with(painter) {
                    draw(Size(width, pokeRatio * width))
                }
            }
        }
    }
}