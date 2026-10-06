package com.example.trabalhomobil1.ui.detalhe

import com.example.trabalhomobil1.FakeReceitaRepository
import com.example.trabalhomobil1.MainDispatcherRule
import com.example.trabalhomobil1.receitaDeExemplo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalheViewModelTest {

    @get:Rule
    val regraMain = MainDispatcherRule()

    private val repository = FakeReceitaRepository()

    @Test
    fun `id inexistente mostra NaoEncontrada`() = runTest {
        val viewModel = DetalheViewModel("nao-existe", repository)
        backgroundScope.launch { viewModel.uiState.collect {} } // simula a tela observando

        advanceUntilIdle()

        assertEquals(DetalheUiState.NaoEncontrada, viewModel.uiState.value)
    }

    @Test
    fun `favoritar atualiza a tela sozinho pelo Flow`() = runTest {
        repository.adicionar(receitaDeExemplo("1"))
        val viewModel = DetalheViewModel("1", repository)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.alternarFavorita()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado is DetalheUiState.Sucesso)
        assertTrue((estado as DetalheUiState.Sucesso).receita.favorita)
    }
}
