package com.estoque.repository;

import com.estoque.model.Fornecedor;

import java.util.Comparator;
import java.util.List;

/**
 * Repositório especializado para fornecedores, com listagem ordenada por nome.
 */
public class FornecedorRepository extends Repositorio<Fornecedor> {

    public FornecedorRepository() {
        super("Fornecedor");
    }

    /**
     * {@inheritDoc}
     * Retorna fornecedores ordenados alfabeticamente por nome.
     */
    @Override
    public List<Fornecedor> listarTodos() {
        return super.listarTodos().stream()
                .sorted(Comparator.comparing(Fornecedor::getNome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
