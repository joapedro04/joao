package com.example.trabalhomobil1.ui.detalhe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.domain.model.Receita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

sealed interface DetalheUiState {
    data object Carregando : DetalheUiState
    data class Sucesso(val receita: Receita) : DetalheUiState
    data object NaoEncontrada : DetalheUiState
    data object Erro : DetalheUiState
}

/**
 * ViewModel do detalhe. Recebe o id que veio na rota de navegação (DetalheRota).
 * Cada entrada de Detalhe na pilha tem seu próprio ViewModel (decorator do Navigation 3).
 */
class DetalheViewModel(
    private val receitaId: String,
    private val repository: ReceitaRepository
) : ViewModel() {

    private enum class Carga { CARREGANDO, OK, NAO_ENCONTRADA, ERRO }

    private val carga = MutableStateFlow(Carga.CARREGANDO)

    // O Flow do Room emite de novo quando a receita muda (ex.: favoritou): a tela se atualiza sozinha.
    val uiState: StateFlow<DetalheUiState> =
        combine(repository.observarReceita(receitaId), carga) { receita, cargaAtual ->
            when {
                receita != null -> DetalheUiState.Sucesso(receita)
                cargaAtual == Carga.NAO_ENCONTRADA -> DetalheUiState.NaoEncontrada
                cargaAtual == Carga.ERRO -> DetalheUiState.Erro
                else -> DetalheUiState.Carregando
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalheUiState.Carregando)

    init {
        carregar()
    }

    /** Garante que a receita está no banco (baixa da API se precisar). Também é o "Tentar novamente". */
    fun carregar() {
        viewModelScope.launch {
            carga.value = Carga.CARREGANDO
            carga.value = try {
                if (repository.carregarReceita(receitaId)) Carga.OK else Carga.NAO_ENCONTRADA
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                Carga.ERRO
            }
        }
    }

    fun alternarFavorita() {
        val receita = (uiState.value as? DetalheUiState.Sucesso)?.receita ?: return
        viewModelScope.launch {
            repository.definirFavorita(receita.id, !receita.favorita)
        }
    }

    companion object {
        fun fabrica(receitaId: String, repository: ReceitaRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { DetalheViewModel(receitaId, repository) }
            }
    }
}
