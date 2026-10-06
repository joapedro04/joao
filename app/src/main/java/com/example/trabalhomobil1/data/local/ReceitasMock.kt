package com.example.trabalhomobil1.data.local

import com.example.trabalhomobil1.domain.model.Receita

/**
 * Dados simulados da Etapa 1 (sem API e sem banco).
 * Os ids e as fotos são os mesmos do TheMealDB, que será usado na Etapa 2.
 * Alguns campos ficaram nulos de propósito para testar o tratamento de opcionais.
 */
object ReceitasMock {

    val receitas: List<Receita> = listOf(
        Receita(
            id = "52772",
            nome = "Frango Teriyaki ao forno",
            categoria = "Frango",
            modoPreparo = "Misture o molho, cubra o frango e asse por 30 minutos a 180 °C.",
            tempoPreparo = 45,
            imagemUrl = "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
            ingredientes = listOf("Molho de soja", "Água", "Açúcar mascavo", "Gengibre", "Alho", "Amido de milho")
        ),
        Receita(
            id = "52795",
            nome = "Frango Handi",
            categoria = "Frango",
            modoPreparo = "Refogue cebola, tomate e temperos; junte o frango e cozinhe em fogo baixo.",
            tempoPreparo = null,
            imagemUrl = "https://www.themealdb.com/images/media/meals/wyxwsp1486979827.jpg",
            ingredientes = listOf("Frango", "Cebola", "Tomate", "Alho", "Pasta de gengibre", "Óleo")
        ),
        Receita(
            id = "52874",
            nome = "Torta de carne com mostarda",
            categoria = "Carne",
            modoPreparo = null,
            tempoPreparo = 120,
            imagemUrl = "https://www.themealdb.com/images/media/meals/sytuqu1511553755.jpg",
            ingredientes = listOf("Carne", "Farinha de trigo", "Óleo", "Vinho tinto", "Caldo de carne", "Cebola")
        ),
        Receita(
            id = "52977",
            nome = "Sopa Corba",
            categoria = null,
            modoPreparo = "Cozinhe a lentilha com os legumes e bata até ficar cremosa.",
            tempoPreparo = 40,
            imagemUrl = "https://www.themealdb.com/images/media/meals/58oia61564916529.jpg",
            ingredientes = listOf("Lentilha", "Cebola", "Cenoura", "Extrato de tomate", "Cominho", "Páprica")
        ),
        Receita(
            id = "53049",
            nome = "Apam balik (panqueca)",
            categoria = "Sobremesa",
            modoPreparo = "Prepare a massa, cozinhe na frigideira e recheie com amendoim e açúcar.",
            tempoPreparo = 25,
            imagemUrl = null,
            ingredientes = listOf("Leite", "Óleo", "Ovos", "Farinha", "Fermento", "Sal")
        ),
        Receita(
            id = "52959",
            nome = "Salmão assado com erva-doce",
            categoria = "Frutos do mar",
            modoPreparo = "Asse a erva-doce e os tomates, junte o salmão e finalize com limão.",
            tempoPreparo = 35,
            imagemUrl = "https://www.themealdb.com/images/media/meals/1548772327.jpg",
            ingredientes = listOf("Erva-doce", "Salsinha", "Limão", "Tomate-cereja", "Azeite", "Salmão")
        )
    )

    /** Retorna a receita com o id informado, ou null se não existir. */
    fun buscarPorId(id: String): Receita? = receitas.find { it.id == id }
}
