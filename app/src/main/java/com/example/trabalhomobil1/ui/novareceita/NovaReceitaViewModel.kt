package com.example.trabalhomobil1.ui.novareceita

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.domain.model.NovaReceita
import com.example.trabalhomobil1.domain.validacao.ResultadoValidacao
import com.example.trabalhomobil1.domain.validacao.ValidadorReceita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado do formulário. Os campos "tocado" controlam quando mostrar o erro:
 * só depois que o usuário mexeu no campo, para não abrir a tela já cheia de vermelho.
 */
data class NovaReceitaUiState(
    val nome: String = "",
    val categoria: String = "",
    val tempo: String = "",
    val modoPreparo: String = "",
    val ingredienteDigitado: String = "",
    val ingredientes: List<String> = emptyList(),
    val nomeTocado: Boolean = false,
    val tempoTocado: Boolean = false,
    val ingredientesTocado: Boolean = false,
    val salvando: Boolean = false,
    // Preenchido após salvar: a tela mostra o Snackbar e avisa que já mostrou.
    val idSalvo: String? = null
) {
    val validacao: ResultadoValidacao
        get() = ValidadorReceita.validar(nome, tempo, ingredientes)

    val podeSalvar: Boolean
        get() = validacao.valido && !salvando
}

class NovaReceitaViewModel(private val repository: ReceitaRepository) : ViewModel() {

    // MutableStateFlow é privado: só o ViewModel altera; a tela recebe a versão somente leitura.
    private val _uiState = MutableStateFlow(NovaReceitaUiState())
    val uiState: StateFlow<NovaReceitaUiState> = _uiState.asStateFlow()

    // Os take(...) limitam o tamanho do que é digitado (validação de entrada).
    fun aoMudarNome(valor: String) = _uiState.update {
        it.copy(nome = valor.take(ValidadorReceita.TAMANHO_MAXIMO_NOME + 1), nomeTocado = true)
    }

    fun aoMudarCategoria(valor: String) = _uiState.update { it.copy(categoria = valor.take(40)) }

    fun aoMudarTempo(valor: String) = _uiState.update { it.copy(tempo = valor.take(5), tempoTocado = true) }

    fun aoMudarModoPreparo(valor: String) = _uiState.update { it.copy(modoPreparo = valor.take(2000)) }

    fun aoMudarIngrediente(valor: String) = _uiState.update {
        it.copy(ingredienteDigitado = valor.take(ValidadorReceita.TAMANHO_MAXIMO_INGREDIENTE), ingredientesTocado = true)
    }

    fun adicionarIngrediente() = _uiState.update {
        val novo = it.ingredienteDigitado.trim()
        if (novo.isEmpty() || it.ingredientes.size >= ValidadorReceita.QUANTIDADE_MAXIMA_INGREDIENTES) {
            it
        } else {
            it.copy(ingredientes = it.ingredientes + novo, ingredienteDigitado = "", ingredientesTocado = true)
        }
    }

    fun removerIngrediente(posicao: Int) = _uiState.update {
        it.copy(ingredientes = it.ingredientes.filterIndexed { i, _ -> i != posicao }, ingredientesTocado = true)
    }

    fun salvar() {
        val estado = _uiState.value
        if (!estado.podeSalvar) return // Defesa extra: o botão já fica desabilitado.

        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            val id = repository.criarReceita(
                NovaReceita(
                    nome = estado.nome.trim(),
                    categoria = estado.categoria.trim().ifBlank { null },
                    tempoPreparo = estado.tempo.trim().toInt(),
                    ingredientes = estado.ingredientes,
                    modoPreparo = estado.modoPreparo.trim().ifBlank { null }
                )
            )
            // Limpa o formulário e guarda o id para o Snackbar oferecer "Ver".
            _uiState.value = NovaReceitaUiState(idSalvo = id)
        }
    }

    fun mensagemExibida() = _uiState.update { it.copy(idSalvo = null) }

    companion object {
        fun fabrica(repository: ReceitaRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { NovaReceitaViewModel(repository) }
        }
    }
}
