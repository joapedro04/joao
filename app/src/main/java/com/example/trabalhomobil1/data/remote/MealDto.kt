package com.example.trabalhomobil1.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO (Data Transfer Object): espelha exatamente o JSON do TheMealDB.
 * Fica isolado na camada remota; o resto do app usa o modelo de domínio [com.example.trabalhomobil1.domain.model.Receita].
 */
@Serializable
data class MealsResponseDto(
    // A API devolve "meals": null quando não encontra nada.
    val meals: List<MealDto>? = null
)

@Serializable
data class MealDto(
    @SerialName("idMeal") val id: String,
    @SerialName("strMeal") val nome: String,
    @SerialName("strCategory") val categoria: String? = null,
    @SerialName("strInstructions") val instrucoes: String? = null,
    @SerialName("strMealThumb") val imagemUrl: String? = null,
    // A API não usa lista: são 20 campos fixos de ingrediente e 20 de medida.
    @SerialName("strIngredient1") val ingrediente1: String? = null,
    @SerialName("strIngredient2") val ingrediente2: String? = null,
    @SerialName("strIngredient3") val ingrediente3: String? = null,
    @SerialName("strIngredient4") val ingrediente4: String? = null,
    @SerialName("strIngredient5") val ingrediente5: String? = null,
    @SerialName("strIngredient6") val ingrediente6: String? = null,
    @SerialName("strIngredient7") val ingrediente7: String? = null,
    @SerialName("strIngredient8") val ingrediente8: String? = null,
    @SerialName("strIngredient9") val ingrediente9: String? = null,
    @SerialName("strIngredient10") val ingrediente10: String? = null,
    @SerialName("strIngredient11") val ingrediente11: String? = null,
    @SerialName("strIngredient12") val ingrediente12: String? = null,
    @SerialName("strIngredient13") val ingrediente13: String? = null,
    @SerialName("strIngredient14") val ingrediente14: String? = null,
    @SerialName("strIngredient15") val ingrediente15: String? = null,
    @SerialName("strIngredient16") val ingrediente16: String? = null,
    @SerialName("strIngredient17") val ingrediente17: String? = null,
    @SerialName("strIngredient18") val ingrediente18: String? = null,
    @SerialName("strIngredient19") val ingrediente19: String? = null,
    @SerialName("strIngredient20") val ingrediente20: String? = null,
    @SerialName("strMeasure1") val medida1: String? = null,
    @SerialName("strMeasure2") val medida2: String? = null,
    @SerialName("strMeasure3") val medida3: String? = null,
    @SerialName("strMeasure4") val medida4: String? = null,
    @SerialName("strMeasure5") val medida5: String? = null,
    @SerialName("strMeasure6") val medida6: String? = null,
    @SerialName("strMeasure7") val medida7: String? = null,
    @SerialName("strMeasure8") val medida8: String? = null,
    @SerialName("strMeasure9") val medida9: String? = null,
    @SerialName("strMeasure10") val medida10: String? = null,
    @SerialName("strMeasure11") val medida11: String? = null,
    @SerialName("strMeasure12") val medida12: String? = null,
    @SerialName("strMeasure13") val medida13: String? = null,
    @SerialName("strMeasure14") val medida14: String? = null,
    @SerialName("strMeasure15") val medida15: String? = null,
    @SerialName("strMeasure16") val medida16: String? = null,
    @SerialName("strMeasure17") val medida17: String? = null,
    @SerialName("strMeasure18") val medida18: String? = null,
    @SerialName("strMeasure19") val medida19: String? = null,
    @SerialName("strMeasure20") val medida20: String? = null
) {
    /** Junta ingrediente + medida e descarta os campos vazios. Ex.: "Arroz (2 xícaras)". */
    fun ingredientesComMedida(): List<String> = listOf(
        ingrediente1 to medida1,
        ingrediente2 to medida2,
        ingrediente3 to medida3,
        ingrediente4 to medida4,
        ingrediente5 to medida5,
        ingrediente6 to medida6,
        ingrediente7 to medida7,
        ingrediente8 to medida8,
        ingrediente9 to medida9,
        ingrediente10 to medida10,
        ingrediente11 to medida11,
        ingrediente12 to medida12,
        ingrediente13 to medida13,
        ingrediente14 to medida14,
        ingrediente15 to medida15,
        ingrediente16 to medida16,
        ingrediente17 to medida17,
        ingrediente18 to medida18,
        ingrediente19 to medida19,
        ingrediente20 to medida20
    ).mapNotNull { (ingrediente, medida) ->
        val nomeIngrediente = ingrediente?.trim()
        if (nomeIngrediente.isNullOrEmpty()) return@mapNotNull null
        val textoMedida = medida?.trim()
        if (textoMedida.isNullOrEmpty()) nomeIngrediente else "$nomeIngrediente ($textoMedida)"
    }
}
