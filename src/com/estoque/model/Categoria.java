package com.estoque.model;

import com.estoque.interfaces.Identificavel;

/**

 * @param id        identificador único da categoria
 * @param nome      nome da categoria
 * @param descricao descrição da categoria
 */
public record Categoria(String id, String nome, String descricao) implements Identificavel {

    @Override
    public String getId() {
        return id;
    }
}
