# Guia de estudo para a apresentação

Respostas curtas, do jeito que dá para falar em voz alta. Cada uma aponta para o arquivo onde a
coisa acontece: abra o arquivo enquanto explica.

---

## 1. Visão geral (decore isto)

> "É um app de receitas. Na Etapa 2 ele busca receitas no TheMealDB, mostra numa lista, abre o
> detalhe, deixa favoritar e criar receitas próprias, e tudo isso fica salvo no Room. A arquitetura
> tem três camadas: UI em Compose com ViewModel, um Repository que junta API e banco, e as fontes de
> dados (Retrofit e Room). As dependências são criadas à mão no AppContainer."

Roteiro de demonstração (2 min):
1. Buscar `chicken` → aparece o *Carregando* e depois a lista.
2. Digitar rápido `cake` → só uma busca acontece (debounce).
3. Abrir uma receita → favoritar (coração) → voltar → abrir **Salvas** → está lá.
4. **Nova receita** → mostrar o botão desabilitado e os erros → preencher → Snackbar "Receita salva!" → **Ver**.
5. Girar a tela → nada se perde.
6. Fechar o app de verdade (remover dos recentes) e abrir de novo → **Salvas** continua com tudo.
7. Ativar o modo avião e buscar `chicken` de novo → funciona pelo cache; buscar algo novo → estado de **Erro** com "Tentar novamente".

---

## 2. Parcial (Views XML)

**Por que Intent explícita?**
Porque eu sei exatamente qual tela abrir: `Intent(this, DetalheReceitaActivity::class.java)`.
A implícita é para pedir uma *ação* ao sistema (ex.: compartilhar), sem dizer qual app vai responder.

**Por que passar só o id e não o objeto inteiro?**
O id é pequeno e não precisa ser `Parcelable`. A tela de detalhe busca a receita pelo id e trata o
caso de não encontrar. O Navigation 3 da Etapa 2 usa o mesmo padrão (`DetalheRota(receitaId)`).

**ViewBinding x findViewById?**
O ViewBinding gera uma classe por layout com as Views já tipadas. Não existe o risco de pegar um id
errado nem de dar `ClassCastException`, e ele é *null-safe*.

**Por que `_binding = null` no `onDestroyView` do Fragment?**
A View do Fragment pode ser destruída enquanto o Fragment continua vivo, por exemplo na pilha de volta.
Se eu guardar o binding, seguro a View antiga na memória (vazamento). Por isso eu solto no `onDestroyView`.

**Como você tratou campos opcionais?**
`receita.categoria ?: "Sem categoria"` (Elvis), `receita.tempoPreparo?.let { "$it min" } ?: "Tempo não informado"`.

**Por que `data class` com `val`?**
Imutável: para "favoritar" eu crio uma cópia com `copy(favorita = true)`. Assim ninguém altera o
objeto por baixo dos panos, e comparar estados fica fácil (o `equals` já vem pronto).

---

## 3. Etapa 2 — perguntas prováveis

**Por que StateFlow e não LiveData?**
O StateFlow é Kotlin puro (coroutines). Dá para testar sem Android e combinar com operadores
(`debounce`, `flatMapLatest`, `combine`). Ele sempre tem um valor atual e funciona direto com
`collectAsStateWithLifecycle`, que, assim como o LiveData, para de coletar quando a tela não está visível.

**Para que serve o `collectAsStateWithLifecycle`?**
Ele transforma o `StateFlow` em `State` do Compose: quando o valor muda, a tela recompõe. E ele só
coleta quando a tela está pelo menos em STARTED, o que economiza bateria quando o app vai para segundo plano.

**O que é fluxo unidirecional?**
O estado desce (ViewModel → tela) e os eventos sobem (tela → funções do ViewModel). A tela nunca
altera o estado diretamente. Exemplo: `onValueChange = viewModel::aoMudarTermo`.

**Como o estado sobrevive à rotação?**
Girar a tela recria a Activity, mas o **ViewModel não é recriado**. A lista e o estado continuam nele.
O **termo de busca** fica no `SavedStateHandle`, que sobrevive até se o Android matar o processo. A
**pilha de navegação** é salva pelo `rememberNavBackStack`, porque as rotas são `@Serializable`.
(`BuscaViewModel.kt`, `AppNavegacao.kt`)

**Como funciona o back stack no Navigation 3?**
A pilha é só uma lista de rotas que **eu** controlo. Navegar é `pilha.add(DetalheRota(id))` e voltar é
`pilha.removeLastOrNull()`. O `NavDisplay` sempre mostra a última rota da lista, e o `entryProvider`
diz qual tela desenhar para cada tipo de rota. Os argumentos vão dentro da rota (`DetalheRota(val receitaId: String)`).

**Para que servem os dois decorators?**
- `rememberSaveableStateHolderNavEntryDecorator`: guarda o estado `rememberSaveable` de cada tela
  enquanto ela está na pilha (ex.: a rolagem).
- `rememberViewModelStoreNavEntryDecorator`: cada entrada da pilha tem os **próprios** ViewModels.
  Quando ela sai da pilha, o ViewModel é destruído e o `viewModelScope` cancela as coroutines.
  Dois detalhes de receitas diferentes na pilha têm ViewModels diferentes.

**Como a busca é cancelada?**
Em `BuscaViewModel.uiState`:
1. `debounce(500)`: só deixa o termo passar depois de 500 ms sem digitação. Digitar "c", "ch", "chi" rápido gera uma busca só.
2. `flatMapLatest`: quando chega um termo novo, ele **cancela a coroutine da busca anterior**. O
   cancelamento chega no `suspend fun` do Retrofit, que aborta a requisição HTTP.
