package com.estoque.repository;

import com.estoque.model.Categoria;

import java.util.Comparator;
import java.util.List;

/**
 * Repositório especializado para categorias, com listagem ordenada por nome.
 */
public class CategoriaRepository extends Repositorio<Categoria> {

    public CategoriaRepository() {
        super("Categoria");
    }

    /**
     * {@inheritDoc}
     * Retorna categorias ordenadas alfabeticamente por nome.
     */
    @Override
    public List<Categoria> listarTodos() {
        return super.listarTodos().stream()
                .sorted(Comparator.comparing(Categoria::nome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
