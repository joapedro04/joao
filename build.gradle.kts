// Arquivo de build raiz: só declara os plugins; cada módulo aplica os que usa.
plugins {
    alias(libs.plugins.android.application) apply false
}
