package com.example.trabalhomobil1

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.trabalhomobil1.ui.busca.BuscaConteudo
import com.example.trabalhomobil1.ui.busca.BuscaUiState
import com.example.trabalhomobil1.ui.components.ReceitaItemUi
import com.example.trabalhomobil1.ui.theme.ReceitasTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Testa a tela de busca "burra" (BuscaConteudo) em cada estado, sem ViewModel. */
@RunWith(AndroidJUnit4::class)
class BuscaTelaTest {

    @get:Rule
    val regraCompose = createComposeRule()

    private fun mostrar(estado: BuscaUiState, aoTentar: () -> Unit = {}, aoAbrir: (String) -> Unit = {}) {
        regraCompose.setContent {
            ReceitasTheme {
                BuscaConteudo(
                    termo = "",
                    estado = estado,
                    aoMudarTermo = {},
                    aoTentarNovamente = aoTentar,
                    aoAbrirReceita = aoAbrir,
                    aoAbrirSalvas = {},
                    aoCriarReceita = {}
                )
            }
        }
    }

    @Test
    fun erroMostraBotaoTentarNovamente() {
        var tentou = false
        mostrar(BuscaUiState.Erro, aoTentar = { tentou = true })

        regraCompose.onNodeWithText("Tentar novamente").performClick()

        assertTrue(tentou)
    }

    @Test
    fun vazioMostraMensagem() {
        mostrar(BuscaUiState.Vazio)

        regraCompose.onNodeWithText("Nenhuma receita encontrada para essa busca.").assertIsDisplayed()
    }

    @Test
    fun cliqueNoItemAbreAReceitaCerta() {
        var idAberto: String? = null
        val item = ReceitaItemUi("52772", "Teriyaki Chicken", null, null, criadaPeloUsuario = false)
        mostrar(BuscaUiState.Sucesso(listOf(item)), aoAbrir = { idAberto = it })

        regraCompose.onNodeWithText("Sem categoria").assertIsDisplayed() // categoria nula -> texto padrão
        regraCompose.onNodeWithText("Teriyaki Chicken").performClick()

        assertEquals("52772", idAberto)
    }
}
