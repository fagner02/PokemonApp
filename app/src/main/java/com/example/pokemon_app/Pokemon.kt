package com.example.pokemon_app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.collectAsState
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
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokemon_app.api.Pokemon
import com.example.pokemon_app.api.PokemonService
import com.example.pokemon_app.components.BottomBar
import com.example.pokemon_app.components.GardenScreen
import com.example.pokemon_app.components.HelpAndSupportScreen
import com.example.pokemon_app.components.ListScreen
import com.example.pokemon_app.components.SettingsScreen
import com.example.pokemon_app.components.TopBar
import com.example.pokemon_app.components.setAlarm
import com.example.pokemon_app.data.AuthRepository
import com.example.pokemon_app.data.AuthViewModel
import com.example.pokemon_app.data.ForgotPasswordScreen
import com.example.pokemon_app.data.LoginScreen
import com.example.pokemon_app.data.PokemonDatabase
import com.example.pokemon_app.data.PokemonRepository
import com.example.pokemon_app.data.PokemonViewModel
import com.example.pokemon_app.data.RegisterScreen
import com.example.pokemon_app.theme.PokemonAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val favList: MutableList<String> = mutableStateListOf()

val screens = mapOf(
    "list" to 0,
    "fav" to 1,
    "garden" to 2,
    "settings" to 3,
    "help" to 4
)

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Canal"
        val descriptionText = "Canal para notificações"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("NOTIFICATION_CHANNEL", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
private val isDarkModePreferences = booleanPreferencesKey("is_dark_mode")
val notificationPreferences = booleanPreferencesKey("notifications")

class PokemonActivity : ComponentActivity() {
    private val service = PokemonService()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this@PokemonActivity)

        setContent {
            val context = LocalContext.current
            val authViewModel = remember { AuthViewModel(AuthRepository()) }
            var hasNotificationPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else true
                )
            }

            val permissionRequest =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { result ->
                    hasNotificationPermission = result
                    if(!hasNotificationPermission){
                        Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                    }
                }

            val list: MutableList<Pokemon> = remember { emptyList<Pokemon>().toMutableStateList() }
            val isDarkModeFlow = remember {
                dataStore.data.map { preference ->
                    preference[isDarkModePreferences] ?: false
                }
            }
            val notificationFlow = remember {
                dataStore.data.map { preference ->
                    preference[notificationPreferences] ?: true
                }
            }
            val notificationEnabled by notificationFlow.collectAsState(initial = true)
            val isDarkModeEnabled by isDarkModeFlow.collectAsState(initial = false)
            val color = MaterialTheme.colorScheme.background.toArgb()
            var isLoading by remember { mutableStateOf(false) }
            var lastRoute by remember { mutableStateOf("list") }
            var route by remember { mutableStateOf("list") }

            val navController = rememberNavController()
            navController.addOnDestinationChangedListener { _, dest, _ ->
                run {
                    if ((dest.route ?: "list") != route) {
                        lastRoute = route
                        route = dest.route ?: "list"
                    }
                }
            }

            LaunchedEffect(isDarkModeEnabled) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkModeEnabled) SystemBarStyle.dark(Color.Black.toArgb()) else SystemBarStyle.light(
                        color,
                        color
                    )
                )
            }
            LaunchedEffect(true) {
                permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                Thread {
                    if (PokemonRepository(
                            PokemonDatabase.getDatabase(this@PokemonActivity).itemDao()
                        ).getCount() == 0
                    )
                        setAlarm(this@PokemonActivity, true)
                }.start()
                Toast.makeText(this@PokemonActivity, "Loading", Toast.LENGTH_SHORT).show()
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

                val scope = rememberCoroutineScope()

                    Scaffold(modifier = Modifier.fillMaxSize(),
                        bottomBar = { BottomBar(route, navController) },
                        topBar = { TopBar(route, navController, authViewModel) }
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

                        NavHost(navController, startDestination = "login") {
                            composable("login"){
                                LoginScreen(authViewModel, navController)
                            }
                            composable("register"){
                                RegisterScreen(authViewModel, navController)
                            }
                            composable("forgotpassword"){
                                ForgotPasswordScreen(authViewModel, navController)
                            }
                            composable(
                                "list",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                            ) {
                                ListScreen(
                                    list,
                                    { input -> searchQuery = input },
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
                                ListScreen(
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
                                val pokemonViewModel = remember { PokemonViewModel(this@PokemonActivity)}
                                GardenScreen(pokemonViewModel, this@PokemonActivity)
                            }
                            composable("settings",
                                enterTransition = { getInTransition(this, lastRoute, route) },
                                exitTransition = { getOutTransition(this, route, lastRoute) }
                            ) {
                                SettingsScreen(
                                    isDarkModeEnabled = isDarkModeEnabled,
                                    isNotificationsEnabled = notificationEnabled,
                                    onToggleDarkMode = {
                                        scope.launch {
                                            dataStore.edit { preference ->
                                                preference[isDarkModePreferences] = it
                                            }
                                        }
                                    },
                                    onToggleNotifications = {
                                        scope.launch {
                                            dataStore.edit { preference ->
                                                preference[notificationPreferences] = it
                                            }
                                        }
                                    },
                                    onClearFavorites = {
                                        favList.clear()
                                        Toast.makeText(
                                            context,
                                            "Favoritos limpos com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onResetPreferences = {
                                        scope.launch {
                                            dataStore.edit { preferences ->
                                                preferences[isDarkModePreferences] = false
                                            }
                                            dataStore.edit { preference ->
                                                preference[notificationPreferences] = true
                                            }
                                        }
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