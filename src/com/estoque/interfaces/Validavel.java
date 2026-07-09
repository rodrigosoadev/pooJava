package com.estoque.interfaces;

import com.estoque.exceptions.ValidacaoException;

/**
 * Contrato para entidades que possuem regras de validação de dados.
 */
public interface Validavel {

    /**
     * Valida o estado atual da entidade.
     *
     * @throws ValidacaoException se algum dado estiver inválido
     */
    void validar() throws ValidacaoException;
}
