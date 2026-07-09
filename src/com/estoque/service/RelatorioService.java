package com.estoque.service;

import com.estoque.exceptions.EntidadeNaoEncontradaException;
import com.estoque.interfaces.Alertavel;
import com.estoque.model.Categoria;
import com.estoque.model.Fornecedor;
import com.estoque.model.Movimentacao;
import com.estoque.model.Produto;
import com.estoque.repository.CategoriaRepository;
import com.estoque.repository.FornecedorRepository;
import com.estoque.repository.MovimentacaoRepository;
import com.estoque.repository.ProdutoRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela geração de relatórios e listagens do sistema de estoque.
 */
public class RelatorioService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public RelatorioService(ProdutoRepository produtoRepository,
                            CategoriaRepository categoriaRepository,
                            FornecedorRepository fornecedorRepository,
                            MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Gera linhas de relatório com dados completos de cada produto.
     *
     * @return listagem formatada de produtos com categoria, fornecedor, quantidade e preço
     */
    public List<String> listarProdutosGeral() {
        List<String> linhas = new ArrayList<>();
        for (Produto produto : produtoRepository.listarTodos()) {
            String categoriaNome = obterNomeCategoria(produto.getCategoriaId());
            String fornecedorNome = obterNomeFornecedor(produto.getFornecedorId());
            linhas.add(String.format(
                    "ID: %s | Nome: %s | Categoria: %s | Fornecedor: %s | Estoque: %d %s | Preço: R$ %.2f",
                    produto.getId(), produto.getNome(), categoriaNome, fornecedorNome,
                    produto.getQuantidadeEstoque(), produto.getUnidadeMedida(), produto.getPreco()
            ));
        }
        return linhas;
    }

    /**
     * Lista alertas de produtos com estoque no ou abaixo do mínimo configurado.
     *
     * @return mensagens de alerta geradas via interface {@link Alertavel}
     */
    public List<String> listarProdutosComAlerta() {
        return produtoRepository.listarTodos().stream()
                .filter(Alertavel::necessitaAlerta)
                .map(Alertavel::gerarAlerta)
                .collect(Collectors.toList());
    }

    /**
     * Calcula o valor total do estoque (soma de preço × quantidade de todos os produtos).
     *
     * @return valor monetário total
     */
    public double calcularValorTotalEstoque() {
        return produtoRepository.listarTodos().stream()
                .mapToDouble(p -> p.getPreco() * p.getQuantidadeEstoque())
                .sum();
    }

    /**
     * Retorna o histórico de movimentações de um produto.
     *
     * @param produtoId identificador do produto
     * @return linhas formatadas do histórico
     * @throws EntidadeNaoEncontradaException se o produto não existir
     */
    public List<String> historicoMovimentacoesPorProduto(String produtoId)
            throws EntidadeNaoEncontradaException {
        Produto produto = produtoRepository.consultar(produtoId);
        List<Movimentacao> movimentacoes = movimentacaoRepository.listarPorProduto(produtoId);
        return formatarMovimentacoes(produto.getNome(), movimentacoes);
    }

    /**
     * Retorna movimentações dentro de um intervalo de datas (inclusive).
     *
     * @param dataInicio data inicial
     * @param dataFim    data final
     * @return linhas formatadas das movimentações no período
     */
    public List<String> historicoMovimentacoesPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Movimentacao> movimentacoes = movimentacaoRepository.listarTodos().stream()
                .filter(m -> !m.getData().isBefore(dataInicio) && !m.getData().isAfter(dataFim))
                .collect(Collectors.toList());
        return formatarMovimentacoes("Todos os produtos", movimentacoes);
    }

    private List<String> formatarMovimentacoes(String titulo, List<Movimentacao> movimentacoes) {
        List<String> linhas = new ArrayList<>();
        linhas.add("Histórico — " + titulo);
        if (movimentacoes.isEmpty()) {
            linhas.add("  Nenhuma movimentação encontrada.");
            return linhas;
        }
        for (Movimentacao mov : movimentacoes) {
            String nomeProduto = obterNomeProduto(mov.getProdutoId());
            linhas.add(String.format(
                    "  [%s] %s | Produto: %s (%s) | Qtd: %d | Data: %s",
                    mov.getId(), mov.getTipoDescricao(), nomeProduto, mov.getProdutoId(),
                    mov.getQuantidade(), mov.getData()
            ));
        }
        return linhas;
    }

    private String obterNomeCategoria(String categoriaId) {
        try {
            Categoria categoria = categoriaRepository.consultar(categoriaId);
            return categoria.nome();
        } catch (EntidadeNaoEncontradaException e) {
            return "N/A";
        }
    }

    private String obterNomeFornecedor(String fornecedorId) {
        try {
            Fornecedor fornecedor = fornecedorRepository.consultar(fornecedorId);
            return fornecedor.getNome();
        } catch (EntidadeNaoEncontradaException e) {
            return "N/A";
        }
    }

    private String obterNomeProduto(String produtoId) {
        try {
            return produtoRepository.consultar(produtoId).getNome();
        } catch (EntidadeNaoEncontradaException e) {
            return "Desconhecido";
        }
    }
}
