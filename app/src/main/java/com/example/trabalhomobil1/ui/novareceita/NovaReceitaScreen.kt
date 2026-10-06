package com.example.trabalhomobil1.ui.novareceita

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.domain.validacao.ErroValidacao
import com.example.trabalhomobil1.domain.validacao.ValidadorReceita
import com.example.trabalhomobil1.ui.components.larguraDeLeitura

@Composable
fun NovaReceitaScreen(
    viewModel: NovaReceitaViewModel,
    aoAbrirReceita: (String) -> Unit,
    aoVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val mensagem = stringResource(R.string.receita_salva)
    val acao = stringResource(R.string.ver)

    // Efeito colateral: roda quando idSalvo muda. Mostra o Snackbar de sucesso.
    LaunchedEffect(estado.idSalvo) {
        val id = estado.idSalvo ?: return@LaunchedEffect
        val resultado = snackbar.showSnackbar(mensagem, actionLabel = acao, duration = SnackbarDuration.Long)
        if (resultado == SnackbarResult.ActionPerformed) aoAbrirReceita(id)
        viewModel.mensagemExibida()
    }

    NovaReceitaConteudo(
        estado = estado,
        snackbar = snackbar,
        aoMudarNome = viewModel::aoMudarNome,
        aoMudarCategoria = viewModel::aoMudarCategoria,
        aoMudarTempo = viewModel::aoMudarTempo,
        aoMudarIngrediente = viewModel::aoMudarIngrediente,
        aoAdicionarIngrediente = viewModel::adicionarIngrediente,
        aoRemoverIngrediente = viewModel::removerIngrediente,
        aoMudarModoPreparo = viewModel::aoMudarModoPreparo,
        aoSalvar = viewModel::salvar,
        aoVoltar = aoVoltar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaReceitaConteudo(
    estado: NovaReceitaUiState,
    snackbar: SnackbarHostState,
    aoMudarNome: (String) -> Unit,
    aoMudarCategoria: (String) -> Unit,
    aoMudarTempo: (String) -> Unit,
    aoMudarIngrediente: (String) -> Unit,
    aoAdicionarIngrediente: () -> Unit,
    aoRemoverIngrediente: (Int) -> Unit,
    aoMudarModoPreparo: (String) -> Unit,
    aoSalvar: () -> Unit,
    aoVoltar: () -> Unit
) {
    val validacao = estado.validacao
    // Só mostra o erro de um campo depois que o usuário mexeu nele.
    val erroNome = validacao.erroNome.takeIf { estado.nomeTocado }
    val erroTempo = validacao.erroTempo.takeIf { estado.tempoTocado }
    val erroIngredientes = validacao.erroIngredientes.takeIf { estado.ingredientesTocado }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nova_receita)) },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(painterResource(R.drawable.ic_voltar), contentDescription = stringResource(R.string.voltar))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { espacos ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(espacos)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .larguraDeLeitura()
                .padding(16.dp)
                .animateContentSize() // anima quando ingredientes entram/saem
        ) {
            OutlinedTextField(
                value = estado.nome,
                onValueChange = aoMudarNome,
                label = { Text(stringResource(R.string.campo_nome)) },
                isError = erroNome != null,
                supportingText = {
                    Text(erroNome?.let { textoDoErro(it) } ?: stringResource(R.string.ajuda_obrigatorio))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.categoria,
                onValueChange = aoMudarCategoria,
                label = { Text(stringResource(R.string.campo_categoria)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.tempo,
                onValueChange = aoMudarTempo,
                label = { Text(stringResource(R.string.campo_tempo)) },
                isError = erroTempo != null,
                supportingText = {
                    Text(erroTempo?.let { textoDoErro(it) } ?: stringResource(R.string.ajuda_tempo))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                stringResource(R.string.titulo_ingredientes) + " *",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )

            estado.ingredientes.forEachIndexed { posicao, ingrediente ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("• $ingrediente", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    IconButton(onClick = { aoRemoverIngrediente(posicao) }) {
                        Icon(
                            painterResource(R.drawable.ic_remover),
                            contentDescription = stringResource(R.string.remover_ingrediente, ingrediente)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = estado.ingredienteDigitado,
                onValueChange = aoMudarIngrediente,
                label = { Text(stringResource(R.string.campo_ingrediente)) },
                isError = erroIngredientes != null,
                supportingText = {
                    Text(
                        if (erroIngredientes != null) stringResource(R.string.erro_ingredientes)
                        else stringResource(R.string.ajuda_ingredientes)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { aoAdicionarIngrediente() }),
                modifier = Modifier.fillMaxWidth()
            )
            FilledTonalButton(
                onClick = aoAdicionarIngrediente,
                enabled = estado.ingredienteDigitado.isNotBlank()
            ) {
                Text(stringResource(R.string.adicionar_ingrediente))
            }

            OutlinedTextField(
                value = estado.modoPreparo,
                onValueChange = aoMudarModoPreparo,
                label = { Text(stringResource(R.string.campo_modo_preparo)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Desabilitado enquanto o formulário for inválido.
            Button(
                onClick = aoSalvar,
                enabled = estado.podeSalvar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.salvar_receita))
            }
            AnimatedVisibility(visible = !validacao.valido) {
                Text(
                    stringResource(R.string.formulario_incompleto),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun textoDoErro(erro: ErroValidacao): String = when (erro) {
    ErroValidacao.OBRIGATORIO -> stringResource(R.string.erro_obrigatorio)
    ErroValidacao.MUITO_LONGO -> stringResource(R.string.erro_muito_longo)
    ErroValidacao.NAO_NUMERICO -> stringResource(R.string.erro_tempo_numerico)
    ErroValidacao.FORA_DO_INTERVALO ->
        stringResource(R.string.erro_tempo_intervalo, ValidadorReceita.TEMPO_MAXIMO_MINUTOS)
}
