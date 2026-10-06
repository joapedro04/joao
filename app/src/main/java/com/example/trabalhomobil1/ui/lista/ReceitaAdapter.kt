package com.example.trabalhomobil1.ui.lista

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.databinding.ItemReceitaBinding
import com.example.trabalhomobil1.domain.model.Receita

/**
 * Adapter do RecyclerView: cria as linhas (ViewHolders) e preenche cada uma com uma receita.
 * O clique é repassado para a Activity pela função [aoClicar], assim o adapter
 * não precisa saber como a navegação funciona.
 */
class ReceitaAdapter(
    private val receitas: List<Receita>,
    private val aoClicar: (Receita) -> Unit
) : RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder>() {

    /** Guarda o binding da linha para não procurar as Views de novo a cada bind. */
    class ReceitaViewHolder(val binding: ItemReceitaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceitaViewHolder {
        val binding = ItemReceitaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceitaViewHolder, position: Int) {
        val receita = receitas[position]
        val binding = holder.binding
        val contexto = binding.root.context

        binding.textoNome.text = receita.nome
        // ?: (Elvis) usa o texto padrão quando a categoria é nula.
        binding.textoCategoria.text = receita.categoria ?: contexto.getString(R.string.sem_categoria)
        // let só executa se o tempo não for nulo; senão cai no texto padrão.
        binding.textoTempo.text = receita.tempoPreparo?.let { contexto.getString(R.string.tempo_minutos, it) }
            ?: contexto.getString(R.string.tempo_nao_informado)

        // Coil baixa a foto em segundo plano; fallback é usado quando a URL é nula.
        binding.imagemReceita.load(receita.imagemUrl) {
            placeholder(R.drawable.ic_receita_placeholder)
            error(R.drawable.ic_receita_placeholder)
            fallback(R.drawable.ic_receita_placeholder)
        }

        binding.root.setOnClickListener { aoClicar(receita) }
    }

    override fun getItemCount(): Int = receitas.size
}
