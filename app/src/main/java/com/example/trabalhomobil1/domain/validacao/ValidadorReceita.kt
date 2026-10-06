package com.example.trabalhomobil1.domain.validacao

/** Tipos de erro possíveis. A tela converte cada um numa mensagem (strings.xml). */
enum class ErroValidacao { OBRIGATORIO, MUITO_LONGO, NAO_NUMERICO, FORA_DO_INTERVALO }

/** Resultado da validação do formulário: null em um campo significa "campo válido". */
data class ResultadoValidacao(
    val erroNome: ErroValidacao?,
    val erroTempo: ErroValidacao?,
    val erroIngredientes: ErroValidacao?
) {
    val valido: Boolean
        get() = erroNome == null && erroTempo == null && erroIngredientes == null
}

/**
 * Regras do formulário "Nova receita", em Kotlin puro (sem Android).
 * Por isso dá para testar com um teste unitário simples, sem emulador.
 */
object ValidadorReceita {

    const val TAMANHO_MAXIMO_NOME = 80
    const val TEMPO_MAXIMO_MINUTOS = 1440 // 24 horas
    const val TAMANHO_MAXIMO_INGREDIENTE = 60
    const val QUANTIDADE_MAXIMA_INGREDIENTES = 30

    fun validarNome(nome: String): ErroValidacao? = when {
        nome.isBlank() -> ErroValidacao.OBRIGATORIO
        nome.trim().length > TAMANHO_MAXIMO_NOME -> ErroValidacao.MUITO_LONGO
        else -> null
    }

    fun validarTempo(tempo: String): ErroValidacao? {
        if (tempo.isBlank()) return ErroValidacao.OBRIGATORIO
        val minutos = tempo.trim().toIntOrNull() ?: return ErroValidacao.NAO_NUMERICO
        return if (minutos in 1..TEMPO_MAXIMO_MINUTOS) null else ErroValidacao.FORA_DO_INTERVALO
    }

    fun validarIngredientes(ingredientes: List<String>): ErroValidacao? =
        if (ingredientes.none { it.isNotBlank() }) ErroValidacao.OBRIGATORIO else null

    fun validar(nome: String, tempo: String, ingredientes: List<String>) = ResultadoValidacao(
        erroNome = validarNome(nome),
        erroTempo = validarTempo(tempo),
        erroIngredientes = validarIngredientes(ingredientes)
    )
}
