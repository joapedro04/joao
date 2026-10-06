package com.example.trabalhomobil1.ui.salvas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.ui.components.ReceitaItemUi
import com.example.trabalhomobil1.ui.components.paraItemUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

sealed interface SalvasUiState {
    data object Carregando : SalvasUiState
    data object Vazio : SalvasUiState
    data class Sucesso(
        val criadas: List<ReceitaItemUi>,
        val favoritas: List<ReceitaItemUi>
    ) : SalvasUiState
}

/** Junta dois Flows do Room: qualquer mudança em favoritos ou receitas criadas atualiza a tela. */
class SalvasViewModel(repository: ReceitaRepository) : ViewModel() {

    val uiState: StateFlow<SalvasUiState> =
        combine(repository.observarCriadasPeloUsuario(), repository.observarFavoritas()) { criadas, favoritas ->
            if (criadas.isEmpty() && favoritas.isEmpty()) {
                SalvasUiState.Vazio
            } else {
                SalvasUiState.Sucesso(
                    criadas = criadas.map { it.paraItemUi() },
                    favoritas = favoritas.map { it.paraItemUi() }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SalvasUiState.Carregando)

    companion object {
        fun fabrica(repository: ReceitaRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { SalvasViewModel(repository) }
        }
    }
}
