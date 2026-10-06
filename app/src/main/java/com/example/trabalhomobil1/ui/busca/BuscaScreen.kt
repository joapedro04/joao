package com.example.trabalhomobil1.ui.busca

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.ui.components.IndicadorCarregando
import com.example.trabalhomobil1.ui.components.MensagemErro
import com.example.trabalhomobil1.ui.components.MensagemEstado
import com.example.trabalhomobil1.ui.components.ReceitaItem
import com.example.trabalhomobil1.ui.components.ReceitaItemUi
import com.example.trabalhomobil1.ui.components.larguraDeLeitura

/** Liga o ViewModel à tela: coleta o estado e repassa os eventos. */
@Composable
fun BuscaScreen(
    viewModel: BuscaViewModel,
    aoAbrirReceita: (String) -> Unit,
    aoAbrirSalvas: () -> Unit,
    aoCriarReceita: () -> Unit
) {
    // collectAsStateWithLifecycle: só coleta enquanto a tela está visível (economiza recursos).
    val termo by viewModel.termo.collectAsStateWithLifecycle()
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    BuscaConteudo(
        termo = termo,
        estado = estado,
        aoMudarTermo = viewModel::aoMudarTermo,
        aoTentarNovamente = viewModel::tentarNovamente,
        aoAbrirReceita = aoAbrirReceita,
        aoAbrirSalvas = aoAbrirSalvas,
        aoCriarReceita = aoCriarReceita
    )
}

/** Tela "burra" (sem ViewModel): recebe estado e devolve eventos. Facilita preview e testes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuscaConteudo(
    termo: String,
    estado: BuscaUiState,
    aoMudarTermo: (String) -> Unit,
    aoTentarNovamente: () -> Unit,
    aoAbrirReceita: (String) -> Unit,
    aoAbrirSalvas: () -> Unit,
    aoCriarReceita: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_busca)) },
                actions = {
                    IconButton(onClick = aoAbrirSalvas) {
                        Icon(
                            painterResource(R.drawable.ic_favorito),
                            contentDescription = stringResource(R.string.abrir_salvas)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = aoCriarReceita,
                icon = { Icon(painterResource(R.drawable.ic_adicionar), contentDescription = null) },
                text = { Text(stringResource(R.string.nova_receita)) }
            )
        }
    ) { espacos ->
        Column(
            Modifier
                .padding(espacos)
                .fillMaxSize()
                .larguraDeLeitura()
        ) {
            OutlinedTextField(
                value = termo,
                onValueChange = aoMudarTermo,
                label = { Text(stringResource(R.string.buscar_label)) },
                supportingText = { Text(stringResource(R.string.buscar_dica)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_buscar), contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Animação implícita: faz um fade só quando o TIPO de estado muda (ex.: Carregando -> Sucesso).
            AnimatedContent(
                targetState = estado,
                contentKey = { it::class },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "estadoBusca",
                modifier = Modifier.fillMaxSize()
            ) { atual ->
                when (atual) {
                    BuscaUiState.Carregando -> IndicadorCarregando()
                    BuscaUiState.Vazio -> MensagemEstado(stringResource(R.string.busca_vazia))
                    BuscaUiState.Erro -> MensagemErro(stringResource(R.string.busca_erro), aoTentarNovamente)
                    is BuscaUiState.Sucesso -> ListaReceitas(atual.receitas, aoAbrirReceita)
                }
            }
        }
    }
}

@Composable
private fun ListaReceitas(receitas: List<ReceitaItemUi>, aoAbrirReceita: (String) -> Unit) {
    LazyColumn(
        // Espaço no fim para o botão flutuante não cobrir o último item.
        contentPadding = PaddingValues(bottom = 88.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // key = id: o Compose sabe qual item é qual mesmo se a lista mudar de ordem,
        // preservando o estado de cada linha e permitindo animar a troca.
        items(receitas, key = { it.id }) { item ->
            ReceitaItem(
                item = item,
                aoClicar = { aoAbrirReceita(item.id) },
                modifier = Modifier.animateItem()
            )
        }
    }
}
