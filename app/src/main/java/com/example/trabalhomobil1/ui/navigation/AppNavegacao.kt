package com.example.trabalhomobil1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.trabalhomobil1.data.repository.ReceitaRepository
import com.example.trabalhomobil1.ui.busca.BuscaScreen
import com.example.trabalhomobil1.ui.busca.BuscaViewModel
import com.example.trabalhomobil1.ui.detalhe.DetalheScreen
import com.example.trabalhomobil1.ui.detalhe.DetalheViewModel
import com.example.trabalhomobil1.ui.novareceita.NovaReceitaScreen
import com.example.trabalhomobil1.ui.novareceita.NovaReceitaViewModel
import com.example.trabalhomobil1.ui.salvas.SalvasScreen
import com.example.trabalhomobil1.ui.salvas.SalvasViewModel

/**
 * Navegação do app com Navigation 3.
 *
 * - A pilha (back stack) é uma lista de rotas que NÓS controlamos: navegar = add(rota),
 *   voltar = removeLastOrNull(). O NavDisplay sempre mostra a última rota da lista.
 * - rememberNavBackStack salva a pilha (por isso as rotas são @Serializable).
 * - entryProvider diz qual tela desenhar para cada tipo de rota.
 * - Decorators:
 *   - rememberSaveableStateHolderNavEntryDecorator: guarda o estado "rememberSaveable" de cada tela
 *     (ex.: posição de rolagem) enquanto ela estiver na pilha.
 *   - rememberViewModelStoreNavEntryDecorator: cada entrada da pilha ganha seus próprios ViewModels,
 *     que são destruídos quando a entrada sai da pilha.
 */
@Composable
fun AppNavegacao(repository: ReceitaRepository) {
    val pilha = rememberNavBackStack(BuscaRota)

    NavDisplay(
        backStack = pilha,
        onBack = { pilha.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<BuscaRota> {
                BuscaScreen(
                    viewModel = viewModel(factory = BuscaViewModel.fabrica(repository)),
                    aoAbrirReceita = { id -> pilha.add(DetalheRota(id)) },
                    aoAbrirSalvas = { pilha.add(SalvasRota) },
                    aoCriarReceita = { pilha.add(NovaReceitaRota) }
                )
            }
            entry<DetalheRota> { rota ->
                // O argumento vem da própria rota.
                DetalheScreen(
                    viewModel = viewModel(factory = DetalheViewModel.fabrica(rota.receitaId, repository)),
                    aoVoltar = { pilha.removeLastOrNull() }
                )
            }
            entry<SalvasRota> {
                SalvasScreen(
                    viewModel = viewModel(factory = SalvasViewModel.fabrica(repository)),
                    aoAbrirReceita = { id -> pilha.add(DetalheRota(id)) },
                    aoVoltar = { pilha.removeLastOrNull() }
                )
            }
            entry<NovaReceitaRota> {
                NovaReceitaScreen(
                    viewModel = viewModel(factory = NovaReceitaViewModel.fabrica(repository)),
                    aoAbrirReceita = { id ->
                        // Troca o formulário pelo detalhe da receita recém-criada.
                        pilha.removeLastOrNull()
                        pilha.add(DetalheRota(id))
                    },
                    aoVoltar = { pilha.removeLastOrNull() }
                )
            }
        }
    )
}
