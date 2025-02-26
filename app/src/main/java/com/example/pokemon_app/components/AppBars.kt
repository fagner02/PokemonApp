package com.example.pokemon_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.NavigationBarDefaults.windowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CatchingPokemon
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pokemon_app.R
import com.example.pokemon_app.data.AuthViewModel
import kotlinx.coroutines.CoroutineScope


@Composable
fun BottomBar(route: String, navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .windowInsetsPadding(windowInsets)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            val bottomBarButtonColors=  ButtonDefaults.textButtonColors().copy(
                disabledContentColor = ButtonDefaults.textButtonColors().contentColor,
                disabledContainerColor = ButtonDefaults.textButtonColors().containerColor,
                containerColor = ButtonDefaults.textButtonColors().disabledContainerColor,
                contentColor = ButtonDefaults.textButtonColors().disabledContentColor
            )


            TextButton(
                enabled = route != "list",
                colors = bottomBarButtonColors,
                onClick = { navController.navigate("list") },
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (route != "list") Icons.Outlined.Home else Icons.Filled.Home,
                        contentDescription = "início"
                    )
                    Text("início")
                }
            }

            TextButton(enabled = route != "fav",
                colors = bottomBarButtonColors,
                onClick = { navController.navigate("fav") }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (route != "fav") Icons.Outlined.FavoriteBorder else Icons.Filled.Favorite,
                        contentDescription = "favoritos"
                    )
                    Text("favoritos")
                }
            }

            TextButton(enabled = route != "garden",
                colors = bottomBarButtonColors,
                onClick = { navController.navigate("garden") }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (route != "garden" +
                            "") Icons.Outlined.CatchingPokemon else Icons.Filled.CatchingPokemon,
                        contentDescription = "jardim"
                    )
                    Text("jardim")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(route: String, navController: NavHostController, authViewModel: AuthViewModel, setLogout: () -> Unit){
    val title: String =
        when (route) {
            "list"->"Início"
            "fav"-> "Favoritos"
            "garden" -> "Jardim"
            "settings" -> "Configurações"
            "help" -> "Ajuda"
            "logout" -> "Logout"
            else -> navController.currentBackStackEntry?.arguments?.getString("pokemon")
                ?:
                ""
        }
    var showDropDownMenu by remember { mutableStateOf(false) }
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.poke),
                contentDescription = "icone",
                modifier = Modifier
                    .size(42.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(title)
        }
    },
        actions = {
            MinimalDropdownMenu(navController, authViewModel, setLogout)
        }
    )
}

@Composable
fun MinimalDropdownMenu(navController: NavHostController, authViewModel: AuthViewModel, setLogout: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Configurações") },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = {
                    expanded = false
                    navController.navigate("settings")
                }
            )

            HorizontalDivider(thickness = 2.dp, color = Color.LightGray)

            DropdownMenuItem(
                text = { Text("Ajuda") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null)},
                onClick = {
                    expanded = false
                    navController.navigate("help")
                }
            )

            HorizontalDivider(thickness = 2.dp, color = Color.LightGray)

            DropdownMenuItem(
                text = { Text("Logout") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                onClick = {
                    expanded = false
                    authViewModel.logout()
                    setLogout()
                    navController.navigate("login")
                }
            )
        }
    }
}