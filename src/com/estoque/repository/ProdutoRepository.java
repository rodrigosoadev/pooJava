package com.estoque.repository;

import com.estoque.model.Produto;

import java.util.Comparator;
import java.util.List;

/**
 * Repositório especializado para produtos, com listagem ordenada por nome.
 */
public class ProdutoRepository extends Repositorio<Produto> {

    public ProdutoRepository() {
        super("Produto");
    }

    /**
     * Retorna produtos ordenados alfabeticamente por nome.
     */
    @Override
    public List<Produto> listarTodos() {
        return super.listarTodos().stream()
                .sorted(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
