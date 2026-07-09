package com.estoque;

import com.estoque.enums.UnidadeMedida;
import com.estoque.exceptions.EntidadeDuplicadaException;
import com.estoque.exceptions.EntidadeNaoEncontradaException;
import com.estoque.exceptions.EstoqueInsuficienteException;
import com.estoque.exceptions.ValidacaoException;
import com.estoque.model.Categoria;
import com.estoque.model.Entrada;
import com.estoque.model.Fornecedor;
import com.estoque.model.Produto;
import com.estoque.model.Saida;
import com.estoque.repository.CategoriaRepository;
import com.estoque.repository.FornecedorRepository;
import com.estoque.repository.MovimentacaoRepository;
import com.estoque.repository.ProdutoRepository;
import com.estoque.service.EstoqueService;
import com.estoque.service.RelatorioService;
import com.estoque.util.GeradorId;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Ponto de entrada do Sistema de Controle de Estoque.
 * Exibe menus de console para gerenciamento de produtos, categorias, fornecedores,
 * movimentações e relatórios.
 */
public class Main {

    private final Scanner scanner = new Scanner(System.in);

    private final ProdutoRepository produtoRepository = new ProdutoRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final FornecedorRepository fornecedorRepository = new FornecedorRepository();
    private final MovimentacaoRepository movimentacaoRepository = new MovimentacaoRepository();

    private final EstoqueService estoqueService =
            new EstoqueService(produtoRepository, movimentacaoRepository);
    private final RelatorioService relatorioService =
            new RelatorioService(produtoRepository, categoriaRepository,
                    fornecedorRepository, movimentacaoRepository);

    /**
     * Inicia a aplicação exibindo o menu principal em loop.
     *
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.carregarDadosExemplo();
        app.executar();
    }

    /**
     * Carrega dados de exemplo para facilitar testes manuais do sistema.
     */
    private void carregarDadosExemplo() {
        try {
            Categoria cat1 = new Categoria(GeradorId.gerarIdCategoria(), "Eletrônicos", "Produtos eletrônicos em geral");
            Categoria cat2 = new Categoria(GeradorId.gerarIdCategoria(), "Alimentos", "Produtos alimentícios");
            categoriaRepository.cadastrar(cat1);
            categoriaRepository.cadastrar(cat2);

            Fornecedor for1 = new Fornecedor(
                    GeradorId.gerarIdFornecedor(),
                    "Tech Supply Ltda",
                    "11.222.333/0001-81",
                    "contato@techsupply.com.br",
                    "(11) 98765-4321"
            );
            Fornecedor for2 = new Fornecedor(
                    GeradorId.gerarIdFornecedor(),
                    "Distribuidora Alimentos SA",
                    "00.000.000/0001-91",
                    "vendas@distribuidora.com.br",
                    "(21) 3333-4444"
            );
            fornecedorRepository.cadastrar(for1);
            fornecedorRepository.cadastrar(for2);

            Produto p1 = new Produto(
                    GeradorId.gerarIdProduto(), "Mouse USB", "Mouse óptico USB",
                    29.90, 50, 10, UnidadeMedida.UNIDADE, cat1.id(), for1.getId()
            );
            Produto p2 = new Produto(
                    GeradorId.gerarIdProduto(), "Arroz 5kg", "Arroz branco tipo 1",
                    22.50, 8, 15, UnidadeMedida.PACOTE, cat2.id(), for2.getId()
            );
            produtoRepository.cadastrar(p1);
            produtoRepository.cadastrar(p2);

            System.out.println("[Dados de exemplo carregados com sucesso]");
        } catch (EntidadeDuplicadaException | ValidacaoException e) {
            System.out.println("[Aviso] Não foi possível carregar dados de exemplo: " + e.getMessage());
        }
    }

