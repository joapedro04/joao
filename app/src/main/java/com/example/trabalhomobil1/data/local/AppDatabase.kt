package com.example.trabalhomobil1.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Banco local do app (arquivo receitas.db), com as três tabelas. */
@Database(
    entities = [ReceitaEntity::class, FavoritoEntity::class, BuscaCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun receitaDao(): ReceitaDao
}
