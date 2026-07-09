package com.estoque.repository;

import com.estoque.model.Movimentacao;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositório especializado para movimentações de estoque.
 */
public class MovimentacaoRepository extends Repositorio<Movimentacao> {

    public MovimentacaoRepository() {
        super("Movimentação");
    }

    /**
     * {@inheritDoc}
     * Retorna movimentações ordenadas por data (mais recentes primeiro).
     */
    @Override
    public List<Movimentacao> listarTodos() {
        return super.listarTodos().stream()
                .sorted(Comparator.comparing(Movimentacao::getData).reversed()
                        .thenComparing(Movimentacao::getId))
                .toList();
    }

    /**
     * Lista movimentações de um produto específico, ordenadas por data.
     *
     * @param produtoId identificador do produto
     * @return histórico de movimentações do produto
     */
    public List<Movimentacao> listarPorProduto(String produtoId) {
        return super.listarTodos().stream()
                .filter(m -> m.getProdutoId().equals(produtoId))
                .sorted(Comparator.comparing(Movimentacao::getData).reversed()
                        .thenComparing(Movimentacao::getId))
                .collect(Collectors.toList());
    }
}
