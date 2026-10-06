package com.example.trabalhomobil1.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Endpoints do TheMealDB usados pelo app. O Retrofit gera a implementação desta interface.
 * As funções são suspend: a coroutine fica suspensa (sem travar a thread) enquanto espera a rede.
 */
interface MealDbApi {

    /** Busca receitas pelo nome. Ex.: search.php?s=chicken */
    @GET("search.php")
    suspend fun buscarPorNome(@Query("s") termo: String): MealsResponseDto

    /** Busca uma receita pelo id. Ex.: lookup.php?i=52772 */
    @GET("lookup.php")
    suspend fun buscarPorId(@Query("i") id: String): MealsResponseDto

    companion object {
        /**
         * O "1" no caminho é a chave PÚBLICA de teste documentada pelo TheMealDB
         * (https://www.themealdb.com/api.php). Não é um segredo, por isso pode ficar no código.
         */
        const val URL_BASE = "https://www.themealdb.com/api/json/v1/1/"
    }
}