3. `viewModelScope`: se a tela sair da pilha, o ViewModel morre e tudo que estava rodando é cancelado.
Isso está provado no teste `BuscaViewModelTest` ("termo novo cancela a busca anterior").

**Por que você relança a `CancellationException`?**
Cancelamento não é erro. Se eu capturasse a exceção e mostrasse "Erro", a coroutine cancelada
continuaria rodando e mostraria um estado errado.

**Por que `Dispatchers.IO` no repository?**
Rede e disco são entrada/saída: o IO é um pool de threads para esse tipo de trabalho. A thread
principal fica livre para desenhar a tela. O ViewModel não precisa saber disso, porque o repository
já é seguro para ser chamado da Main.

**Por que Room retornando Flow?**
O Flow do Room emite de novo sempre que a tabela muda. Quando eu favorito no detalhe, a tela Salvas
e o próprio ícone do detalhe se atualizam sozinhos, sem eu mandar "recarregar".

**Por que um Repository com interface?**
O ViewModel só conhece a interface `ReceitaRepository` e não sabe se o dado vem da API ou do Room.
Nos testes eu troco pela `FakeReceitaRepository`, que funciona em memória.

**Como funciona o cache?**
Cada termo buscado é salvo na tabela `buscas_cache` com a hora da busca. Se o mesmo termo foi
buscado há menos de 1 hora, a resposta sai só do banco. Senão, vai na API e salva. Se não houver
internet, uso o que já está salvo; se não houver nada salvo, a tela mostra **Erro** com "Tentar novamente".
(`ReceitaRepositoryImpl.buscar` e `ReceitaRepositoryImplTest`)

**Por que DTO, Entity e modelo de domínio separados?**
O DTO é o formato da API (20 campos de ingrediente!), a Entity é o formato do banco e a `Receita`
é o que o app usa. Se a API mudar, só o DTO e o mapper mudam. (`ReceitaMappers.kt`)

**O que é o AppContainer?**
Injeção de dependências manual: ele cria **uma vez** o Retrofit, o banco e o repository. Ele é
criado na `ReceitasApp` (Application), então vive enquanto o app vive. Os ViewModels recebem o
repository pelo construtor (via `viewModelFactory`).

**Para que serve o `sealed interface` no UiState?**
Ele limita os estados possíveis (Carregando, Sucesso, Vazio, Erro). O `when` na tela é **obrigado**
a tratar todos: se eu criar um estado novo e esquecer de tratar, o código nem compila.

**Onde fica a validação do formulário e por que separada?**
Em `domain/validacao/ValidadorReceita.kt`, que é Kotlin puro. Dá para testar com JUnit simples, sem
emulador (`ValidadorReceitaTest`). A tela só converte o tipo de erro em mensagem.

**Por que o erro só aparece depois que o usuário mexe no campo?**
Para a tela não abrir já cheia de vermelho. Os campos "tocado" no `NovaReceitaUiState` controlam
isso. O botão "Salvar" fica desabilitado enquanto o formulário for inválido.

**Onde fica a chave da API e por que não vai para o GitHub?**
O TheMealDB usa a chave **pública de teste "1"**, documentada no site e que já faz parte da URL. Ela
não é segredo. Se eu usasse uma chave privada, ela iria no `local.properties` (que está no
`.gitignore`), seria lida pelo Gradle e exposta via `BuildConfig`, e eu versionaria só um
`local.properties.example` sem o valor.

**O que você fez de acessibilidade?**
- `contentDescription` em ícones e fotos; `null` nas imagens decorativas, porque o nome já está escrito ao lado.
- Cores só do `MaterialTheme`, que já vêm em pares com contraste.
- `IconButton`, `ListItem` e botões têm área de toque de 48dp.
- Textos com a tipografia do tema (em `sp`), sem altura fixa, e telas com rolagem: funciona com a fonte no máximo.
- Títulos marcados com `semantics { heading() }` para o TalkBack navegar por eles.

**O que são as animações implícitas?**
`AnimatedContent` faz fade quando o tipo de estado muda (Carregando → Lista). `animateItem()` anima
itens entrando e saindo da lista. `animateContentSize()` e `AnimatedVisibility` no formulário.

**Para que serve o `key` na LazyColumn?**
Para o Compose saber qual item é qual, mesmo se a lista mudar de ordem. Ele preserva o estado da
linha certa e consegue animar. Na tela Salvas as keys têm prefixo, porque a mesma receita pode
aparecer nas duas seções, e a key precisa ser única.

**Como você testou?**
- Unitários (JVM): validador, `BuscaViewModel` (debounce, cancelamento, erro e retry, SavedStateHandle),
  `NovaReceitaViewModel`, `DetalheViewModel`, cache do repository. Usam fakes e `kotlinx-coroutines-test`
  com relógio virtual (`advanceTimeBy`).
- UI (emulador): formulário (botão desabilitado, erro, Snackbar) e estados da busca.

---

## 4. Perguntas de verificação (responda sozinho antes da apresentação)

1. O que acontece com uma busca em andamento se eu digitar outra letra? Em que linha isso acontece?
2. Se eu tirar o `SavedStateHandle` e guardar o termo numa variável comum do ViewModel, o que se perde?
3. Por que o `DetalheViewModel` recebe o id pelo construtor e não por uma função `carregar(id)`?
4. Qual a diferença entre `compileSdk`, `targetSdk` e `minSdk`?
5. Por que o `gradlew` vai para o Git, mas o `local.properties` não?
6. Se o professor estiver sem internet, o que o app mostra na primeira abertura?
