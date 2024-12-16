package com.example.myapplication.zooapp.api

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class ApiResource(val url: String, val name:String)
@Serializable
data class ApiResourceList(val next: String?, val previous: String?, val results: List<ApiResource>)
@Serializable
data class Sprites(val front_default: String?)
@Serializable
data class TypeSlot(val slot: Int, val type: Name)
@Serializable
data class Cries(val latest: String, val latency: String)
@Serializable
data class Name(val name: String)
@Serializable
data class AbilitySlot(val is_hidden: Boolean, val slot: Int, val ability: Name)
@Serializable
data class EncounterDetails(val chance: Int, val max_level: Int, val min_level: Int, val method: Name)
@Serializable
data class EncounterVersion(val encounter_details: List<EncounterDetails>, val max_chance: Int, val version: Name)
@Serializable
data class Encounter(val location_area: ApiResource, val version_details:  List<EncounterVersion>)
@Serializable
data class Pokemon(val name: String, val types: List<TypeSlot>, val sprites: Sprites, val cries: Cries, val abilities: List<AbilitySlot>)
@Serializable
data class Location(val names: List<Name>)

class PokemonService {
    private val client= HttpClient()
    private val api = "https://pokeapi.co/api/v2"
    private var next: String? = "${api}/pokemon?offset=0&limit=10"
    suspend fun getList(): MutableList<Pokemon> {
        try {
            val res = client.get { url("$next") }
            val resourceList = Gson().fromJson(res.bodyAsText(), ApiResourceList::class.java)
            val pokemons:MutableList<Pokemon> = emptyList<Pokemon>().toMutableList()
            for (resource in resourceList.results){
                val pokeRes =
                    HttpClient().get { url("https://pokeapi.co/api/v2/pokemon/${resource.name}") }
                val pokemon =
                    Gson().fromJson(pokeRes.bodyAsText(), Pokemon::class.java)
                pokemons.add(pokemon)
            }
            next = resourceList.next
            return pokemons
        }
        catch (e:Error) {
            return  mutableStateListOf()
        }
    }

    suspend fun getEncounters(pokemon: String): MutableList<Encounter> {
        try{
            val res = client.get { url("$api/pokemon/$pokemon/encounters") }
            val listType = object : TypeToken<List<Encounter>>() {}.type
            val encounters = Gson().fromJson<List<Encounter>>(res.bodyAsText(), listType)
            return encounters.toMutableStateList()
        } catch (e: Error){
            return emptyList<Encounter>().toMutableStateList()
        }
    }

    suspend fun getLocation(url: String): Location{
        try{
            val res = client.get { url(url) }
            val location = Gson().fromJson(res.bodyAsText(), Location::class.java)
            return location
        }catch (e: Error){
            return Location(emptyList())
        }
    }
}
