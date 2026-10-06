package com.example.trabalhomobil1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Tema do app. As telas usam só as cores do MaterialTheme (ex.: onSurface sobre surface),
 * que já são pares com contraste adequado, tanto no modo claro quanto no escuro.
 * No Android 12+ as cores seguem o papel de parede do usuário (cores dinâmicas).
 */
@Composable
fun ReceitasTheme(
    temaEscuro: Boolean = isSystemInDarkTheme(),
    conteudo: @Composable () -> Unit
) {
    val contexto = LocalContext.current
    val cores = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (temaEscuro) dynamicDarkColorScheme(contexto) else dynamicLightColorScheme(contexto)
        temaEscuro -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(colorScheme = cores, content = conteudo)
}
