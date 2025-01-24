package com.example.pokemon_app.components

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pokemon_app.R
import com.example.pokemon_app.data.EncounteredPokemon
import com.example.pokemon_app.data.PokemonDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@RequiresApi(Build.VERSION_CODES.S)
fun setAlarm(context: Context){
    val calendar = Calendar.getInstance()
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, PokemonEncounterReceiver::class.java)

    intent.putExtra("pokemon", "456")
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    if(!alarmManager.canScheduleExactAlarms()){
        Toast.makeText(context, "Permissão necessária para configurar alarmes exatos.", Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    }
    val hours = 0;
    val miliseconds = hours * 60 * 60 * 1000
    if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, calendar.timeInMillis + 1000, pendingIntent)
    }

}

@OptIn(DelicateCoroutinesApi::class)
fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.(BroadcastReceiver.PendingResult) -> Unit){
    val pendingResult: BroadcastReceiver.PendingResult = goAsync()
    GlobalScope.launch(context) {
        try {
            block(pendingResult)
        } finally {
            pendingResult.finish()
        }
    }
}

class PokemonEncounterReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) = goAsync {
        println("recebido")
        val itemDao = PokemonDatabase.getDatabase(context.applicationContext).itemDao()
        val pokemon = intent.getStringExtra("pokemon")
        if (pokemon != null) {
            itemDao.insert(EncounteredPokemon(name = pokemon))
        }

        val builder = NotificationCompat.Builder(context, "NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.poke)
            .setContentTitle(pokemon)
            .setContentText("content")
            .setPriority(NotificationCompat.PRIORITY_MAX)

        with(NotificationManagerCompat.from(context)) {

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@goAsync
            }
            notify("title".hashCode(), builder.build())
        }
        setAlarm(context)
    }
}