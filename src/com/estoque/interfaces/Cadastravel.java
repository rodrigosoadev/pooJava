package com.estoque.interfaces;

import com.estoque.exceptions.EntidadeDuplicadaException;
import com.estoque.exceptions.EntidadeNaoEncontradaException;
import com.estoque.exceptions.ValidacaoException;

import java.util.List;

/**
 * Contrato genérico de operações CRUD para entidades persistidas em memória.
 *
 * @param <T> tipo da entidade gerenciada
 */
public interface Cadastravel<T> {

    /**
     * Insere uma nova entidade no repositório.
     *
     * @param entidade entidade a ser cadastrada
     * @throws EntidadeDuplicadaException se já existir entidade com o mesmo ID
     * @throws ValidacaoException         se os dados forem inválidos
     */
    void cadastrar(T entidade) throws EntidadeDuplicadaException, ValidacaoException;

    /**
     * Atualiza uma entidade existente.
     *
     * @param entidade entidade com dados atualizados
     * @throws EntidadeNaoEncontradaException se o ID não existir
     * @throws ValidacaoException             se os dados forem inválidos
     */
    void editar(T entidade) throws EntidadeNaoEncontradaException, ValidacaoException;

    /**
     * Remove uma entidade pelo identificador.
     *
     * @param id identificador da entidade
     * @throws EntidadeNaoEncontradaException se o ID não existir
     */
    void excluir(String id) throws EntidadeNaoEncontradaException;

    /**
     * Busca uma entidade pelo identificador.
     *
     * @param id identificador da entidade
     * @return entidade encontrada
     * @throws EntidadeNaoEncontradaException se o ID não existir
     */
    T consultar(String id) throws EntidadeNaoEncontradaException;

    /**
     * @return lista de todas as entidades cadastradas
     */
    List<T> listarTodos();
}
