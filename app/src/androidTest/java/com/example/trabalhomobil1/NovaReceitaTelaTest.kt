package com.example.trabalhomobil1

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.domain.model.NovaReceita
import com.example.trabalhomobil1.domain.model.Receita
import com.example.trabalhomobil1.ui.novareceita.NovaReceitaScreen
import com.example.trabalhomobil1.ui.novareceita.NovaReceitaViewModel
import com.example.trabalhomobil1.ui.theme.ReceitasTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Teste de UI do fluxo principal do formulário: validação, botão desabilitado e Snackbar. */
@RunWith(AndroidJUnit4::class)
class NovaReceitaTelaTest {

    @get:Rule
    val regraCompose = createComposeRule()

    private val repository = RepositoryQueSoSalva()

    @Test
    fun preencherFormularioHabilitaSalvarEMostraSnackbar() {
        // Criado fora do composable: dentro dele, cada recomposição criaria um ViewModel novo.
        val viewModel = NovaReceitaViewModel(repository)
        regraCompose.setContent {
            ReceitasTheme {
                NovaReceitaScreen(viewModel, aoAbrirReceita = {}, aoVoltar = {})
            }
        }

        regraCompose.onNodeWithText("Salvar receita").assertIsNotEnabled()

        regraCompose.onNodeWithText("Nome *").performTextInput("Bolo de cenoura")
        regraCompose.onNodeWithText("Tempo de preparo em minutos *").performTextInput("40")
        regraCompose.onNodeWithText("Salvar receita").assertIsNotEnabled() // ainda falta ingrediente

        regraCompose.onNodeWithText("Ingrediente").performTextInput("Cenoura")
        regraCompose.onNodeWithText("Adicionar ingrediente").performClick()
        regraCompose.onNodeWithText("• Cenoura").assertIsDisplayed()

        regraCompose.onNodeWithText("Salvar receita").assertIsEnabled().performClick()

        regraCompose.onNodeWithText("Receita salva!").assertIsDisplayed()
        assertEquals("Bolo de cenoura", repository.salvas.single().nome)
    }

    @Test
    fun tempoInvalidoMostraMensagemDeErro() {
        // Criado fora do composable: dentro dele, cada recomposição criaria um ViewModel novo.
        val viewModel = NovaReceitaViewModel(repository)
        regraCompose.setContent {
            ReceitasTheme {
                NovaReceitaScreen(viewModel, aoAbrirReceita = {}, aoVoltar = {})
            }
        }

        regraCompose.onNodeWithText("Tempo de preparo em minutos *").performTextInput("0")

        regraCompose.onNodeWithText("Digite um valor entre 1 e 1440").assertIsDisplayed()
    }

    /** Fake mínimo: só guarda o que foi salvo. */
    private class RepositoryQueSoSalva : ReceitaRepository {
        val salvas = mutableListOf<NovaReceita>()
        override suspend fun criarReceita(nova: NovaReceita): String {
            salvas += nova
            return "minha-1"
        }
        override suspend fun buscar(termo: String): List<Receita> = emptyList()
        override fun observarReceita(id: String): Flow<Receita?> = flowOf(null)
        override suspend fun carregarReceita(id: String) = false
        override fun observarFavoritas(): Flow<List<Receita>> = flowOf(emptyList())
        override fun observarCriadasPeloUsuario(): Flow<List<Receita>> = flowOf(emptyList())
        override suspend fun definirFavorita(id: String, favorita: Boolean) = Unit
    }
}
