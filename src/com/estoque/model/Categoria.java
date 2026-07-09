package com.estoque.model;

import com.estoque.interfaces.Identificavel;

/**
 * Representa uma categoria de produtos.
 * Implementada como {@code record} imutável com identificador, nome e descrição.
 *
 * @param id        identificador único da categoria
 * @param nome      nome da categoria
 * @param descricao descrição da categoria
 */
public record Categoria(String id, String nome, String descricao) implements Identificavel {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }
}
