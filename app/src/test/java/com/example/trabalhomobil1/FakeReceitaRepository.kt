package com.example.trabalhomobil1

import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.domain.model.NovaReceita
import com.example.trabalhomobil1.domain.model.Receita
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository falso para testes: tudo em memória, sem rede e sem banco.
 * Permite simular demora, falha e verificar quais buscas foram feitas ou canceladas.
 */
class FakeReceitaRepository : ReceitaRepository {

    val termosBuscados = mutableListOf<String>()
    val buscasCanceladas = mutableListOf<String>()
    var atrasoBuscaMs = 0L
    var falharBusca = false
    var resultadoBusca: List<Receita> = listOf(receitaDeExemplo())

    private val receitas = MutableStateFlow<Map<String, Receita>>(emptyMap())
    private val favoritos = MutableStateFlow<Set<String>>(emptySet())

    override suspend fun buscar(termo: String): List<Receita> {
        termosBuscados += termo
        try {
            delay(atrasoBuscaMs)
        } catch (erro: CancellationException) {
            buscasCanceladas += termo
            throw erro
        }
        if (falharBusca) throw IOException("Sem conexão (simulado)")
        return resultadoBusca
    }

    override fun observarReceita(id: String): Flow<Receita?> =
        combine(receitas, favoritos) { mapa, favs -> mapa[id]?.copy(favorita = id in favs) }

    override suspend fun carregarReceita(id: String): Boolean = id in receitas.value

    override fun observarFavoritas(): Flow<List<Receita>> =
        combine(receitas, favoritos) { mapa, favs -> favs.mapNotNull { mapa[it]?.copy(favorita = true) } }

    override fun observarCriadasPeloUsuario(): Flow<List<Receita>> =
        receitas.map { mapa -> mapa.values.filter { it.criadaPeloUsuario } }

    override suspend fun definirFavorita(id: String, favorita: Boolean) {
        favoritos.update { if (favorita) it + id else it - id }
    }

    override suspend fun criarReceita(nova: NovaReceita): String {
        val id = "minha-${receitas.value.size + 1}"
        val receita = Receita(
            id = id,
            nome = nova.nome,
            categoria = nova.categoria,
            modoPreparo = nova.modoPreparo,
            tempoPreparo = nova.tempoPreparo,
            imagemUrl = null,
            ingredientes = nova.ingredientes,
            criadaPeloUsuario = true
        )
        receitas.update { it + (id to receita) }
        return id
    }

    fun adicionar(receita: Receita) = receitas.update { it + (receita.id to receita) }
}

fun receitaDeExemplo(id: String = "52772") = Receita(
    id = id,
    nome = "Teriyaki Chicken Casserole",
    categoria = "Chicken",
    modoPreparo = "Asse por 30 minutos.",
    tempoPreparo = null,
    imagemUrl = null,
    ingredientes = listOf("soy sauce", "water")
)
