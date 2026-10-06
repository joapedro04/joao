package com.example.trabalhomobil1.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.domain.model.Receita

/**
 * Modelo de UI: só o que uma linha da lista precisa mostrar.
 * Separado do modelo de domínio para a tela não depender de campos que não usa.
 */
data class ReceitaItemUi(
    val id: String,
    val nome: String,
    val categoria: String?,
    val imagemUrl: String?,
    val criadaPeloUsuario: Boolean
)

fun Receita.paraItemUi(): ReceitaItemUi = ReceitaItemUi(
    id = id,
    nome = nome,
    categoria = categoria,
    imagemUrl = imagemUrl,
    criadaPeloUsuario = criadaPeloUsuario
)

/** Linha reutilizável de receita (usada na Busca e nas Salvas). */
@Composable
fun ReceitaItem(
    item: ReceitaItemUi,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoria = item.categoria ?: stringResource(R.string.sem_categoria)
    val subtitulo = if (item.criadaPeloUsuario) {
        stringResource(R.string.minha_receita_categoria, categoria)
    } else {
        categoria
    }
    // ListItem já respeita a altura mínima de toque e cresce com a fonte do sistema.
    ListItem(
        headlineContent = { Text(item.nome) },
        supportingContent = { Text(subtitulo) },
        leadingContent = {
            // Decorativa (descrição nula): o nome da receita já está no texto ao lado.
            ImagemReceita(
                url = item.imagemUrl,
                descricao = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        },
        modifier = modifier.clickable(onClick = aoClicar)
    )
}

/** Foto da receita baixada pelo Coil, com ícone padrão enquanto carrega ou se não houver URL. */
@Composable
fun ImagemReceita(url: String?, descricao: String?, modifier: Modifier = Modifier) {
    val padrao = painterResource(R.drawable.ic_receita_placeholder)
    AsyncImage(
        model = url,
        contentDescription = descricao,
        placeholder = padrao,
        error = padrao,
        fallback = padrao,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
