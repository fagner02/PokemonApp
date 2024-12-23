package com.example.pokemon_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigationDefaults.windowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pokemon_app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(route: String, navController: NavHostController, scope: CoroutineScope, drawerState: DrawerState){
    val title: String =
        when (route) {
            "list"->"Início"
            "fav"-> "Favoritos"
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
            IconButton(onClick = {
                scope.launch {
                    drawerState.apply { if (isClosed) open() else close() }
                }
            }) {
                Icon(
                    Icons.Rounded.Menu, "menu",
                    Modifier.size(24.dp))
            }
            DropdownMenu(
                showDropDownMenu,
                onDismissRequest = { showDropDownMenu = false }) {
                DropdownMenuItem(
                    onClick = {},
                    text = { Text("ajuda") })
                DropdownMenuItem(
                    onClick = {},
                    text = { Text("config") })
            }
        }
    )
}