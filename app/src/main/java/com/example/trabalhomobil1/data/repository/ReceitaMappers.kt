package com.example.trabalhomobil1.data.repository

import com.example.trabalhomobil1.data.local.ReceitaEntity
import com.example.trabalhomobil1.data.remote.MealDto
import com.example.trabalhomobil1.domain.model.Receita

/*
 * Mappers: convertem um modelo no outro.
 * DTO (formato da API) -> Entity (formato do banco) -> Receita (domínio, usado pelas telas).
 * Assim, se a API mudar, só o DTO e este arquivo mudam.
 */

private const val SEPARADOR_INGREDIENTES = "\n"

fun MealDto.paraEntity(agora: Long): ReceitaEntity = ReceitaEntity(
    id = id,
    nome = nome.trim(),
    categoria = categoria?.takeIf { it.isNotBlank() },
    modoPreparo = instrucoes?.takeIf { it.isNotBlank() },
    tempoPreparo = null, // O TheMealDB não informa o tempo de preparo.
    imagemUrl = imagemUrl?.takeIf { it.isNotBlank() },
    ingredientes = ingredientesComMedida().joinToString(SEPARADOR_INGREDIENTES),
    criadaPeloUsuario = false,
    atualizadaEm = agora
)

fun ReceitaEntity.paraDominio(favorita: Boolean): Receita = Receita(
    id = id,
    nome = nome,
    categoria = categoria,
    modoPreparo = modoPreparo,
    tempoPreparo = tempoPreparo,
    imagemUrl = imagemUrl,
    ingredientes = ingredientes.split(SEPARADOR_INGREDIENTES).filter { it.isNotBlank() },
    favorita = favorita,
    criadaPeloUsuario = criadaPeloUsuario
)

fun List<String>.juntarIngredientes(): String = joinToString(SEPARADOR_INGREDIENTES)
