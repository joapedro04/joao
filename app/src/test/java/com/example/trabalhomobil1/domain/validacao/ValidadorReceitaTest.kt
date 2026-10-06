package com.example.trabalhomobil1.domain.validacao

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorReceitaTest {

    @Test
    fun `nome vazio ou so com espacos e obrigatorio`() {
        assertEquals(ErroValidacao.OBRIGATORIO, ValidadorReceita.validarNome(""))
        assertEquals(ErroValidacao.OBRIGATORIO, ValidadorReceita.validarNome("   "))
    }

    @Test
    fun `nome acima do limite e muito longo`() {
        val nomeGrande = "a".repeat(ValidadorReceita.TAMANHO_MAXIMO_NOME + 1)
        assertEquals(ErroValidacao.MUITO_LONGO, ValidadorReceita.validarNome(nomeGrande))
    }

    @Test
    fun `nome valido nao tem erro`() {
        assertNull(ValidadorReceita.validarNome("Bolo de cenoura"))
    }

    @Test
    fun `tempo precisa ser numero inteiro maior que zero`() {
        assertEquals(ErroValidacao.OBRIGATORIO, ValidadorReceita.validarTempo(""))
        assertEquals(ErroValidacao.NAO_NUMERICO, ValidadorReceita.validarTempo("abc"))
        assertEquals(ErroValidacao.NAO_NUMERICO, ValidadorReceita.validarTempo("2.5"))
        assertEquals(ErroValidacao.FORA_DO_INTERVALO, ValidadorReceita.validarTempo("0"))
        assertEquals(ErroValidacao.FORA_DO_INTERVALO, ValidadorReceita.validarTempo("-5"))
        assertEquals(ErroValidacao.FORA_DO_INTERVALO, ValidadorReceita.validarTempo("1441"))
        assertNull(ValidadorReceita.validarTempo("45"))
        assertNull(ValidadorReceita.validarTempo(" 45 "))
    }

    @Test
    fun `precisa de pelo menos um ingrediente`() {
        assertEquals(ErroValidacao.OBRIGATORIO, ValidadorReceita.validarIngredientes(emptyList()))
        assertEquals(ErroValidacao.OBRIGATORIO, ValidadorReceita.validarIngredientes(listOf(" ")))
        assertNull(ValidadorReceita.validarIngredientes(listOf("Ovo")))
    }

    @Test
    fun `formulario so e valido quando todos os campos sao validos`() {
        assertTrue(ValidadorReceita.validar("Bolo", "40", listOf("Ovo")).valido)
        assertFalse(ValidadorReceita.validar("", "40", listOf("Ovo")).valido)
        assertFalse(ValidadorReceita.validar("Bolo", "0", listOf("Ovo")).valido)
        assertFalse(ValidadorReceita.validar("Bolo", "40", emptyList()).valido)
    }
}
