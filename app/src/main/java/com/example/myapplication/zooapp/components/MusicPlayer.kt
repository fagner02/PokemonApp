package com.example.myapplication.zooapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@UnstableApi
@Composable
fun MusicPlayer(songUrl: String) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    var isPlaying by remember { mutableStateOf(false) }
    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    var sliderPosition by remember {
        mutableFloatStateOf(0f)
    }

    var totalDuration by remember {
        mutableLongStateOf(0)
    }

    player.addListener(object : Player.Listener {
        @Deprecated("Deprecated in Java")
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == ExoPlayer.STATE_READY && player.isPlaying) {
                isPlaying = true
            }
            if (playbackState == ExoPlayer.STATE_ENDED) {
                isPlaying = false
                player.pause()
                player.seekTo(0)
            }
            if (playbackState == ExoPlayer.STATE_READY && totalDuration == 0L) {
                totalDuration = player.duration
            }
        }
    })

    val scope = rememberCoroutineScope()
    LaunchedEffect(isPlaying) {
        if(!isPlaying){
            sliderPosition = 0f
        }
        scope.launch {
            while (isPlaying) {
                sliderPosition = player.currentPosition.toFloat()
                delay(10)
            }
        }
    }

    LaunchedEffect(true) {
        player.addMediaItem(MediaItem.fromUri(songUrl))
        player.prepare()
        currentPosition.longValue = player.currentPosition
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(25))
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Row(
        ) {
            IconButton(
                onClick = {
                    if(totalDuration == 0L) return@IconButton
                    if(isPlaying){
                        player.seekTo(0)
                    }
                    player.play()
                }
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "play"
                )
            }
            Slider(
                modifier = Modifier.padding(end = 16.dp),
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    isPlaying = false
                },
                onValueChangeFinished = {
                    if(totalDuration ==0L)return@Slider
                    player.seekTo(sliderPosition.toLong())
                },
                valueRange = 0f..(totalDuration.toFloat()),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onPrimary,
                    activeTrackColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    }
}
