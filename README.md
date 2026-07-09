# Sistema de Controle de Estoque

Sistema de gerenciamento de estoque desenvolvido em **Java puro** (sem frameworks), com interface via console. Permite cadastrar produtos, categorias e fornecedores, registrar movimentações de entrada e saída, gerar relatórios e receber alertas de estoque mínimo.

## Visão Geral

O sistema mantém todos os dados em memória durante a execução. A arquitetura segue princípios de POO com separação clara entre domínio (`model`), persistência em memória (`repository`), regras de negócio (`service`) e interface (`Main`).

Principais capacidades:

- CRUD de produtos, categorias e fornecedores
- Registro de entradas e saídas de estoque com validação de saldo
- Alertas automáticos quando o estoque atinge ou fica abaixo do mínimo
- Relatórios de produtos, valor total do estoque e histórico de movimentações

## Estrutura de Pastas

```
pooJava/
├── src/com/estoque/
│   ├── Main.java                      # Menu de console (ponto de entrada)
│   ├── enums/
│   │   ├── TipoMovimentacao.java
│   │   └── UnidadeMedida.java
│   ├── model/
│   │   ├── Categoria.java             # Record
│   │   ├── Fornecedor.java
│   │   ├── Produto.java
│   │   ├── Movimentacao.java          # Classe abstrata
│   │   ├── Entrada.java
│   │   └── Saida.java
│   ├── interfaces/
│   │   ├── Identificavel.java
│   │   ├── Cadastravel.java
│   │   ├── Alertavel.java
│   │   └── Validavel.java
│   ├── exceptions/
│   │   ├── EntidadeNaoEncontradaException.java
│   │   ├── EntidadeDuplicadaException.java
│   │   ├── EstoqueInsuficienteException.java
│   │   └── ValidacaoException.java
│   ├── repository/
│   │   ├── Repositorio.java           # Genérico com HashMap
│   │   ├── ProdutoRepository.java
│   │   ├── CategoriaRepository.java
│   │   ├── FornecedorRepository.java
│   │   └── MovimentacaoRepository.java
│   ├── service/
│   │   ├── EstoqueService.java
│   │   └── RelatorioService.java
│   └── util/
│       ├── Validador.java
│       └── GeradorId.java
├── bin/                               # Classes compiladas (gerado)
├── README.md
└── diagrama-classes.md
```

## Como Compilar e Executar

### Linux / macOS / Git Bash

```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin com.estoque.Main
```

### Windows (PowerShell)

```powershell
New-Item -ItemType Directory -Force -Path bin
Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName } | ForEach-Object { javac -d bin $_ }
# Ou compile tudo de uma vez:
$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d bin $files
java -cp bin com.estoque.Main
```

### Windows (CMD)

```cmd
mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
java -cp bin com.estoque.Main
```

**Requisito:** JDK 17 ou superior (usa `record` e pattern matching `instanceof`).

## Funcionalidades do Menu

### Menu Principal

| Opção | Descrição |
|-------|-----------|
| 1 | Submenu de Produtos |
| 2 | Submenu de Categorias |
| 3 | Submenu de Fornecedores |
| 4 | Submenu de Movimentações |
| 5 | Submenu de Relatórios |
| 0 | Sair |

### Produtos
Cadastrar, editar, excluir, consultar por ID e listar todos (com alertas de estoque mínimo).

### Categorias
Cadastrar, editar, excluir e listar todas.

### Fornecedores
Cadastrar, editar, excluir e listar todos (com validação de CNPJ e e-mail).

### Movimentações
- Registrar entrada (soma ao estoque)
- Registrar saída (valida saldo; exibe mensagem amigável se insuficiente)
- Listar histórico de movimentações de um produto

### Relatórios
- Listagem geral de produtos (categoria, fornecedor, quantidade, preço)
- Produtos com estoque abaixo do mínimo
- Valor total do estoque (Σ preço × quantidade)
- Histórico por produto
- Histórico por período

## Principais Decisões de Design

### Records
`Categoria` é implementada como `record` imutável, com método explícito `getId()` no corpo para cumprir o contrato `Identificavel`.

### Interfaces
- **Identificavel** — permite indexação genérica no `Repositorio<T>` via `HashMap<String, T>`
- **Cadastravel&lt;T&gt;** — contrato CRUD implementado pela classe abstrata `Repositorio`
- **Alertavel** — `Produto` dispara alertas quando `quantidadeEstoque <= estoqueMinimo`
- **Validavel** — `Fornecedor` e `Produto` centralizam validações; repositório as invoca automaticamente

### Herança e Polimorfismo
`Movimentacao` é abstrata com `processar(Produto)` e `getTipoDescricao()`. `Entrada` soma quantidade; `Saida` valida saldo e subtrai. O `EstoqueService` chama `movimentacao.processar(produto)` de forma polimórfica, sem `if`/`instanceof`.

### Exceções Checked
Todas as exceções customizadas estendem `Exception`. O `Main` trata cada uma com `try/catch`, exibindo mensagens amigáveis sem encerrar o programa. Entradas inválidas do usuário (`InputMismatchException`, datas futuras, etc.) também são tratadas.

### Repositório Genérico
`Repositorio<T extends Identificavel>` encapsula o `HashMap` e implementa CRUD. Repositórios especializados adicionam ordenação nas listagens (produtos por nome, movimentações por data).

### Geração de IDs
`GeradorId` produz identificadores sequenciais com prefixo e zeros à esquerda: `PRD001`, `FOR001`, `CAT001`, `MOV001`.

## Dados de Exemplo

Ao iniciar, o sistema carrega automaticamente categorias, fornecedores e produtos de exemplo via `carregarDadosExemplo()` para facilitar testes manuais. O produto "Arroz 5kg" já inicia com estoque abaixo do mínimo para demonstrar alertas.

## Diagrama de Classes

Consulte o arquivo [diagrama-classes.md](diagrama-classes.md) para o diagrama Mermaid completo com todas as relações de herança, implementação e associação.
