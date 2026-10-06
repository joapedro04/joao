// Arquivo de build raiz: só declara os plugins; cada módulo aplica os que usa.
plugins {
    alias(libs.plugins.android.application) apply false
    // Não aplicamos este plugin no app (o AGP 9 já tem Kotlin embutido); declará-lo aqui
    // só fixa a versão do compilador Kotlin usada pelo projeto.
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
}
