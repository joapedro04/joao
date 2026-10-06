# Receitas — trabalho de Desenvolvimento Android

## Objetivo

App para **encontrar receitas, ver os detalhes e separar as favoritas**. Nesta etapa (Parcial),
os dados são simulados (mock) e as telas são feitas com Android Views (XML).

## Como rodar

| Item | Versão |
|---|---|
| Android Studio | Rabbit 1 (2026.2.1) ou mais recente |
| JDK | 17 ou mais recente (o JBR que vem com o Android Studio já serve) |
| minSdk / targetSdk / compileSdk | 24 / 37 / 37 |
| Gradle / AGP / Kotlin | 9.8.0 (wrapper) / 9.4.1 / 2.4.20 |

1. Clone o repositório: `git clone <url-deste-repositório>`
2. No Android Studio: **File > Open** e escolha a pasta do projeto.
3. Aguarde o *Gradle Sync* (o Android Studio baixa sozinho o SDK que faltar e cria o `local.properties`).
4. Escolha um emulador ou celular (Android 7.0+) e clique em **Run**.

Pela linha de comando: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).

Não há chaves nem senhas no projeto: nada precisa ser configurado.

## Bibliotecas externas

| Biblioteca | Para que serve |
|---|---|
| AndroidX AppCompat | Base das Activities (`AppCompatActivity`) com compatibilidade para versões antigas do Android. |
| Material Components | Tema Material 3 e componentes como `MaterialButton`, `Chip` e `ChipGroup`. |
| AndroidX RecyclerView | Lista eficiente que recicla as linhas ao rolar. |
| AndroidX Fragment KTX | `Fragment` e `FragmentContainerView` usados na seção de ingredientes. |
| AndroidX Core KTX | Extensões Kotlin do Android (ex.: `bundleOf`). |
| Coil 3 (+ coil-network-okhttp) | Baixa e exibe as fotos das receitas em segundo plano, com cache. |

## Requisitos da Parcial → onde estão

| Requisito | Onde |
|---|---|
| Duas telas XML com Views e ViewGroups | `activity_lista_receitas.xml` (LinearLayout + RecyclerView), `activity_detalhe_receita.xml` (FrameLayout, ScrollView, LinearLayout, ImageView, TextView, Space) |
| Navegação por Intent explícita com dados | `ListaReceitasActivity.abrirDetalhe()` envia o id com `putExtra`; `DetalheReceitaActivity` lê com `getStringExtra` |
| Receita não encontrada | `DetalheReceitaActivity.mostrarNaoEncontrada()` |
| ViewBinding (sem findViewById) | `buildFeatures.viewBinding = true` e todas as telas, adapter e fragment |
| Interação que atualiza a UI | Botão favoritar (ícone + texto) em `DetalheReceitaActivity.alternarFavorito()` |
| data class imutável e opcionais | `domain/model/Receita.kt`; tratamento com `?.`, `?:` e `let` no adapter e no detalhe |
| Dados mockados | `data/local/ReceitasMock.kt` |
| Opcional: componente XML reutilizável | `item_chip_ingrediente.xml`, inflado no `IngredientesFragment` (clique atualiza o contador) |
| Opcional: Fragment com ViewBinding | `ui/detalhe/IngredientesFragment.kt` (`_binding = null` em `onDestroyView`) |

## Arquitetura (Parcial)

- `domain/model`: modelo `Receita` (data class imutável).
- `data/local`: `ReceitasMock`, que faz o papel de fonte de dados.
- `ui/lista` e `ui/detalhe`: Activities, Adapter e Fragment.

Fluxo: `ReceitasMock` → `ReceitaAdapter` (lista) → clique → `Intent` com o id → `DetalheReceitaActivity` busca no mock pelo id.
