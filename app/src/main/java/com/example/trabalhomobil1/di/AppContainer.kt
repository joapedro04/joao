package com.example.trabalhomobil1.di

import android.content.Context
import androidx.room.Room
import com.example.trabalhomobil1.data.local.AppDatabase
import com.example.trabalhomobil1.data.remote.MealDbApi
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.data.repository.ReceitaRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Injeção de dependências manual: este objeto cria UMA vez as peças caras
 * (Retrofit, banco Room) e entrega o repository pronto para quem precisar.
 * É criado na Application ([com.example.trabalhomobil1.ReceitasApp]), então vive enquanto o app vive.
 */
class AppContainer(context: Context) {

    // ignoreUnknownKeys: o JSON tem campos que não usamos; sem isso a conversão falharia.
    private val json = Json { ignoreUnknownKeys = true }

    private val api: MealDbApi = Retrofit.Builder()
        .baseUrl(MealDbApi.URL_BASE)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(MealDbApi::class.java)

    private val banco: AppDatabase = Room
        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "receitas.db")
        .build()

    val receitaRepository: ReceitaRepository = ReceitaRepositoryImpl(api, banco.receitaDao())
}
