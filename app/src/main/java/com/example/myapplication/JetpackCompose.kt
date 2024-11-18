package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class JetpackCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier=Modifier.fillMaxSize(), color=MaterialTheme.colorScheme.background){
                    CounterApp(
                        name = "Android"
                    )
                }
            }
        }
    }
}

@Composable
fun CounterApp(name: String, modifier: Modifier = Modifier) {
    var result by remember { mutableDoubleStateOf(0.0) }
    // Estado para armazenar o valor de entrada do usuário
    var input by remember { mutableStateOf("") }

    // Estrutura em coluna para alinhar elementos verticalmente
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Alinhamento horizontal centralizado
        modifier = Modifier
            .fillMaxSize() // Ocupa todo o espaço disponível
            .padding(16.dp), // Adiciona espaçamento nas bordas
        verticalArrangement = Arrangement.Center // Centraliza os elementos verticalmente
    ) {
        // Exibe o resultado atual
        Text(
            text = "Resultado: $result",
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )


        // Campo de entrada para número, onde o usuário digita um valor
        OutlinedTextField(
            value = input,
            onValueChange = { input = it }, // Atualiza o valor de input com o valor digitado
            label = { Text("Digite um número") },
            modifier = Modifier.fillMaxWidth() // Ocupa a largura total
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espaço entre o campo de entrada e os botões

        // Linha com os botões de incremento e decremento
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espaçamento entre os botões
            modifier = Modifier.fillMaxWidth()
        ) {
            // Botão Incrementar
            Button(
                onClick = {
                    result += input.toDoubleOrNull()
                        ?: 0.0 // Adiciona o valor digitado ao resultado
                    input = "" // Limpa o campo de entrada
                },
                modifier = Modifier.weight(1f) // O botão ocupa metade da linha
            ) {
                Text("Incrementar")
            }
            // Botão Decrementar
            Button(
                onClick = {
                    result -= input.toDoubleOrNull() ?: 0.0 // Subtrai o valor digitado do resultado
                    input = "" // Limpa o campo de entrada
                },
                modifier = Modifier.weight(1f, true) // O botão ocupa metade da linha
            ) {
                Text("Decrementar")

            }
        }


        // Linha com os botões de multiplicação e divisão
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espaçamento entre os botões
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp) // Espaçamento superior
        ) {
            // Botão Multiplicar
            Button(
                onClick = {
                    result *= input.toDoubleOrNull()
                        ?: 1.0 // Multiplica o resultado pelo valor digitado
                    input = "" // Limpa o campo de entrada
                },
                modifier = Modifier.weight(1f) // O botão ocupa metade da linha
            ) {
                Text("Multiplicar")
            }

            // Botão Dividir
            Button(
                onClick = {
                    val value = input.toDoubleOrNull() ?: 1.0
                    if (value != 0.0) { // Verifica se o valor não é zero para evitar divisão por zero
                        result /= value // Divide o resultado pelo valor digitado
                    }
                    input = "" // Limpa o campo de entrada
                },
                modifier = Modifier.weight(1f) // O botão ocupa metade da linha
            ) {
                Text("Dividir")
            }
        }
        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espaçamento entre os botões
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp) // Espaçamento superior
        ) {
            Button(onClick = {
                result = kotlin.math.sqrt(result);
            }, modifier = Modifier.weight(1f)) {
                Text("Raiz")
            }
            Button(onClick = {
                result = kotlin.math.round(result);
            }, modifier = Modifier.weight(1f)) {
                Text("Arredondar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Espaço antes do botão Limpar

        // Botão Limpar para redefinir o resultado e o campo de entrada
        Button(
            onClick = {
                result = 0.0 // Redefine o resultado para zero
                input = "" // Limpa o campo de entrada
            },
            modifier = Modifier.fillMaxWidth() // O botão ocupa a largura total
        ) {
            Text("Limpar")
        }
    }
}