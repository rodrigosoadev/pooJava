package com.estoque.interfaces;

/**
 * Contrato para entidades que podem gerar alertas de estoque mínimo.
 */
public interface Alertavel {

    /**
     * @return {@code true} quando a quantidade em estoque está no ou abaixo do mínimo configurado
     */
    boolean necessitaAlerta();

    /**
     * @return mensagem formatada descrevendo o alerta de estoque
     */
    String gerarAlerta();
}
