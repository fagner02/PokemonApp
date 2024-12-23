package com.example.myapplication.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarme Disparado!", Toast.LENGTH_LONG).show()
        val mediaPlayer= MediaPlayer()
        intent.getParcelableExtra<Uri>("Ringtone")?.let { mediaPlayer.setDataSource(context, it) }
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build())
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}

