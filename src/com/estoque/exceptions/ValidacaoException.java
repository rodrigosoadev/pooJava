package com.estoque.exceptions;

/**
 * Exceção lançada quando dados de entrada ou entidades possuem valores inválidos.
 */
public class ValidacaoException extends Exception {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
