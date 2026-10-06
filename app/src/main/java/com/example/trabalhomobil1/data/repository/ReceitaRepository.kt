package com.example.trabalhomobil1.data.repository

import com.example.trabalhomobil1.domain.model.NovaReceita
import com.example.trabalhomobil1.domain.model.Receita
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da camada de dados. Os ViewModels conhecem só esta interface, não sabem
 * se os dados vêm da API ou do Room. Nos testes ela é trocada por um fake.
 */
interface ReceitaRepository {

    /** Busca receitas pelo nome (API + cache local). Lança exceção se falhar e não houver cache. */
    suspend fun buscar(termo: String): List<Receita>

    /** Observa uma receita: emite de novo sempre que ela (ou o favorito dela) muda. */
    fun observarReceita(id: String): Flow<Receita?>

    /** Garante que a receita está no banco, baixando da API se preciso. Retorna false se não existir. */
    suspend fun carregarReceita(id: String): Boolean

    fun observarFavoritas(): Flow<List<Receita>>

    fun observarCriadasPeloUsuario(): Flow<List<Receita>>

    suspend fun definirFavorita(id: String, favorita: Boolean)

    /** Salva uma receita criada pelo usuário e devolve o id gerado. */
    suspend fun criarReceita(nova: NovaReceita): String
}
