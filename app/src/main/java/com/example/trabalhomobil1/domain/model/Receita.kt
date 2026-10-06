package com.example.trabalhomobil1.domain.model

/**
 * Modelo imutável (todos os campos são val) de uma receita, usado pelas regras e pelas telas.
 * Não depende de Room nem de Retrofit. Campos que podem não existir são anuláveis
 * (String? / Int?) e a tela decide qual texto padrão mostrar quando vierem nulos.
 */
data class Receita(
    val id: String,
    val nome: String,
    val categoria: String?,
    val modoPreparo: String?,
    val tempoPreparo: Int?,
    val imagemUrl: String?,
    val ingredientes: List<String> = emptyList(),
    val favorita: Boolean = false,
    val criadaPeloUsuario: Boolean = false
)

/** Dados digitados no formulário "Nova receita", já validados. */
data class NovaReceita(
    val nome: String,
    val categoria: String?,
    val tempoPreparo: Int,
    val ingredientes: List<String>,
    val modoPreparo: String?
)
