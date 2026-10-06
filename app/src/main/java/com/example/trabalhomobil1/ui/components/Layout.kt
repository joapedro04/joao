package com.example.trabalhomobil1.ui.components

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Em telas largas (tablet, celular deitado) o conteúdo fica centralizado com largura
 * máxima de leitura, em vez de esticar linhas de texto de ponta a ponta.
 */
fun Modifier.larguraDeLeitura(): Modifier = this
    .wrapContentWidth()
    .widthIn(max = 840.dp)
