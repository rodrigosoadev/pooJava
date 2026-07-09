# Diagrama de Classes — Sistema de Controle de Estoque

```mermaid
classDiagram
    direction TB

  class Main {
    -Scanner scanner
    -ProdutoRepository produtoRepository
    -CategoriaRepository categoriaRepository
    -FornecedorRepository fornecedorRepository
    -MovimentacaoRepository movimentacaoRepository
    -EstoqueService estoqueService
    -RelatorioService relatorioService
    +main(String[] args)$
    -executar()
    -carregarDadosExemplo()
  }

  class TipoMovimentacao {
    <<enumeration>>
    ENTRADA
    SAIDA
  }

  class UnidadeMedida {
    <<enumeration>>
    UNIDADE
    CAIXA
    KILOGRAMA
    LITRO
    METRO
    PACOTE
  }

  class Identificavel {
    <<interface>>
    +getId() String
  }

  class Cadastravel~T~ {
    <<interface>>
    +cadastrar(T)
    +editar(T)
    +excluir(String)
    +consultar(String) T
    +listarTodos() List~T~
  }

  class Alertavel {
    <<interface>>
    +necessitaAlerta() boolean
    +gerarAlerta() String
  }

  class Validavel {
    <<interface>>
    +validar()
  }

  class Categoria {
    <<record>>
    +id String
    +nome String
    +descricao String
    +getId() String
  }

  class Fornecedor {
    -String id
    -String nome
    -String cnpj
    -String email
    -String telefone
    +validar()
  }

  class Produto {
    -String id
    -String nome
    -String descricao
    -double preco
    -int quantidadeEstoque
    -int estoqueMinimo
    -UnidadeMedida unidadeMedida
    -String categoriaId
    -String fornecedorId
    +necessitaAlerta() boolean
    +gerarAlerta() String
    +validar()
  }

  class Movimentacao {
    <<abstract>>
    -String id
    -String produtoId
    -int quantidade
    -LocalDate data
    +getTipo() TipoMovimentacao*
    +getTipoDescricao() String*
    +processar(Produto)*
    +validar()
  }

  class Entrada {
    +processar(Produto)
    +getTipoDescricao() String
  }

  class Saida {
    +processar(Produto)
    +getTipoDescricao() String
  }

  class Repositorio~T~ {
    <<abstract>>
    -Map~String,T~ entidades
  }

  class ProdutoRepository
  class CategoriaRepository
  class FornecedorRepository
  class MovimentacaoRepository {
    +listarPorProduto(String) List~Movimentacao~
  }

  class EstoqueService {
    -ProdutoRepository produtoRepository
    -MovimentacaoRepository movimentacaoRepository
    +registrarMovimentacao(Movimentacao)
  }

  class RelatorioService {
    -ProdutoRepository produtoRepository
    -CategoriaRepository categoriaRepository
    -FornecedorRepository fornecedorRepository
    -MovimentacaoRepository movimentacaoRepository
    +listarProdutosGeral() List~String~
    +listarProdutosComAlerta() List~String~
    +calcularValorTotalEstoque() double
    +historicoMovimentacoesPorProduto(String) List~String~
    +historicoMovimentacoesPorPeriodo(LocalDate, LocalDate) List~String~
  }

  class Validador {
    +validarCnpj(String)$
    +validarEmail(String)$
    +validarDataNaoFutura(LocalDate)$
    +validarPositivo(double, String)$
    +validarNaoNegativo(int, String)$
  }

  class GeradorId {
    +gerarIdProduto()$ String
    +gerarIdFornecedor()$ String
    +gerarIdCategoria()$ String
    +gerarIdMovimentacao()$ String
  }

  class EntidadeNaoEncontradaException {
    <<exception>>
  }

  class EntidadeDuplicadaException {
    <<exception>>
  }

  class EstoqueInsuficienteException {
    <<exception>>
  }

  class ValidacaoException {
    <<exception>>
  }

  Identificavel <|.. Categoria
  Identificavel <|.. Fornecedor
  Identificavel <|.. Produto
  Identificavel <|.. Movimentacao

  Validavel <|.. Fornecedor
  Validavel <|.. Produto

  Alertavel <|.. Produto

  Cadastravel <|.. Repositorio
  Repositorio <|-- ProdutoRepository
  Repositorio <|-- CategoriaRepository
  Repositorio <|-- FornecedorRepository
  Repositorio <|-- MovimentacaoRepository

  Movimentacao <|-- Entrada
  Movimentacao <|-- Saida

  Produto --> UnidadeMedida : usa
  Movimentacao --> TipoMovimentacao : usa
  Produto ..> Categoria : categoriaId
  Produto ..> Fornecedor : fornecedorId
  Movimentacao ..> Produto : produtoId

  Main --> ProdutoRepository
  Main --> CategoriaRepository
  Main --> FornecedorRepository
  Main --> MovimentacaoRepository
  Main --> EstoqueService
  Main --> RelatorioService

  EstoqueService --> ProdutoRepository
  EstoqueService --> MovimentacaoRepository
  EstoqueService ..> Movimentacao : processar()

  RelatorioService --> ProdutoRepository
  RelatorioService --> CategoriaRepository
  RelatorioService --> FornecedorRepository
  RelatorioService --> MovimentacaoRepository

  Fornecedor ..> Validador : usa
  Produto ..> Validador : usa
  Movimentacao ..> Validador : usa

  Main ..> GeradorId : usa
  Repositorio ..> ValidacaoException : lança
  Repositorio ..> EntidadeNaoEncontradaException : lança
  Repositorio ..> EntidadeDuplicadaException : lança
  Saida ..> EstoqueInsuficienteException : lança
  EstoqueService ..> EstoqueInsuficienteException : lança
```

## Legenda

| Símbolo | Significado |
|---------|-------------|
| `<\|--` | Herança / extensão |
| `<\|..` | Implementação de interface |
| `-->` | Associação / uso direto |
| `..>` | Dependência |

## Relações Principais

1. **Polimorfismo:** `EstoqueService` recebe `Movimentacao` e invoca `processar(Produto)` — resolvido em tempo de execução por `Entrada` ou `Saida`.
2. **Genéricos:** `Repositorio<T extends Identificavel>` implementa `Cadastravel<T>` com `HashMap<String, T>`.
3. **Alertas:** `RelatorioService` filtra produtos via `Alertavel::necessitaAlerta` sem conhecer detalhes de `Produto`.
4. **Validação:** entidades `Validavel` são validadas automaticamente no `Repositorio` antes de cadastrar/editar.
