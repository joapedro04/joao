package com.example.trabalhomobil1.ui.busca

import androidx.lifecycle.SavedStateHandle
import com.example.trabalhomobil1.FakeReceitaRepository
import com.example.trabalhomobil1.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BuscaViewModelTest {

    @get:Rule
    val regraMain = MainDispatcherRule()

    private val repository = FakeReceitaRepository()

    private fun criarViewModel(estadoSalvo: SavedStateHandle = SavedStateHandle()) =
        BuscaViewModel(estadoSalvo, repository)

    @Test
    fun `comeca carregando e depois mostra a lista`() = runTest {
        val viewModel = criarViewModel()
        assertEquals(BuscaUiState.Carregando, viewModel.uiState.value)

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado is BuscaUiState.Sucesso)
        assertEquals("52772", (estado as BuscaUiState.Sucesso).receitas.first().id)
    }

    @Test
    fun `lista vazia vira estado Vazio`() = runTest {
        repository.resultadoBusca = emptyList()
        val viewModel = criarViewModel()

        advanceUntilIdle()

        assertEquals(BuscaUiState.Vazio, viewModel.uiState.value)
    }

    @Test
    fun `falha vira Erro e tentar novamente recupera`() = runTest {
        repository.falharBusca = true
        val viewModel = criarViewModel()
        advanceUntilIdle()
        assertEquals(BuscaUiState.Erro, viewModel.uiState.value)

        repository.falharBusca = false
        viewModel.tentarNovamente()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is BuscaUiState.Sucesso)
    }

    @Test
    fun `debounce - digitacao rapida busca so o ultimo termo`() = runTest {
        val viewModel = criarViewModel()
        advanceUntilIdle()
        repository.termosBuscados.clear()

        viewModel.aoMudarTermo("c")
        advanceTimeBy(100)
        viewModel.aoMudarTermo("ch")
        advanceTimeBy(100)
        viewModel.aoMudarTermo("chi")
        advanceUntilIdle()

        assertEquals(listOf("chi"), repository.termosBuscados)
    }

    @Test
    fun `flatMapLatest - termo novo cancela a busca anterior em andamento`() = runTest {
        repository.atrasoBuscaMs = 1_000 // cada busca "demora" 1 segundo
        val viewModel = criarViewModel()
        advanceUntilIdle()
        repository.termosBuscados.clear()

        viewModel.aoMudarTermo("frango")
        advanceTimeBy(BuscaViewModel.ESPERA_DIGITACAO_MS + 100) // busca de "frango" já começou
        viewModel.aoMudarTermo("peixe")
        advanceUntilIdle()

        assertEquals(listOf("frango", "peixe"), repository.termosBuscados)
        assertEquals(listOf("frango"), repository.buscasCanceladas)
        assertTrue(viewModel.uiState.value is BuscaUiState.Sucesso)
    }

    @Test
    fun `termo e restaurado do SavedStateHandle (rotacao ou morte do processo)`() = runTest {
        val viewModel = criarViewModel(SavedStateHandle(mapOf("termo" to "bolo")))

        advanceUntilIdle()

        assertEquals("bolo", viewModel.termo.value)
        assertEquals(listOf("bolo"), repository.termosBuscados)
    }

    @Test
    fun `termo muito longo e cortado`() = runTest {
        val viewModel = criarViewModel()

        viewModel.aoMudarTermo("x".repeat(200))

        assertEquals(BuscaViewModel.TAMANHO_MAXIMO_TERMO, viewModel.termo.value.length)
    }
}
