package com.example.myapplication.nightevents;

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable;
import androidx.navigation.NavHostController;

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        // Lista de itens com rotas, rótulos e ícones
        val items = listOf(
            Triple("home", "Home", Icons.Default.Home),
            Triple("events", "Inscritos", Icons.Default.Event),
            Triple("favorites", "Favoritos", Icons.Default.Favorite)
        )
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icon,
                        contentDescription = label
                    )
                }, // Ícone específico para cada item
                label = { Text(label) },
                selected = false, // Atualize a lógica de seleção, se necessário
                onClick = { navController.navigate(route) }
            )
        }
    }
}

