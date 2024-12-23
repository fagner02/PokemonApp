package com.example.myapplication.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HelpAndSupportScreen(onSendSupportMessage: (String) -> Unit, modifier: Modifier) {
    val faqs = listOf(
        "O que são os Pokémon favoritos?" to "São os Pokémon que você marcou com o ícone de coração para acessá-los mais rapidamente.",
        "Como alterar o modo claro/escuro?" to "Acesse a tela 'Configurações' e alterne a opção de modo escuro.",
        "Como enviar uma mensagem para o suporte?" to "Use o campo de mensagem abaixo nesta tela para entrar em contato com o suporte.",
        "O que fazer se o aplicativo não carregar?" to "Verifique sua conexão com a internet ou reinicie o aplicativo.",
        "Como adiciono um Pokémon aos favoritos?" to "Clique no ícone de coração ao lado do Pokémon para marcá-lo como favorito.",
        "É possível buscar um Pokémon específico?" to "Use a barra de pesquisa na tela principal para encontrar o Pokémon desejado.",
        "Como alterar as configurações de notificação?" to "Vá para a tela 'Configurações' e ative ou desative as notificações conforme sua preferência."
    )
    var supportMessage by remember { mutableStateOf("") }
    val state = rememberScrollState()
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Perguntas Frequentes (FAQs)", style = MaterialTheme.typography.titleMedium)

        faqs.forEach { (question, answer)  ->
            ExpandableCard(
                question
            ){
                Text(answer)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Envie uma mensagem para o nosso suporte", style = MaterialTheme.typography.bodySmall)

        TextField(
            value = supportMessage,
            onValueChange = {  supportMessage = it },
            label = { Text("Mensagem") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (supportMessage.isNotBlank()) {
                    onSendSupportMessage(supportMessage)
                    supportMessage = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Enviar")
        }
    }
}