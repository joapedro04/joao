package com.example.trabalhomobil1.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trabalhomobil1.R

/*
 * Componentes reutilizáveis para os estados de uma tela: carregando, vazio e erro.
 */

@Composable
fun IndicadorCarregando(modifier: Modifier = Modifier) {
    val descricao = stringResource(R.string.carregando)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // O leitor de tela anuncia "Carregando" em vez de ficar em silêncio.
        CircularProgressIndicator(Modifier.semantics { contentDescription = descricao })
    }
}

/** Mensagem centralizada. Rola se a fonte estiver muito grande. */
@Composable
fun MensagemEstado(
    texto: String,
    modifier: Modifier = Modifier,
    acao: (@Composable () -> Unit)? = null
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(texto, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            if (acao != null) {
                Spacer(Modifier.height(16.dp))
                acao()
            }
        }
    }
}

@Composable
fun MensagemErro(texto: String, aoTentarNovamente: () -> Unit, modifier: Modifier = Modifier) {
    MensagemEstado(texto = texto, modifier = modifier) {
        Button(onClick = aoTentarNovamente) {
            Text(stringResource(R.string.tentar_novamente))
        }
    }
}
