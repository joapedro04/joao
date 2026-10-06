package com.example.trabalhomobil1.data.repository

import com.example.trabalhomobil1.data.local.BuscaCacheEntity
import com.example.trabalhomobil1.data.local.FavoritoEntity
import com.example.trabalhomobil1.data.local.ReceitaComFavorito
import com.example.trabalhomobil1.data.local.ReceitaDao
import com.example.trabalhomobil1.data.local.ReceitaEntity
import com.example.trabalhomobil1.data.remote.MealDbApi
import com.example.trabalhomobil1.data.remote.MealDto
import com.example.trabalhomobil1.data.remote.MealsResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/** Testa a política de cache (1 hora) e o funcionamento offline do repository. */
class ReceitaRepositoryImplTest {

    private val api = FakeApi()
    private val dao = FakeDao()
    private var agora = 0L

    private fun criarRepository(dispatcher: TestDispatcher) =
        ReceitaRepositoryImpl(api, dao, io = dispatcher, relogio = { agora })

    @Test
    fun `mesmo termo dentro de 1 hora usa o cache e nao chama a API`() = runTest {
        val repository = criarRepository(StandardTestDispatcher(testScheduler))

        repository.buscar("chicken")
        agora += 30 * 60 * 1000L // 30 minutos depois
        val resultado = repository.buscar("Chicken ")

        assertEquals(1, api.chamadas)
        assertEquals("Teriyaki Chicken", resultado.single().nome)
    }

    @Test
    fun `depois de 1 hora o cache expira e a API e chamada de novo`() = runTest {
        val repository = criarRepository(StandardTestDispatcher(testScheduler))

        repository.buscar("chicken")
        agora += ReceitaRepositoryImpl.VALIDADE_CACHE_MS + 1
        repository.buscar("chicken")

        assertEquals(2, api.chamadas)
    }

    @Test
    fun `sem internet usa as receitas ja salvas`() = runTest {
        val repository = criarRepository(StandardTestDispatcher(testScheduler))
        repository.buscar("chicken")
        agora += ReceitaRepositoryImpl.VALIDADE_CACHE_MS + 1

        api.semInternet = true
        val resultado = repository.buscar("chicken")

        assertEquals("Teriyaki Chicken", resultado.single().nome)
    }

    @Test(expected = IOException::class)
    fun `sem internet e sem cache lanca erro para a tela mostrar o estado Erro`() = runTest {
        val repository = criarRepository(StandardTestDispatcher(testScheduler))
        api.semInternet = true

        repository.buscar("chicken")
    }

    @Test
    fun `ingredientes da API sao convertidos com a medida`() = runTest {
        val repository = criarRepository(StandardTestDispatcher(testScheduler))

        val receita = repository.buscar("chicken").single()

        assertEquals(listOf("soy sauce (3/4 cup)", "water"), receita.ingredientes)
        assertTrue(receita.tempoPreparo == null) // a API não informa tempo
    }

    private class FakeApi : MealDbApi {
        var chamadas = 0
        var semInternet = false

        override suspend fun buscarPorNome(termo: String): MealsResponseDto {
            chamadas++
            if (semInternet) throw IOException("sem internet")
            return MealsResponseDto(
                listOf(
                    MealDto(
                        id = "52772",
                        nome = "Teriyaki Chicken",
                        ingrediente1 = "soy sauce",
                        medida1 = "3/4 cup",
                        ingrediente2 = "water",
                        medida2 = " ",
                        ingrediente3 = ""
                    )
                )
            )
        }

        override suspend fun buscarPorId(id: String) = MealsResponseDto(null)
    }

    /** DAO em memória que imita o comportamento das consultas do Room. */
    private class FakeDao : ReceitaDao {
        private val receitas = MutableStateFlow<Map<String, ReceitaEntity>>(emptyMap())
        private val favoritos = MutableStateFlow<Map<String, FavoritoEntity>>(emptyMap())
        private val buscas = mutableMapOf<String, Long>()

        override suspend fun salvarReceitas(receitas: List<ReceitaEntity>) =
            this.receitas.update { atual -> atual + receitas.associateBy { it.id } }

        override suspend fun salvarReceita(receita: ReceitaEntity) = salvarReceitas(listOf(receita))

        override suspend fun buscarPorNome(termo: String) =
            receitas.value.values.filter { it.nome.contains(termo, ignoreCase = true) }

        override suspend fun existe(id: String) = id in receitas.value

        override fun observarReceita(id: String): Flow<ReceitaComFavorito?> =
            combine(receitas, favoritos) { r, f -> r[id]?.let { ReceitaComFavorito(it, id in f) } }

        override fun observarFavoritas(): Flow<List<ReceitaEntity>> =
            combine(receitas, favoritos) { r, f -> f.keys.mapNotNull { r[it] } }

        override fun observarCriadasPeloUsuario(): Flow<List<ReceitaEntity>> =
            receitas.map { r -> r.values.filter { it.criadaPeloUsuario } }

        override suspend fun favoritar(favorito: FavoritoEntity) =
            favoritos.update { it + (favorito.receitaId to favorito) }

        override suspend fun desfavoritar(receitaId: String) = favoritos.update { it - receitaId }

        override suspend fun registrarBusca(busca: BuscaCacheEntity) {
            buscas[busca.termo] = busca.atualizadaEm
        }

        override suspend fun quandoBuscou(termo: String): Long? = buscas[termo]
    }
}
