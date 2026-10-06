package com.example.trabalhomobil1.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabela "receitas": guarda tanto as receitas vindas da API (cache) quanto as criadas pelo usuário.
 */
@Entity(tableName = "receitas")
data class ReceitaEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val categoria: String?,
    val modoPreparo: String?,
    val tempoPreparo: Int?,
    val imagemUrl: String?,
    // Um ingrediente por linha. Evita criar uma tabela só para ingredientes.
    val ingredientes: String,
    val criadaPeloUsuario: Boolean,
    // Momento (em milissegundos) em que a linha foi salva/atualizada.
    val atualizadaEm: Long
)

/** Tabela "favoritos": só o id da receita e quando ela foi favoritada. */
@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey val receitaId: String,
    val favoritadaEm: Long
)

/**
 * Tabela "buscas_cache": lembra quando cada termo foi buscado na API.
 * É o que permite a política de expiração do cache (1 hora).
 */
@Entity(tableName = "buscas_cache")
data class BuscaCacheEntity(
    @PrimaryKey val termo: String,
    val atualizadaEm: Long
)

/** Resultado de consulta: a receita + se ela está nos favoritos. */
data class ReceitaComFavorito(
    @Embedded val receita: ReceitaEntity,
    val favorita: Boolean
)
