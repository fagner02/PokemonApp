package com.example.myapplication.animalapp

import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myapplication.R

@Composable
fun AnimalScreen(animal: String="Cat", modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val desc=if (animal =="Cat") "um gato" else "um cachorro"
    val id = if(animal=="Cat") R.drawable.cat else R.drawable.dog
    val mediaPlayer=MediaPlayer.create(context, if(animal=="Cat") R.raw.cat else R.raw.dog)
    val videoId = if(animal=="Cat") R.raw.catvideo else R.raw.dogvideo
    val uri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(videoId))
        .appendPath(context.resources.getResourceTypeName(videoId))
        .appendPath(context.resources.getResourceEntryName(videoId))
        .build()
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            this.setMediaItem(MediaItem.fromUri(uri))
            this.prepare()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()) {
        Image(painter = painterResource
            (id = id),
            contentDescription = desc,
            contentScale = ContentScale.Crop,
            modifier= Modifier
                .size(150.dp)
                .clip(CircleShape))
        Button(onClick = {
            mediaPlayer.start()
        }){Text(
            text = "Reproduzir Som",
            modifier = modifier
        )}
        Button(onClick = {
            exoPlayer.play()
        }){Text(
            text = "Reproduzir Vídeo",
            modifier = modifier
        )}
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .height(100.dp)
                .weight(weight = 1f, false)
        )
    }
}