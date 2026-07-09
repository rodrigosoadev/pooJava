package com.estoque.exceptions;

/**
 * Exceção lançada ao tentar cadastrar uma entidade com ID já existente.
 */
public class EntidadeDuplicadaException extends Exception {

    public EntidadeDuplicadaException(String mensagem) {
        super(mensagem);
    }
}
