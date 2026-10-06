package com.example.trabalhomobil1.ui.lista

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhomobil1.data.local.ReceitasMock
import com.example.trabalhomobil1.databinding.ActivityListaReceitasBinding
import com.example.trabalhomobil1.ui.detalhe.DetalheReceitaActivity

/** Tela 1: lista de receitas (RecyclerView com dados do mock). */
class ListaReceitasActivity : AppCompatActivity() {

    // ViewBinding: classe gerada a partir de activity_lista_receitas.xml (sem findViewById).
    private lateinit var binding: ActivityListaReceitasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaReceitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listaReceitas.adapter = ReceitaAdapter(ReceitasMock.receitas) { receita ->
            abrirDetalhe(receita.id)
        }
    }

    /** Intent explícita: dizemos exatamente qual Activity abrir e enviamos só o id. */
    private fun abrirDetalhe(id: String) {
        val intent = Intent(this, DetalheReceitaActivity::class.java)
        intent.putExtra(DetalheReceitaActivity.EXTRA_RECEITA_ID, id)
        startActivity(intent)
    }
}
