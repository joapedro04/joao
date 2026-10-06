package com.example.trabalhomobil1.ui.novareceita

import com.example.trabalhomobil1.FakeReceitaRepository
import com.example.trabalhomobil1.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NovaReceitaViewModelTest {

    @get:Rule
    val regraMain = MainDispatcherRule()

    private val repository = FakeReceitaRepository()
    private val viewModel = NovaReceitaViewModel(repository)

    @Test
    fun `formulario vazio nao pode salvar`() {
        assertFalse(viewModel.uiState.value.podeSalvar)
    }

    @Test
    fun `ingrediente em branco nao e adicionado`() {
        viewModel.aoMudarIngrediente("   ")
        viewModel.adicionarIngrediente()

        assertTrue(viewModel.uiState.value.ingredientes.isEmpty())
    }

    @Test
    fun `formulario valido salva no repository e limpa os campos`() = runTest {
        viewModel.aoMudarNome("Bolo de cenoura")
        viewModel.aoMudarTempo("40")
        viewModel.aoMudarIngrediente("Cenoura")
        viewModel.adicionarIngrediente()
        assertTrue(viewModel.uiState.value.podeSalvar)

        viewModel.salvar()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertNotNull(estado.idSalvo)
        assertEquals("", estado.nome)
        val criadas = repository.observarCriadasPeloUsuario().first()
        assertEquals("Bolo de cenoura", criadas.single().nome)
        assertEquals(40, criadas.single().tempoPreparo)
    }

    @Test
    fun `remover o unico ingrediente torna o formulario invalido`() {
        viewModel.aoMudarNome("Bolo")
        viewModel.aoMudarTempo("40")
        viewModel.aoMudarIngrediente("Ovo")
        viewModel.adicionarIngrediente()

        viewModel.removerIngrediente(0)

        assertFalse(viewModel.uiState.value.podeSalvar)
    }
}
