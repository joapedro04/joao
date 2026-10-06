package com.example.trabalhomobil1.ui.salvas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.ui.components.IndicadorCarregando
import com.example.trabalhomobil1.ui.components.MensagemEstado
import com.example.trabalhomobil1.ui.components.ReceitaItem
import com.example.trabalhomobil1.ui.components.ReceitaItemUi
import com.example.trabalhomobil1.ui.components.larguraDeLeitura

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalvasScreen(
    viewModel: SalvasViewModel,
    aoAbrirReceita: (String) -> Unit,
    aoVoltar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_salvas)) },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(painterResource(R.drawable.ic_voltar), contentDescription = stringResource(R.string.voltar))
                    }
                }
            )
        }
    ) { espacos ->
        AnimatedContent(
            targetState = estado,
            contentKey = { it::class },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "estadoSalvas",
            modifier = Modifier
                .padding(espacos)
                .fillMaxSize()
        ) { atual ->
            when (atual) {
                SalvasUiState.Carregando -> IndicadorCarregando()
                SalvasUiState.Vazio -> MensagemEstado(stringResource(R.string.salvas_vazio))
                is SalvasUiState.Sucesso -> {
                    val textoSemFavoritas = stringResource(R.string.nenhuma_favorita)
                    val tituloMinhas = stringResource(R.string.secao_minhas_receitas)
                    val tituloFavoritas = stringResource(R.string.secao_favoritas)
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .larguraDeLeitura()
                    ) {
                        if (atual.criadas.isNotEmpty()) {
                            secao(tituloMinhas, "criada", atual.criadas, aoAbrirReceita)
                        }
                        if (atual.favoritas.isEmpty()) {
                            item(key = "sem-favoritas") {
                                Text(
                                    textoSemFavoritas,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            secao(tituloFavoritas, "favorita", atual.favoritas, aoAbrirReceita)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Cabeçalho + itens de uma seção. As keys levam um prefixo porque uma receita criada
 * pelo usuário pode estar nas duas seções, e a key precisa ser única na LazyColumn.
 */
private fun LazyListScope.secao(
    titulo: String,
    prefixo: String,
    itens: List<ReceitaItemUi>,
    aoAbrirReceita: (String) -> Unit
) {
    item(key = "titulo-$prefixo") {
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
                .semantics { heading() }
        )
    }
    items(itens, key = { "$prefixo-${it.id}" }) { item ->
        ReceitaItem(
            item = item,
            aoClicar = { aoAbrirReceita(item.id) },
            modifier = Modifier.animateItem()
        )
    }
}
