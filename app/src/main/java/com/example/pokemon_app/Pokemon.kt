package com.example.pokemon_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokemon_app.api.Pokemon
import com.example.pokemon_app.api.PokemonService
import com.example.pokemon_app.components.BottomBar
import com.example.pokemon_app.components.Drawer
import com.example.pokemon_app.components.GardenScreen
import com.example.pokemon_app.components.HelpAndSupportScreen
import com.example.pokemon_app.components.HomeScreen
import com.example.pokemon_app.components.SettingsScreen
import com.example.pokemon_app.components.TopBar
import com.example.pokemon_app.theme.PokemonAppTheme
import kotlinx.coroutines.delay

val favList: MutableList<String> = mutableStateListOf()

val screens = mapOf(
    "list" to 0,
    "fav" to 1,
    "garden" to 2,
    "settings" to 3,
    "help" to 4
)

class PokemonActivity : ComponentActivity() {
    private val service = PokemonService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
        )

        setContent {
            val list: MutableList<Pokemon> = remember { emptyList<Pokemon>().toMutableStateList() }
            var isDarkModeEnabled by remember { mutableStateOf(false) }
            val color = MaterialTheme.colorScheme.background.toArgb()
            var isLoading by remember { mutableStateOf(false) }

            LaunchedEffect(isDarkModeEnabled) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkModeEnabled) SystemBarStyle.dark(Color.Black.toArgb()) else SystemBarStyle.light(
                        color,
                        color
                    )
                )
            }
            LaunchedEffect(true) {
                isLoading = true
                var res = service.getPokemon()
                while (res != null) {
                    list.add(res)
                    delay(0)
                    res = service.getPokemon()
                }
                isLoading = false
            }

            PokemonAppTheme(
                darkTheme = isDarkModeEnabled
            ) {
                var lastRoute by remember { mutableStateOf("list") }
                var route by remember { mutableStateOf("list") }
                val navController = rememberNavController()
                navController.addOnDestinationChangedListener { _, dest, _ ->
                    run {
                        lastRoute = route
                        route = dest.route ?: "list"
                    }
                }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                Drawer(navController, drawerState) {
                    Scaffold(modifier = Modifier.fillMaxSize(),
                        bottomBar = { BottomBar(route, navController) },
                        topBar = { TopBar(route, navController, scope, drawerState) }
                    ) { innerPadding ->
                        var searchQuery by remember { mutableStateOf("") }
                        val listState = rememberLazyListState()
                        val favListState = rememberLazyListState()
                        var isAnimating by remember { mutableStateOf(false) }

                        LaunchedEffect(isAnimating) {
                            if (isAnimating) {
                                delay(450)
                                isAnimating = false
                            }
                        }

                        NavHost(navController, startDestination = "list") {
                            composable(
                                "list",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                            ) {
                                HomeScreen(
                                    list,
                                    { input ->
                                        searchQuery = input
                                    },
                                    searchQuery,
                                    listState,
                                    isLoading,
                                    isAnimating,
                                    setIsAnimating = { isAnimating = true },
                                    service,
                                    Modifier.padding(innerPadding)
                                )
                            }
                            composable("fav",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                                ) {
                                HomeScreen(
                                    list.filter { favList.contains(it.name) }.toMutableStateList(),
                                    { input -> searchQuery = input },
                                    searchQuery,
                                    favListState,
                                    false,
                                    isAnimating,
                                    setIsAnimating = { isAnimating = true },
                                    service,
                                    Modifier.padding(innerPadding)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(innerPadding)
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Você ainda não tem favoritos")
                                    }
                                }
                            }
                            composable("garden",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                                ) {
                                GardenScreen(list.filter { x -> x.name.contains("cha") })
                            }
                            composable("settings",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                            ) {
                                var isNotificationsEnabled by remember { mutableStateOf(true) }
                                val context = LocalContext.current

                                SettingsScreen(
                                    isDarkModeEnabled = isDarkModeEnabled,
                                    isNotificationsEnabled = isNotificationsEnabled,
                                    onToggleDarkMode = { isDarkModeEnabled = it },
                                    onToggleNotifications = { isNotificationsEnabled = it },
                                    onClearFavorites = {
                                        favList.clear()
                                        Toast.makeText(
                                            context,
                                            "Favoritos limpos com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onResetPreferences = {
                                        isDarkModeEnabled = false
                                        isNotificationsEnabled = true
                                        favList.clear()
                                        Toast.makeText(
                                            context,
                                            "Preferências redefinidas com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                            composable("help",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                            ) {
                                val context = LocalContext.current
                                HelpAndSupportScreen(onSendSupportMessage = { message ->
                                    Toast.makeText(
                                        context,
                                        "Mensagem enviada: $message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }, modifier = Modifier.padding(innerPadding))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getInTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>,
    lastRoute: String,
    currentRoute: String
): EnterTransition {
    with(scope) {
        if ((screens[lastRoute] ?: 0) >= (screens[currentRoute] ?: 0))
            return slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End, tween()
            )
        else
            return slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween()
            )
    }
}

fun getOutTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>,
    nextRoute: String,
    currentRoute: String
): ExitTransition {
    with(scope) {
        if ((screens[nextRoute] ?: 0) >= (screens[currentRoute] ?: 0))
            return slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start, tween()
            )
        else
            return slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween()
            )
    }
}