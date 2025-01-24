package com.example.pokemon_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EncounteredPokemon::class], version = 1, exportSchema = false)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun itemDao(): PokemonDao

    companion object {
        @Volatile
        private var Instance: PokemonDatabase? = null

        fun getDatabase(applicationContext: Context): PokemonDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(applicationContext, PokemonDatabase::class.java, "pokemon_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
