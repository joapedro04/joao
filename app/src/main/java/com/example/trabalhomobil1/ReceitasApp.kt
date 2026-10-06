package com.example.trabalhomobil1

import android.app.Application
import com.example.trabalhomobil1.di.AppContainer

/** Application: é criada antes de qualquer tela e guarda o container de dependências. */
class ReceitasApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
