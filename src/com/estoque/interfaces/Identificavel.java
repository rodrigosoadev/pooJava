package com.estoque.interfaces;

/**
 * Contrato para entidades que possuem identificador único.
 * Utilizado pelo repositório genérico para indexação em {@code HashMap}.
 */
public interface Identificavel {

    /**
     * @return identificador único da entidade
     */
    String getId();
}
