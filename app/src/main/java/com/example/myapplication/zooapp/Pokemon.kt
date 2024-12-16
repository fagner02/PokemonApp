@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.myapplication.zooapp

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.zooapp.components.BottomBar
import com.example.myapplication.zooapp.components.DetailsScreen
import com.example.myapplication.zooapp.components.HelpAndSupportScreen
import com.example.myapplication.zooapp.components.PokemonCard
import com.example.myapplication.zooapp.components.SettingsScreen
import com.example.myapplication.zooapp.components.TopBar
import com.example.myapplication.zooapp.ui.theme.MyApplicationTheme
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds
import io.ktor.client.request.url as _url

@Serializable
data class ApiResource(val url: String, val name:String)
@Serializable
data class ApiResourceList(val next: String?, val previous: String?, val results: List<ApiResource>)
@Suppress("PropertyName")
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
data class Encounter(val location_area: Name, val version_details:  List<EncounterVersion>)
@Serializable
data class Pokemon(val name: String, val types: List<TypeSlot>, val sprites: Sprites, val cries: Cries, val abilities: List<AbilitySlot>)

class Service {
    private val client=HttpClient()
    private val api = "https://pokeapi.co/api/v2"
    private var next: String? = "${api}/pokemon?offset=0&limit=10"
    suspend fun getList(): MutableList<Pokemon> {
        try {
            val res = client.get { _url("$next") }
            val resourceList = Gson().fromJson(res.bodyAsText(), ApiResourceList::class.java)
            val pokemons:MutableList<Pokemon> = emptyList<Pokemon>().toMutableList()
            for (resource in resourceList.results){
                val pokeRes =
                    HttpClient().get { _url("https://pokeapi.co/api/v2/pokemon/${resource.name}") }
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
            val res = client.get { _url("$api/pokemon/$pokemon/encounters") }
            val encounters = Gson().fromJson(res.bodyAsText(), mutableListOf<Encounter>()::class.java)
            return encounters.toMutableStateList()
        } catch (e: Error){
            return emptyList<Encounter>().toMutableStateList()
        }
    }
}
val favList: MutableList<String> = mutableStateListOf()

class PokemonActivity : ComponentActivity() {
    private val service = Service()

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val list: MutableList<Pokemon> = remember { emptyList<Pokemon>().toMutableStateList() }
            var isDarkModeEnabled by remember { mutableStateOf(false) }
            LaunchedEffect(true) {
                service.getEncounters("pikachu")
            }

