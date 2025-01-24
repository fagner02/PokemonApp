package com.example.pokemon_app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun MusicPlayer(songUrl: String) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    var isLoaded by remember { mutableStateOf(false) }
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
                isLoaded = true
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
            .background(Color.Gray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            if (isLoaded) {
                IconButton(
                    onClick = {
                        if (totalDuration == 0L) return@IconButton
                        if (isPlaying) {
                            player.seekTo(0)
                        }
                        player.play()
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.onSurface)
                ) {
                    Icon(
                        Icons.Rounded.PlayArrow,
                        tint = MaterialTheme.colorScheme.surface,
                        contentDescription = "play"
                    )
                }
            } else CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
                    .size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                modifier = Modifier.padding(end = 16.dp),
                value = sliderPosition,
                thumb = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp)).size(ButtonDefaults.IconSize)
                            .background(MaterialTheme.colorScheme.onSurface)

                    )
                },
                onValueChange = {
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    if(totalDuration ==0L)return@Slider
                    player.seekTo(sliderPosition.toLong())
                },
                valueRange = 0f..(totalDuration.toFloat()),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onSurface,
                    activeTrackColor = MaterialTheme.colorScheme.onSurface,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    activeTickColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
