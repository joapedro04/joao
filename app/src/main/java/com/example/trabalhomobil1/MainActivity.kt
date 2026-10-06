package com.example.trabalhomobil1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.trabalhomobil1.ui.navigation.AppNavegacao
import com.example.trabalhomobil1.ui.theme.ReceitasTheme

/** Única Activity da Etapa 2: todas as telas são composables trocadas pelo Navigation 3. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as ReceitasApp).container.receitaRepository
        setContent {
            ReceitasTheme {
                AppNavegacao(repository)
            }
        }
    }
}
