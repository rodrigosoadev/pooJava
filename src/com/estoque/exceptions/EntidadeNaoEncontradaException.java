package com.estoque.exceptions;

/**
 * Exceção lançada quando uma entidade solicitada não é encontrada no repositório.
 */
public class EntidadeNaoEncontradaException extends Exception {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
