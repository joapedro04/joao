package com.example.trabalhomobil1.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/*
 * Rotas do Navigation 3. Cada tela é uma chave (NavKey) que vai para a pilha (back stack).
 * @Serializable permite salvar a pilha e restaurá-la após rotação ou morte do processo.
 * Argumentos vão dentro da própria rota (ex.: o id em DetalheRota).
 */

@Serializable
data object BuscaRota : NavKey

@Serializable
data class DetalheRota(val receitaId: String) : NavKey

@Serializable
data object SalvasRota : NavKey

@Serializable
data object NovaReceitaRota : NavKey
