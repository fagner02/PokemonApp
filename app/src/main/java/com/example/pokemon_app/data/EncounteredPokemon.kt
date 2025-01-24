package com.example.pokemon_app.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "items")
class EncounteredPokemon(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val num: Int
)

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: EncounteredPokemon)

    @Update
    suspend fun update(item: EncounteredPokemon)

    @Delete
    suspend fun delete(item: EncounteredPokemon)

    @Query("SELECT COUNT(id) from items")
    fun getCount(): Int

    @Query("SELECT * from items WHERE id = :id")
    fun getItem(id: Int): Flow<EncounteredPokemon>

    @Query("SELECT * from items ORDER BY num ASC")
    fun getAllItems(): Flow<List<EncounteredPokemon>>
}