    private void executar() {
        boolean continuar = true;
        while (continuar) {
            exibirMenuPrincipal();
            int opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1 -> menuProdutos();
                case 2 -> menuCategorias();
                case 3 -> menuFornecedores();
                case 4 -> menuMovimentacoes();
                case 5 -> menuRelatorios();
                case 0 -> {
                    continuar = false;
                    System.out.println("\nEncerrando o sistema. Até logo!");
                }
                default -> System.out.println("\nOpção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n========================================");
        System.out.println("   SISTEMA DE CONTROLE DE ESTOQUE");
        System.out.println("========================================");
        System.out.println("1 - Produtos");
        System.out.println("2 - Categorias");
        System.out.println("3 - Fornecedores");
        System.out.println("4 - Movimentações de Estoque");
        System.out.println("5 - Relatórios");
        System.out.println("0 - Sair");
        System.out.println("----------------------------------------");
    }

    // ===================== PRODUTOS =====================

    private void menuProdutos() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- PRODUTOS ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Editar");
            System.out.println("3 - Excluir");
            System.out.println("4 - Consultar por ID");
            System.out.println("5 - Listar todos");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> cadastrarProduto();
                case 2 -> editarProduto();
                case 3 -> excluirProduto();
                case 4 -> consultarProduto();
                case 5 -> listarProdutos();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarProduto() {
        try {
            if (categoriaRepository.listarTodos().isEmpty()) {
                System.out.println("Cadastre ao menos uma categoria antes de cadastrar produtos.");
                return;
            }
            if (fornecedorRepository.listarTodos().isEmpty()) {
                System.out.println("Cadastre ao menos um fornecedor antes de cadastrar produtos.");
                return;
            }

            String nome = lerTexto("Nome: ");
            String descricao = lerTexto("Descrição: ");
            double preco = lerDoublePositivo("Preço: ");
            int quantidade = lerInteiroNaoNegativo("Quantidade em estoque: ");
            int estoqueMinimo = lerInteiroNaoNegativo("Estoque mínimo: ");
            UnidadeMedida unidade = lerUnidadeMedida();
            String categoriaId = escolherCategoria();
            String fornecedorId = escolherFornecedor();

            Produto produto = new Produto(
                    GeradorId.gerarIdProduto(), nome, descricao, preco,
                    quantidade, estoqueMinimo, unidade, categoriaId, fornecedorId
            );
            produtoRepository.cadastrar(produto);
            System.out.println("Produto cadastrado com sucesso! ID: " + produto.getId());
        } catch (EntidadeDuplicadaException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
        }
    }

    private void editarProduto() {
        try {
            String id = lerTexto("ID do produto: ");
            Produto produto = produtoRepository.consultar(id);

            System.out.println("Produto atual: " + produto);
            String nome = lerTextoOpcional("Novo nome (Enter para manter): ", produto.getNome());
            String descricao = lerTextoOpcional("Nova descrição (Enter para manter): ", produto.getDescricao());
            double preco = lerDoubleOpcional("Novo preço (Enter para manter): ", produto.getPreco());
            int estoqueMinimo = lerInteiroOpcional("Novo estoque mínimo (Enter para manter): ", produto.getEstoqueMinimo());
            UnidadeMedida unidade = lerUnidadeMedidaOpcional(produto.getUnidadeMedida());
            String categoriaId = escolherCategoriaOpcional(produto.getCategoriaId());
            String fornecedorId = escolherFornecedorOpcional(produto.getFornecedorId());

            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setEstoqueMinimo(estoqueMinimo);
            produto.setUnidadeMedida(unidade);
            produto.setCategoriaId(categoriaId);
            produto.setFornecedorId(fornecedorId);

            produtoRepository.editar(produto);
            System.out.println("Produto atualizado com sucesso!");
        } catch (EntidadeNaoEncontradaException | ValidacaoException e) {
            System.out.println("Erro ao editar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
        }
    }

    private void excluirProduto() {
        try {
            String id = lerTexto("ID do produto: ");
            produtoRepository.excluir(id);
            System.out.println("Produto excluído com sucesso!");
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }

    private void consultarProduto() {
        try {
            String id = lerTexto("ID do produto: ");
            Produto produto = produtoRepository.consultar(id);
            exibirDetalhesProduto(produto);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarProdutos() {
        List<Produto> produtos = produtoRepository.listarTodos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("\n--- Lista de Produtos ---");
        for (Produto p : produtos) {
            exibirDetalhesProduto(p);
            if (p.necessitaAlerta()) {
                System.out.println("  >> " + p.gerarAlerta());
            }
            System.out.println();
        }
    }

    private void exibirDetalhesProduto(Produto produto) {
        String catNome = obterNomeCategoria(produto.getCategoriaId());
        String forNome = obterNomeFornecedor(produto.getFornecedorId());
        System.out.printf(
                "ID: %s | %s | Preço: R$ %.2f | Estoque: %d %s | Mín: %d | Cat: %s | Forn: %s%n",
                produto.getId(), produto.getNome(), produto.getPreco(),
                produto.getQuantidadeEstoque(), produto.getUnidadeMedida(),
                produto.getEstoqueMinimo(), catNome, forNome
        );
    }

    // ===================== CATEGORIAS =====================

    private void menuCategorias() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- CATEGORIAS ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Editar");
            System.out.println("3 - Excluir");
            System.out.println("4 - Listar todas");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> cadastrarCategoria();
                case 2 -> editarCategoria();
                case 3 -> excluirCategoria();
                case 4 -> listarCategorias();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarCategoria() {
        try {
            String nome = lerTexto("Nome: ");
            String descricao = lerTexto("Descrição: ");
            Categoria categoria = new Categoria(GeradorId.gerarIdCategoria(), nome, descricao);
            categoriaRepository.cadastrar(categoria);
            System.out.println("Categoria cadastrada com sucesso! ID: " + categoria.id());
        } catch (EntidadeDuplicadaException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private void editarCategoria() {
        try {
            String id = lerTexto("ID da categoria: ");
            Categoria atual = categoriaRepository.consultar(id);
            String nome = lerTextoOpcional("Novo nome (Enter para manter): ", atual.nome());
            String descricao = lerTextoOpcional("Nova descrição (Enter para manter): ", atual.descricao());
            Categoria atualizada = new Categoria(id, nome, descricao);
            categoriaRepository.editar(atualizada);
            System.out.println("Categoria atualizada com sucesso!");
        } catch (EntidadeNaoEncontradaException | ValidacaoException e) {
            System.out.println("Erro ao editar: " + e.getMessage());
        }
    }

    private void excluirCategoria() {
        try {
            String id = lerTexto("ID da categoria: ");
            categoriaRepository.excluir(id);
            System.out.println("Categoria excluída com sucesso!");
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }

    private void listarCategorias() {
        List<Categoria> categorias = categoriaRepository.listarTodos();
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return;
        }
        System.out.println("\n--- Lista de Categorias ---");
        for (Categoria c : categorias) {
            System.out.printf("ID: %s | Nome: %s | Descrição: %s%n", c.id(), c.nome(), c.descricao());
        }
    }

    // ===================== FORNECEDORES =====================

    private void menuFornecedores() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- FORNECEDORES ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Editar");
            System.out.println("3 - Excluir");
            System.out.println("4 - Listar todos");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> cadastrarFornecedor();
                case 2 -> editarFornecedor();
                case 3 -> excluirFornecedor();
                case 4 -> listarFornecedores();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarFornecedor() {
        try {
            String nome = lerTexto("Nome: ");
            String cnpj = lerTexto("CNPJ: ");
            String email = lerTexto("E-mail: ");
            String telefone = lerTexto("Telefone: ");
            Fornecedor fornecedor = new Fornecedor(
                    GeradorId.gerarIdFornecedor(), nome, cnpj, email, telefone
            );
            fornecedorRepository.cadastrar(fornecedor);
            System.out.println("Fornecedor cadastrado com sucesso! ID: " + fornecedor.getId());
        } catch (EntidadeDuplicadaException | ValidacaoException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private void editarFornecedor() {
        try {
            String id = lerTexto("ID do fornecedor: ");
            Fornecedor fornecedor = fornecedorRepository.consultar(id);
            fornecedor.setNome(lerTextoOpcional("Novo nome (Enter para manter): ", fornecedor.getNome()));
            fornecedor.setCnpj(lerTextoOpcional("Novo CNPJ (Enter para manter): ", fornecedor.getCnpj()));
            fornecedor.setEmail(lerTextoOpcional("Novo e-mail (Enter para manter): ", fornecedor.getEmail()));
            fornecedor.setTelefone(lerTextoOpcional("Novo telefone (Enter para manter): ", fornecedor.getTelefone()));
            fornecedorRepository.editar(fornecedor);
            System.out.println("Fornecedor atualizado com sucesso!");
        } catch (EntidadeNaoEncontradaException | ValidacaoException e) {
            System.out.println("Erro ao editar: " + e.getMessage());
        }
    }

    private void excluirFornecedor() {
        try {
            String id = lerTexto("ID do fornecedor: ");
            fornecedorRepository.excluir(id);
            System.out.println("Fornecedor excluído com sucesso!");
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }

    private void listarFornecedores() {
        List<Fornecedor> fornecedores = fornecedorRepository.listarTodos();
        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor cadastrado.");
            return;
        }
        System.out.println("\n--- Lista de Fornecedores ---");
        for (Fornecedor f : fornecedores) {
            System.out.printf("ID: %s | %s | CNPJ: %s | E-mail: %s | Tel: %s%n",
                    f.getId(), f.getNome(), f.getCnpj(), f.getEmail(), f.getTelefone());
        }
    }

    // ===================== MOVIMENTAÇÕES =====================

    private void menuMovimentacoes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- MOVIMENTAÇÕES DE ESTOQUE ---");
            System.out.println("1 - Registrar entrada");
            System.out.println("2 - Registrar saída");
            System.out.println("3 - Listar histórico de um produto");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> registrarEntrada();
                case 2 -> registrarSaida();
                case 3 -> listarHistoricoProduto();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void registrarEntrada() {
        try {
            if (produtoRepository.listarTodos().isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }
            String produtoId = escolherProduto();
            int quantidade = lerInteiroPositivo("Quantidade de entrada: ");
            LocalDate data = lerData("Data (AAAA-MM-DD): ");

            Entrada entrada = new Entrada(
                    GeradorId.gerarIdMovimentacao(), produtoId, quantidade, data
            );
            estoqueService.registrarMovimentacao(entrada);
            System.out.println("Entrada registrada com sucesso! ID: " + entrada.getId());
        } catch (EntidadeNaoEncontradaException | EstoqueInsuficienteException
                 | ValidacaoException | EntidadeDuplicadaException e) {
            System.out.println("Erro ao registrar entrada: " + e.getMessage());
        }
    }

    private void registrarSaida() {
        try {
            if (produtoRepository.listarTodos().isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }
            String produtoId = escolherProduto();
            int quantidade = lerInteiroPositivo("Quantidade de saída: ");
            LocalDate data = lerData("Data (AAAA-MM-DD): ");

            Saida saida = new Saida(
                    GeradorId.gerarIdMovimentacao(), produtoId, quantidade, data
            );
            estoqueService.registrarMovimentacao(saida);
            System.out.println("Saída registrada com sucesso! ID: " + saida.getId());
        } catch (EntidadeNaoEncontradaException | EstoqueInsuficienteException
                 | ValidacaoException | EntidadeDuplicadaException e) {
            System.out.println("Erro ao registrar saída: " + e.getMessage());
        }
    }

    private void listarHistoricoProduto() {
        try {
            if (produtoRepository.listarTodos().isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }
            String produtoId = escolherProduto();
            List<String> linhas = relatorioService.historicoMovimentacoesPorProduto(produtoId);
            linhas.forEach(System.out::println);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ===================== RELATÓRIOS =====================

    private void menuRelatorios() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- RELATÓRIOS ---");
            System.out.println("1 - Listagem geral de produtos");
            System.out.println("2 - Produtos com estoque abaixo do mínimo");
            System.out.println("3 - Valor total do estoque");
            System.out.println("4 - Histórico por produto");
            System.out.println("5 - Histórico por período");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> relatorioProdutosGeral();
                case 2 -> relatorioAlertas();
                case 3 -> relatorioValorTotal();
                case 4 -> relatorioHistoricoProduto();
                case 5 -> relatorioHistoricoPeriodo();
                case 0 -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void relatorioProdutosGeral() {
        List<String> linhas = relatorioService.listarProdutosGeral();
        if (linhas.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("\n--- Relatório Geral de Produtos ---");
        linhas.forEach(System.out::println);
    }

    private void relatorioAlertas() {
        List<String> alertas = relatorioService.listarProdutosComAlerta();
        if (alertas.isEmpty()) {
            System.out.println("Nenhum produto com estoque abaixo do mínimo.");
            return;
        }
        System.out.println("\n--- Produtos com Alerta de Estoque ---");
        alertas.forEach(System.out::println);
    }

    private void relatorioValorTotal() {
        double total = relatorioService.calcularValorTotalEstoque();
        System.out.printf("%nValor total do estoque: R$ %.2f%n", total);
    }

    private void relatorioHistoricoProduto() {
        try {
            if (produtoRepository.listarTodos().isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }
            String produtoId = escolherProduto();
            relatorioService.historicoMovimentacoesPorProduto(produtoId)
                    .forEach(System.out::println);
        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void relatorioHistoricoPeriodo() {
        try {
            LocalDate inicio = lerData("Data inicial (AAAA-MM-DD): ");
            LocalDate fim = lerData("Data final (AAAA-MM-DD): ");
            if (fim.isBefore(inicio)) {
                System.out.println("Data final não pode ser anterior à data inicial.");
                return;
            }
            relatorioService.historicoMovimentacoesPorPeriodo(inicio, fim)
                    .forEach(System.out::println);
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ===================== SELEÇÃO DE ENTIDADES =====================

    private String escolherCategoria() {
        listarCategorias();
        return lerTexto("ID da categoria: ");
    }

    private String escolherCategoriaOpcional(String atual) {
        listarCategorias();
        return lerTextoOpcional("ID da categoria (Enter para manter " + atual + "): ", atual);
    }

    private String escolherFornecedor() {
        listarFornecedores();
        return lerTexto("ID do fornecedor: ");
    }

    private String escolherFornecedorOpcional(String atual) {
        listarFornecedores();
        return lerTextoOpcional("ID do fornecedor (Enter para manter " + atual + "): ", atual);
    }

    private String escolherProduto() {
        listarProdutos();
        return lerTexto("ID do produto: ");
    }

    private UnidadeMedida lerUnidadeMedida() {
        UnidadeMedida[] valores = UnidadeMedida.values();
        System.out.println("Unidades de medida:");
        for (int i = 0; i < valores.length; i++) {
            System.out.printf("%d - %s%n", i + 1, valores[i]);
        }
        int opcao = lerInteiro("Escolha a unidade: ");
        if (opcao < 1 || opcao > valores.length) {
            System.out.println("Opção inválida. Usando UNIDADE como padrão.");
            return UnidadeMedida.UNIDADE;
        }
        return valores[opcao - 1];
    }

    private UnidadeMedida lerUnidadeMedidaOpcional(UnidadeMedida atual) {
        System.out.print("Alterar unidade? (S/N): ");
        String resposta = scanner.nextLine().trim();
        if (resposta.equalsIgnoreCase("S")) {
            return lerUnidadeMedida();
        }
        return atual;
    }

    private String obterNomeCategoria(String id) {
        try {
            return categoriaRepository.consultar(id).nome();
        } catch (EntidadeNaoEncontradaException e) {
            return "N/A";
        }
    }

    private String obterNomeFornecedor(String id) {
        try {
            return fornecedorRepository.consultar(id).getNome();
        } catch (EntidadeNaoEncontradaException e) {
            return "N/A";
        }
    }

    // ===================== LEITURA SEGURA DE ENTRADAS =====================

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    private int lerInteiroPositivo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor > 0) {
                return valor;
            }
            System.out.println("O valor deve ser maior que zero.");
        }
    }

    private int lerInteiroNaoNegativo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor >= 0) {
                return valor;
            }
            System.out.println("O valor não pode ser negativo.");
        }
    }

    private int lerInteiroOpcional(String mensagem, int padrao) {
        System.out.print(mensagem);
        String linha = scanner.nextLine().trim();
        if (linha.isEmpty()) {
            return padrao;
        }
        try {
            int valor = Integer.parseInt(linha);
            if (valor < 0) {
                System.out.println("Valor negativo ignorado. Mantendo valor atual.");
                return padrao;
            }
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("Número inválido. Mantendo valor atual.");
            return padrao;
        }
    }

    private double lerDoublePositivo(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                double valor = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
                if (valor > 0) {
                    return valor;
                }
                System.out.println("O valor deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número válido.");
            }
        }
    }

    private double lerDoubleOpcional(String mensagem, double padrao) {
        System.out.print(mensagem);
        String linha = scanner.nextLine().trim();
        if (linha.isEmpty()) {
            return padrao;
        }
        try {
            double valor = Double.parseDouble(linha.replace(',', '.'));
            if (valor < 0) {
                System.out.println("Valor negativo ignorado. Mantendo valor atual.");
                return padrao;
            }
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("Número inválido. Mantendo valor atual.");
            return padrao;
        }
    }

    private String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("O campo não pode ser vazio.");
        }
    }

    private String lerTextoOpcional(String mensagem, String padrao) {
        System.out.print(mensagem);
        String valor = scanner.nextLine().trim();
        return valor.isEmpty() ? padrao : valor;
    }

    private LocalDate lerData(String mensagem) throws ValidacaoException {
        while (true) {
            System.out.print(mensagem);
            String texto = scanner.nextLine().trim();
            try {
                LocalDate data = LocalDate.parse(texto);
                if (data.isAfter(LocalDate.now())) {
                    System.out.println("Data não pode ser futura.");
                    continue;
                }
                return data;
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use AAAA-MM-DD.");
            }
        }
    }
}
