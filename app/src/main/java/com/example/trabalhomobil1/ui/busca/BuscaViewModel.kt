package com.example.trabalhomobil1.ui.busca

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.ui.components.ReceitaItemUi
import com.example.trabalhomobil1.ui.components.paraItemUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

/** Os quatro estados visuais da busca. sealed: o "when" na tela é obrigado a tratar todos. */
sealed interface BuscaUiState {
    data object Carregando : BuscaUiState
    data class Sucesso(val receitas: List<ReceitaItemUi>) : BuscaUiState
    data object Vazio : BuscaUiState
    data object Erro : BuscaUiState
}

/**
 * Fluxo unidirecional: a tela chama [aoMudarTermo]/[tentarNovamente] (eventos)
 * e só observa [termo] e [uiState] (estado). Ela nunca altera o estado diretamente.
 */
class BuscaViewModel(
    private val estadoSalvo: SavedStateHandle,
    private val repository: ReceitaRepository
) : ViewModel() {

    // O termo fica no SavedStateHandle: sobrevive à rotação e até ao Android matar o processo.
    val termo: StateFlow<String> = estadoSalvo.getStateFlow(CHAVE_TERMO, "")

    // Incrementar este contador faz a busca rodar de novo (botão "Tentar novamente").
    private val tentativas = MutableStateFlow(0)

    /**
     * Como a busca é cancelada:
     * 1. debounce: só deixa passar o termo depois de 500 ms sem digitação.
     * 2. flatMapLatest: quando chega um termo novo, a coroutine da busca anterior é CANCELADA
     *    automaticamente (o Retrofit aborta a requisição HTTP em andamento) e começa a nova.
     * 3. stateIn(viewModelScope): tudo roda no escopo do ViewModel; quando a tela sai da
     *    pilha de navegação o ViewModel é destruído e o escopo cancela o que estiver rodando.
     * Os dados ficam no ViewModel, que sobrevive à rotação: girar a tela não refaz a busca.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BuscaUiState> =
        combine(termo.debounce(ESPERA_DIGITACAO_MS), tentativas) { termoAtual, _ -> termoAtual }
            .flatMapLatest { termoAtual -> buscar(termoAtual) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, BuscaUiState.Carregando)

    fun aoMudarTermo(novoTermo: String) {
        // Validação de entrada: limita o tamanho do texto enviado para a API.
        estadoSalvo[CHAVE_TERMO] = novoTermo.take(TAMANHO_MAXIMO_TERMO)
    }

    fun tentarNovamente() {
        tentativas.update { it + 1 }
    }

    private fun buscar(termo: String): Flow<BuscaUiState> = flow {
        emit(BuscaUiState.Carregando)
        val receitas = try {
            repository.buscar(termo)
        } catch (erro: CancellationException) {
            throw erro // Cancelamento não é erro: precisa continuar subindo.
        } catch (erro: Exception) {
            null
        }
        emit(
            when {
                receitas == null -> BuscaUiState.Erro
                receitas.isEmpty() -> BuscaUiState.Vazio
                else -> BuscaUiState.Sucesso(receitas.map { it.paraItemUi() })
            }
        )
    }

    companion object {
        private const val CHAVE_TERMO = "termo"
        const val ESPERA_DIGITACAO_MS = 500L
        const val TAMANHO_MAXIMO_TERMO = 50

        /** Ensina o Android a criar este ViewModel, entregando o SavedStateHandle e o repository. */
        fun fabrica(repository: ReceitaRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { BuscaViewModel(createSavedStateHandle(), repository) }
        }
    }
}
