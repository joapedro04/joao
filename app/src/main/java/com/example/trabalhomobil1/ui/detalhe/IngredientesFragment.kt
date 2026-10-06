package com.example.trabalhomobil1.ui.detalhe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.databinding.FragmentIngredientesBinding
import com.example.trabalhomobil1.databinding.ItemChipIngredienteBinding

/**
 * Fragment que mostra os ingredientes como chips marcáveis ("já tenho este").
 *
 * Ciclo de vida usado:
 * - onCreateView: infla o layout e cria o binding.
 * - onViewCreated: a View já existe; preenchemos os chips e os cliques.
 * - onSaveInstanceState: guarda quais chips estavam marcados (para a rotação).
 * - onDestroyView: a View é destruída, mas o Fragment pode continuar vivo
 *   (ex.: na pilha de volta). Por isso zeramos o binding, evitando vazamento de memória.
 */
class IngredientesFragment : Fragment() {

    private var _binding: FragmentIngredientesBinding? = null

    // Só é seguro usar entre onCreateView e onDestroyView.
    private val binding get() = _binding!!

    // Posições dos ingredientes que o usuário marcou como "já tenho".
    private val marcados = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.getIntegerArrayList(ESTADO_MARCADOS)?.let { marcados.addAll(it) }

        val ingredientes = requireArguments().getStringArrayList(ARG_INGREDIENTES).orEmpty()

        ingredientes.forEachIndexed { posicao, nome ->
            // Infla o componente reutilizável e adiciona no ChipGroup.
            val chip = ItemChipIngredienteBinding.inflate(layoutInflater, binding.grupoIngredientes, false).root
            chip.text = nome
            chip.isChecked = posicao in marcados
            chip.setOnCheckedChangeListener { _, marcado ->
                if (marcado) marcados.add(posicao) else marcados.remove(posicao)
                atualizarResumo(ingredientes.size)
            }
            binding.grupoIngredientes.addView(chip)
        }

        atualizarResumo(ingredientes.size)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList(ESTADO_MARCADOS, ArrayList(marcados))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun atualizarResumo(total: Int) {
        binding.textoResumo.text = if (total == 0) {
            getString(R.string.sem_ingredientes)
        } else {
            getString(R.string.resumo_ingredientes, marcados.size, total)
        }
    }

    companion object {
        private const val ARG_INGREDIENTES = "arg_ingredientes"
        private const val ESTADO_MARCADOS = "estado_marcados"

        /** Cria o Fragment já com os argumentos (o Android recria Fragments usando o construtor vazio). */
        fun newInstance(ingredientes: List<String>) = IngredientesFragment().apply {
            arguments = bundleOf(ARG_INGREDIENTES to ArrayList(ingredientes))
        }
    }
}
