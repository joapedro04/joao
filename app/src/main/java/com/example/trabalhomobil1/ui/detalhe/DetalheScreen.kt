package com.example.trabalhomobil1.ui.detalhe

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.domain.model.Receita
import com.example.trabalhomobil1.ui.components.ImagemReceita
import com.example.trabalhomobil1.ui.components.IndicadorCarregando
import com.example.trabalhomobil1.ui.components.MensagemErro
import com.example.trabalhomobil1.ui.components.MensagemEstado
import com.example.trabalhomobil1.ui.components.larguraDeLeitura

@Composable
fun DetalheScreen(viewModel: DetalheViewModel, aoVoltar: () -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    DetalheConteudo(
        estado = estado,
        aoVoltar = aoVoltar,
        aoAlternarFavorita = viewModel::alternarFavorita,
        aoTentarNovamente = viewModel::carregar,
        aoCompartilhar = { receita -> compartilhar(contexto, receita) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheConteudo(
    estado: DetalheUiState,
    aoVoltar: () -> Unit,
    aoAlternarFavorita: () -> Unit,
    aoTentarNovamente: () -> Unit,
    aoCompartilhar: (Receita) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titulo = (estado as? DetalheUiState.Sucesso)?.receita?.nome.orEmpty()
                    Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(painterResource(R.drawable.ic_voltar), contentDescription = stringResource(R.string.voltar))
                    }
                },
                actions = {
                    if (estado is DetalheUiState.Sucesso) {
                        IconButton(onClick = { aoCompartilhar(estado.receita) }) {
                            Icon(
                                painterResource(R.drawable.ic_compartilhar),
                                contentDescription = stringResource(R.string.compartilhar)
                            )
                        }
                        val favorita = estado.receita.favorita
                        // IconToggleButton: o leitor de tela anuncia "marcado/desmarcado" e a área de toque tem 48dp.
                        IconToggleButton(checked = favorita, onCheckedChange = { aoAlternarFavorita() }) {
                            Icon(
                                painterResource(if (favorita) R.drawable.ic_favorito else R.drawable.ic_favorito_borda),
                                contentDescription = stringResource(R.string.favoritar)
                            )
                        }
                    }
                }
            )
        }
    ) { espacos ->
        AnimatedContent(
            targetState = estado,
            contentKey = { it::class },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "estadoDetalhe",
            modifier = Modifier
                .padding(espacos)
                .fillMaxSize()
        ) { atual ->
            when (atual) {
                DetalheUiState.Carregando -> IndicadorCarregando()
                DetalheUiState.NaoEncontrada -> MensagemEstado(stringResource(R.string.receita_nao_encontrada))
                DetalheUiState.Erro -> MensagemErro(stringResource(R.string.detalhe_erro), aoTentarNovamente)
                is DetalheUiState.Sucesso -> DetalheReceita(atual.receita)
            }
        }
    }
}

@Composable
private fun DetalheReceita(receita: Receita) {
    // verticalScroll + nenhuma altura fixa nos textos: funciona com a maior fonte do sistema.
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .larguraDeLeitura()
    ) {
        ImagemReceita(
            url = receita.imagemUrl,
            descricao = stringResource(R.string.foto_da_receita, receita.nome),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        Column(Modifier.padding(16.dp)) {
            Text(
                receita.nome,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                receita.categoria ?: stringResource(R.string.sem_categoria),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                receita.tempoPreparo?.let { stringResource(R.string.tempo_minutos, it) }
                    ?: stringResource(R.string.tempo_nao_informado),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))
            Titulo(stringResource(R.string.titulo_ingredientes))
            if (receita.ingredientes.isEmpty()) {
                Text(stringResource(R.string.sem_ingredientes), style = MaterialTheme.typography.bodyLarge)
            } else {
                receita.ingredientes.forEach { ingrediente ->
                    Text(
                        "• $ingrediente",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Titulo(stringResource(R.string.titulo_modo_preparo))
            Text(
                receita.modoPreparo ?: stringResource(R.string.modo_preparo_nao_informado),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .semantics { heading() }
    )
}

/** Integração com o celular: abre o menu de compartilhar do Android (Intent ACTION_SEND). */
private fun compartilhar(contexto: Context, receita: Receita) {
    val texto = buildString {
        appendLine(receita.nome)
        appendLine()
        receita.ingredientes.forEach { appendLine("• $it") }
        receita.modoPreparo?.let {
            appendLine()
            append(it)
        }
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, receita.nome)
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    contexto.startActivity(Intent.createChooser(intent, contexto.getString(R.string.compartilhar_via)))
}
