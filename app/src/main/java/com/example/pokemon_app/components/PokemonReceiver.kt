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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.pokemon_app.R
import com.example.pokemon_app.api.PokemonService
import com.example.pokemon_app.data.EncounteredPokemon
import com.example.pokemon_app.data.PokemonDatabase
import com.example.pokemon_app.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.abs
import kotlin.random.Random


val timer = longPreferencesKey("timer")

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.S)
fun setAlarm(context: Context, initial: Boolean = false){
    val calendar = Calendar.getInstance()
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, PokemonEncounterReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    if(!alarmManager.canScheduleExactAlarms()){
        Toast.makeText(context, "Permissão necessária para configurar alarmes exatos.", Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
    }

    val hours = 1
    var miliseconds = hours * 60 * 60 * 1000
    if (initial){
        miliseconds = 0
    }
    if (alarmManager.canScheduleExactAlarms()) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, calendar.timeInMillis + miliseconds, pendingIntent)
    }
    GlobalScope.launch {
        context.dataStore.edit {
            it[timer] = calendar.timeInMillis + miliseconds
        }
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
        val itemDao = PokemonDatabase.getDatabase(context.applicationContext).itemDao()
        val pokemonId =  abs(Random.nextInt()%1000)
        itemDao.insert(EncounteredPokemon(num =pokemonId))
        val pokemon = PokemonService().getPokemonById(pokemonId)
        if (pokemon == null) {
            setAlarm(context)
            return@goAsync
        }
        val name = pokemon.name.replaceFirstChar { it.uppercase() }

        val builder = NotificationCompat.Builder(context, "NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.poke)
            .setContentTitle("$name encontrado")
            .setContentText("Um $name apareceu no seu jardim")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@goAsync
            }
            notify(pokemonId, builder.build())
        }
        setAlarm(context)
    }
}