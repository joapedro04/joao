package com.example.trabalhomobil1.ui.detalhe

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.example.trabalhomobil1.R
import com.example.trabalhomobil1.data.local.ReceitasMock
import com.example.trabalhomobil1.databinding.ActivityDetalheReceitaBinding
import com.example.trabalhomobil1.domain.model.Receita

/** Tela 2: detalhe de uma receita, recebida pelo id enviado na Intent. */
class DetalheReceitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheReceitaBinding

    // Receita exibida. Como Receita é imutável, favoritar cria uma cópia nova (copy).
    private var receita: Receita? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheReceitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // O extra pode faltar (null) e o id pode não existir no mock: os dois casos dão "não encontrada".
        val id = intent.getStringExtra(EXTRA_RECEITA_ID)
        val encontrada = id?.let { ReceitasMock.buscarPorId(it) }
        if (encontrada == null) {
            mostrarNaoEncontrada()
            return
        }

        // Depois de uma rotação a Activity é recriada: recuperamos o favorito salvo no Bundle.
        val favoritaSalva = savedInstanceState?.getBoolean(ESTADO_FAVORITA) ?: encontrada.favorita
        val atual = encontrada.copy(favorita = favoritaSalva)
        receita = atual

        exibir(atual)
        binding.botaoFavoritar.setOnClickListener { alternarFavorito() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        receita?.let { outState.putBoolean(ESTADO_FAVORITA, it.favorita) }
    }

    private fun exibir(receita: Receita) {
        binding.textoNome.text = receita.nome
        binding.textoCategoria.text = receita.categoria ?: getString(R.string.sem_categoria)
        binding.textoTempo.text = receita.tempoPreparo?.let { getString(R.string.tempo_minutos, it) }
            ?: getString(R.string.tempo_nao_informado)
        binding.textoModoPreparo.text = receita.modoPreparo ?: getString(R.string.modo_preparo_nao_informado)

        binding.imagemReceita.contentDescription = getString(R.string.foto_da_receita, receita.nome)
        binding.imagemReceita.load(receita.imagemUrl) {
            placeholder(R.drawable.ic_receita_placeholder)
            error(R.drawable.ic_receita_placeholder)
            fallback(R.drawable.ic_receita_placeholder)
        }

        atualizarBotaoFavorito(receita.favorita)
    }

    /** Interação que atualiza a UI: troca o ícone e o texto do botão. */
    private fun alternarFavorito() {
        val atual = receita ?: return
        val nova = atual.copy(favorita = !atual.favorita)
        receita = nova
        atualizarBotaoFavorito(nova.favorita)
    }

    private fun atualizarBotaoFavorito(favorita: Boolean) {
        if (favorita) {
            binding.botaoFavoritar.setIconResource(R.drawable.ic_favorito)
            binding.botaoFavoritar.text = getString(R.string.desfavoritar)
        } else {
            binding.botaoFavoritar.setIconResource(R.drawable.ic_favorito_borda)
            binding.botaoFavoritar.text = getString(R.string.favoritar)
        }
    }

    private fun mostrarNaoEncontrada() {
        binding.conteudo.visibility = View.GONE
        binding.estadoNaoEncontrada.visibility = View.VISIBLE
        binding.botaoVoltar.setOnClickListener { finish() }
    }

    companion object {
        /** Chave do extra com o id da receita (a constante evita erro de digitação). */
        const val EXTRA_RECEITA_ID = "extra_receita_id"
        private const val ESTADO_FAVORITA = "estado_favorita"
    }
}
