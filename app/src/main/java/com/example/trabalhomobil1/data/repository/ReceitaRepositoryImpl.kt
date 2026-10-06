package com.example.trabalhomobil1.data.repository

import com.example.trabalhomobil1.data.local.BuscaCacheEntity
import com.example.trabalhomobil1.data.local.FavoritoEntity
import com.example.trabalhomobil1.data.local.ReceitaDao
import com.example.trabalhomobil1.data.local.ReceitaEntity
import com.example.trabalhomobil1.data.remote.MealDbApi
import com.example.trabalhomobil1.domain.model.NovaReceita
import com.example.trabalhomobil1.domain.model.Receita
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID

/**
 * Implementação que une a API (TheMealDB) e o banco local (Room).
 *
 * Política de cache (offline-first):
 * - Cada termo buscado fica registrado com a hora da busca.
 * - Se o mesmo termo foi buscado há menos de 1 hora, a resposta sai só do banco (sem rede).
 * - Senão, busca na API e salva no banco. Se a rede falhar, usa o que já estiver salvo.
 * - A tela sempre lê do banco, então receitas já vistas funcionam sem internet.
 */
class ReceitaRepositoryImpl(
    private val api: MealDbApi,
    private val dao: ReceitaDao,
    // Dispatchers.IO: pool de threads para trabalho de entrada/saída (rede e disco).
    private val io: CoroutineDispatcher = Dispatchers.IO,
    private val relogio: () -> Long = System::currentTimeMillis
) : ReceitaRepository {

    override suspend fun buscar(termo: String): List<Receita> = withContext(io) {
        val termoLimpo = termo.trim()
        val chaveCache = termoLimpo.lowercase()
        val ultimaBusca = dao.quandoBuscou(chaveCache)
        val cacheValido = ultimaBusca != null && relogio() - ultimaBusca < VALIDADE_CACHE_MS

        if (!cacheValido) {
            try {
                val agora = relogio()
                val remotas = api.buscarPorNome(termoLimpo).meals.orEmpty().map { it.paraEntity(agora) }
                dao.salvarReceitas(remotas)
                dao.registrarBusca(BuscaCacheEntity(chaveCache, agora))
            } catch (erro: IOException) {
                // Sem conexão: se não houver nada salvo, avisa a tela (estado de erro).
                if (dao.buscarPorNome(termoLimpo).isEmpty()) throw erro
            } catch (erro: HttpException) {
                // Servidor respondeu com erro (ex.: 500): mesma regra.
                if (dao.buscarPorNome(termoLimpo).isEmpty()) throw erro
            }
        }

        dao.buscarPorNome(termoLimpo).map { it.paraDominio(favorita = false) }
    }

    override fun observarReceita(id: String): Flow<Receita?> =
        dao.observarReceita(id)
            .map { linha -> linha?.receita?.paraDominio(linha.favorita) }
            .flowOn(io)

    override suspend fun carregarReceita(id: String): Boolean = withContext(io) {
        if (dao.existe(id)) return@withContext true
        if (id.startsWith(PREFIXO_ID_USUARIO)) return@withContext false // Só existe localmente.

        val remota = api.buscarPorId(id).meals?.firstOrNull() ?: return@withContext false
        dao.salvarReceita(remota.paraEntity(relogio()))
        true
    }

    override fun observarFavoritas(): Flow<List<Receita>> =
        dao.observarFavoritas()
            .map { lista -> lista.map { it.paraDominio(favorita = true) } }
            .flowOn(io)

    override fun observarCriadasPeloUsuario(): Flow<List<Receita>> =
        dao.observarCriadasPeloUsuario()
            .map { lista -> lista.map { it.paraDominio(favorita = false) } }
            .flowOn(io)

    override suspend fun definirFavorita(id: String, favorita: Boolean) = withContext(io) {
        if (favorita) {
            dao.favoritar(FavoritoEntity(receitaId = id, favoritadaEm = relogio()))
        } else {
            dao.desfavoritar(id)
        }
    }

    override suspend fun criarReceita(nova: NovaReceita): String = withContext(io) {
        val id = PREFIXO_ID_USUARIO + UUID.randomUUID()
        dao.salvarReceita(
            ReceitaEntity(
                id = id,
                nome = nova.nome,
                categoria = nova.categoria,
                modoPreparo = nova.modoPreparo,
                tempoPreparo = nova.tempoPreparo,
                imagemUrl = null,
                ingredientes = nova.ingredientes.juntarIngredientes(),
                criadaPeloUsuario = true,
                atualizadaEm = relogio()
            )
        )
        id
    }

    companion object {
        const val VALIDADE_CACHE_MS = 60 * 60 * 1000L // 1 hora
        const val PREFIXO_ID_USUARIO = "minha-"
    }
}
