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
    val name: String
)

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: EncounteredPokemon)

    @Update
    suspend fun update(item: EncounteredPokemon)

    @Delete
    suspend fun delete(item: EncounteredPokemon)

    @Query("SELECT * from items WHERE id = :id")
    fun getItem(id: Int): Flow<EncounteredPokemon>

    @Query("SELECT * from items ORDER BY name ASC")
    fun getAllItems(): Flow<List<EncounteredPokemon>>
}