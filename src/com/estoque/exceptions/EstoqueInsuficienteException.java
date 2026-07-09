package com.estoque.exceptions;

/**
 * Exceção lançada quando uma saída de estoque excede a quantidade disponível.
 */
public class EstoqueInsuficienteException extends Exception {

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
