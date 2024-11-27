package com.example.myapplication.zooapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.zooapp.ui.theme.MyApplicationTheme

data class Pokemon (
    val name: String,
    val type: Pair<String, String>,
    val nature: String,
    val abilities: String,
    val imgId: Int,
    val description: String,
    val location: String
)

val pikachu = Pokemon(
    name = "Pikachu",
    type = Pair("Electric", ""),
    nature = "Hardy",
    abilities = "Static, Lightning Rod",
    imgId = R.drawable.pika,
    description = "Um rato elétrico que armazena eletricidade em suas bochechas.",
    location = "Região de Kanto"
)

val charmander = Pokemon(
    name = "Charmander",
    type = Pair("Fire", ""),
    nature = "Timid",
    abilities = "Blaze",
    imgId = R.drawable.chara,
    description = "Uma pequena salamandra com uma chama na ponta da cauda.",
    location = "Região de Kanto"
)

val bulbasaur = Pokemon(
    name = "Bulbasaur",
    type = Pair("Grass", "Poison"),
    nature = "Quiet",
    abilities = "Overgrow",
    imgId = R.drawable.bulb,
    description = "Uma pequena criatura com uma semente nas costas.",
    location = "Região de Kanto"
)

val squirtle = Pokemon(
    name = "Squirtle",
    type = Pair("Water", ""),
    nature = "Bold",
    abilities = "Torrent",
    imgId = R.drawable.turt,
    description = "Uma pequena tartaruga que se esconde em sua carapaça.",
    location = "Região de Kanto"
)

val eevee = Pokemon(
    name = "Eevee",
    type = Pair("Normal", ""),
    nature = "Calm",
    abilities = "Run Away",
    imgId = R.drawable.eve,
    description = "Um Pokémon com genes instáveis que podem evoluir de várias maneiras.",
    location = "Região de Kanto"
)
val pokemons = listOf(pikachu, charmander, bulbasaur, squirtle, eevee)
class PokemonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var searchQuery by remember { mutableStateOf("") }
                    val filteredPokemon = remember(searchQuery) {
                        pokemons.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    }
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "list") {
                        composable("list") {
                            Column(modifier = Modifier.padding(innerPadding)) {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    label = { Text("Pesquisar") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    items(filteredPokemon) { pokemon ->
                                        ListItem(
                                            pokemon,
                                            onPokemonSelected = { navController.navigate("details/${pokemon.name}") })
                                    }
                                }
                            }
                        }

                        composable("details/{pokemon}") { entry ->
                            val name = entry.arguments?.getString("pokemon")
                            val selectedPokemon = pokemons.first { it.name == name }
                            PokemonScreen(selectedPokemon, modifier = Modifier.padding(innerPadding))
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun PokemonScreen(pokemon: Pokemon, modifier: Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = pokemon.imgId),
            contentDescription = "${pokemon.name} Image",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pokemon.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = pokemon.location,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pokemon.description,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@Composable
fun ListItem(pokemon: Pokemon, onPokemonSelected: (Pokemon) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = pokemon.imgId),
                    contentDescription = "${pokemon.name} Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = pokemon.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = pokemon.type.first+""+pokemon.type.second, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pokemon.nature,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Curiosidade: ${pokemon.abilities}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onPokemonSelected(pokemon) }) {
                Text("Mais sobre ${pokemon.name}")
            }
        }
    }
}
