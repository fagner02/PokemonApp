package com.example.myapplication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    isDarkModeEnabled: Boolean,
    isNotificationsEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onClearFavorites: () -> Unit,
    onResetPreferences: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Modo Escuro",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDarkModeEnabled,
                onCheckedChange = {
                    onToggleDarkMode(it)
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Notificações",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isNotificationsEnabled,
                onCheckedChange = {
                    onToggleNotifications(it)
                }
            )
        }

        Button(
            onClick = {
                onClearFavorites()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpar Favoritos")
        }

        Button(
            onClick = {
                onResetPreferences()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Redefinir Preferências")
        }
    }
}
