package com.example.trabalhomobil1.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * DAO: as consultas SQL do app. O Room (via KSP) gera a implementação.
 *
 * - Funções suspend: executam uma vez (inserir, buscar uma lista).
 * - Funções que retornam Flow: ficam "observando" a tabela e emitem de novo sempre que
 *   ela muda. É assim que a tela de favoritos se atualiza sozinha.
 */
@Dao
interface ReceitaDao {

    @Upsert
    suspend fun salvarReceitas(receitas: List<ReceitaEntity>)

    @Upsert
    suspend fun salvarReceita(receita: ReceitaEntity)

    @Query(
        "SELECT * FROM receitas WHERE nome LIKE '%' || :termo || '%' " +
            "ORDER BY criadaPeloUsuario DESC, nome"
    )
    suspend fun buscarPorNome(termo: String): List<ReceitaEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM receitas WHERE id = :id)")
    suspend fun existe(id: String): Boolean

    @Query(
        "SELECT receitas.*, EXISTS(SELECT 1 FROM favoritos WHERE favoritos.receitaId = receitas.id) AS favorita " +
            "FROM receitas WHERE receitas.id = :id"
    )
    fun observarReceita(id: String): Flow<ReceitaComFavorito?>

    @Query(
        "SELECT receitas.* FROM receitas " +
            "INNER JOIN favoritos ON favoritos.receitaId = receitas.id " +
            "ORDER BY favoritos.favoritadaEm DESC"
    )
    fun observarFavoritas(): Flow<List<ReceitaEntity>>

    @Query("SELECT * FROM receitas WHERE criadaPeloUsuario = 1 ORDER BY atualizadaEm DESC")
    fun observarCriadasPeloUsuario(): Flow<List<ReceitaEntity>>

    @Upsert
    suspend fun favoritar(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE receitaId = :receitaId")
    suspend fun desfavoritar(receitaId: String)

    @Upsert
    suspend fun registrarBusca(busca: BuscaCacheEntity)

    @Query("SELECT atualizadaEm FROM buscas_cache WHERE termo = :termo")
    suspend fun quandoBuscou(termo: String): Long?
}