            MyApplicationTheme(
                darkTheme = isDarkModeEnabled
            ) {
                var route by remember { mutableStateOf("list") }
                val navController = rememberNavController()
                navController.addOnDestinationChangedListener { _, dest, _ ->
                    run {
                        route = dest.route ?: "list"
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(Modifier.fillMaxWidth(0.6f)) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        TextButton(onClick = {
                                            navController.navigate("settings")
                                        }) {
                                            Text("Configurações")
                                        }
                                        TextButton(onClick = {
                                            navController.navigate("help")
                                        }) {
                                            Text("Ajuda")
                                        }

                                    }
                                }
                            }
                        }
                    ) {

                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Scaffold(modifier = Modifier.fillMaxSize(),
                                bottomBar = { BottomBar(route, navController) },
                                topBar = { TopBar(route, navController, scope, drawerState) }
                            ) { innerPadding ->
                                var searchQuery by remember { mutableStateOf("") }
                                val listState = rememberLazyListState()
                                val favListState = rememberLazyListState()
                                var isLoading by remember{ mutableStateOf( false)}

                                val reachedBottom by remember {
                                    derivedStateOf {
                                        val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                        (last == null || listState.layoutInfo.totalItemsCount <= last.index + 5)
                                    }
                                }

                                LaunchedEffect(reachedBottom) {
                                    if (reachedBottom) {
                                        isLoading=true
                                        list.addAll(service.getList())
                                        isLoading=false
                                    }
                                }

                                NavHost(navController, startDestination = "list") {
                                    composable("list") {
                                        var selected by remember { mutableStateOf("") }
                                        SharedTransitionLayout {
                                            AnimatedContent(selected, label = "hero") { state ->
                                                if (state == "") {
                                                    PokemonList(
                                                        list,
                                                        animatedVisibilityScope = this@AnimatedContent,
                                                        sharedTransitionScope = this@SharedTransitionLayout,
                                                        modifier = Modifier.padding(innerPadding),
                                                        searchQuery = searchQuery,
                                                        onSelectPokemon = { name ->
                                                            selected = name
                                                        },
                                                        onInputQuery = { input ->
                                                            searchQuery = input
                                                        },
                                                        state = listState,
                                                        isLoading = isLoading
                                                    )
                                                } else {
                                                    val pokemon: Pokemon? by remember {
                                                        mutableStateOf(list.find { it.name == state })
                                                    }
                                                    if (pokemon != null) {
                                                        DetailsScreen(
                                                            pokemon!!,
                                                            modifier = Modifier.padding(innerPadding),
                                                            { selected = "" },
                                                            searchQuery,
                                                            this@SharedTransitionLayout,
                                                            this@AnimatedContent,
                                                            service
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    composable("fav") {
                                        var selected by remember { mutableStateOf("") }
                                        SharedTransitionLayout {
                                            AnimatedContent(selected, label = "fav") { state ->
                                                if (state == "") {
                                                    val favourites =
                                                        list.filter { favList.contains(it.name) }
                                                    if (favourites.isEmpty()) {
                                                        Box(modifier = Modifier.padding(innerPadding)
                                                            .fillMaxSize()
                                                            , contentAlignment = Alignment.Center) {
                                                            Text("Você ainda não tem favoritos")
                                                        }
                                                    } else {
                                                        PokemonList(
                                                            favourites,
                                                            modifier = Modifier.padding(innerPadding),
                                                            animatedVisibilityScope = this@AnimatedContent,
                                                            sharedTransitionScope = this@SharedTransitionLayout,
                                                            onInputQuery = { searchQuery = it },
                                                            onSelectPokemon = { selected = it },
                                                            searchQuery = searchQuery,
                                                            state = favListState,
                                                            isLoading = isLoading
                                                        )
                                                    }
                                                } else {
                                                    val pokemon: Pokemon? by remember {
                                                        mutableStateOf(list.find { it.name == state })
                                                    }
                                                    if (pokemon != null) {
                                                        DetailsScreen(
                                                            pokemon!!,
                                                            modifier = Modifier.padding(innerPadding),
                                                            { selected = "" },
                                                            searchQuery,
                                                            this@SharedTransitionLayout,
                                                            this@AnimatedContent,
                                                            service
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    composable("settings") {
                                        var isNotificationsEnabled by remember { mutableStateOf(true) }
                                        val context = LocalContext.current

                                        SettingsScreen(
                                            isDarkModeEnabled = isDarkModeEnabled,
                                            isNotificationsEnabled = isNotificationsEnabled,
                                            onToggleDarkMode = { isDarkModeEnabled = it },
                                            onToggleNotifications = { isNotificationsEnabled = it },
                                            onClearFavorites = {
                                                favList.clear()
                                                Toast.makeText(context, "Favoritos limpos com sucesso!", Toast.LENGTH_SHORT).show()
                                            },
                                            onResetPreferences = {
                                                isDarkModeEnabled = false
                                                isNotificationsEnabled = true
                                                favList.clear()
                                                Toast.makeText(context,"Preferências redefinidas com sucesso!", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                    composable("help") {
                                        val context = LocalContext.current
                                        HelpAndSupportScreen (onSendSupportMessage = { message ->
                                            Toast.makeText(context, "Mensagem enviada: $message", Toast.LENGTH_SHORT).show()
                                        },modifier = Modifier.padding(innerPadding))
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonList(
    list: List<Pokemon>,
    modifier: Modifier,
    onSelectPokemon: (String)-> Unit,
    onInputQuery: (String)->Unit,
    searchQuery: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    state: LazyListState,
    isLoading: Boolean) {
    with(sharedTransitionScope) {
        Column(modifier = modifier.fillMaxSize()) {
            TextField(
                value = searchQuery,
                onValueChange = onInputQuery,
                singleLine = true,
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.colors().copy(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Pesquisar") },
                trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Pesquisar") },
                enabled = true,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "text"),
                        animatedVisibilityScope
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            )


            val coroutine = rememberCoroutineScope()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clipToBounds().padding(horizontal = 8.dp),
                state = state
            ) {
                itemsIndexed(list.filter { it.name.contains(searchQuery) }) { index, pokemonName ->
                    var selecting by remember { mutableStateOf(false) }
                    var timedout by remember { mutableStateOf(false) }
                    if (selecting && (state.isScrollInProgress && !timedout)) {
                        DisposableEffect(Unit) {
                            onDispose {
                                coroutine.launch {
                                    state.stopScroll()
                                }
                                selecting = false
                                timedout = false


                            }
                        }
                    }
                    if (selecting) {
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(100.milliseconds)
                                timedout = true
                            }
                        }
                    }

                    PokemonCard(
                        pokemonName,
                        index,
                        scrollToItem = {
                            onSelectPokemon(pokemonName.name)
                        },
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope
                    )
                }
                if (isLoading) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }
            }
        }
    }
}


