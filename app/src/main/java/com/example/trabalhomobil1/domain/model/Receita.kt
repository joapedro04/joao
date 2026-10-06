package com.example.trabalhomobil1.domain.model

/**
 * Modelo imutável (todos os campos são val) de uma receita.
 * Campos que podem não existir são anuláveis (String? / Int?) e a tela
 * decide qual texto padrão mostrar quando vierem nulos.
 */
data class Receita(
    val id: String,
    val nome: String,
    val categoria: String?,
    val modoPreparo: String?,
    val tempoPreparo: Int?,
    val imagemUrl: String?,
    val ingredientes: List<String> = emptyList(),
    val favorita: Boolean = false
)
