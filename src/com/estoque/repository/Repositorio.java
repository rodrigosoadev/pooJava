package com.estoque.repository;

import com.estoque.exceptions.EntidadeDuplicadaException;
import com.estoque.exceptions.EntidadeNaoEncontradaException;
import com.estoque.exceptions.ValidacaoException;
import com.estoque.interfaces.Cadastravel;
import com.estoque.interfaces.Identificavel;
import com.estoque.interfaces.Validavel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório genérico em memória baseado em HashMap
 *
 * @param <T> tipo da entidade, deve implementar Identificavel
 */
public abstract class Repositorio<T extends Identificavel> implements Cadastravel<T> {

    private final Map<String, T> entidades = new HashMap<>();
    private final String nomeEntidade;

    protected Repositorio(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    /**
     * Valida a entidade antes de persistir, quando ela implementa Validavel
     *
     * @param entidade entidade a validar
     * @throws ValidacaoException se a validação falhar
     */
    protected void validarEntidade(T entidade) throws ValidacaoException {
        if (entidade instanceof Validavel validavel) {
            validavel.validar();
        }
    }

    @Override
    public void cadastrar(T entidade) throws EntidadeDuplicadaException, ValidacaoException {
        validarEntidade(entidade);
        if (entidades.containsKey(entidade.getId())) {
            throw new EntidadeDuplicadaException(
                    nomeEntidade + " com ID '" + entidade.getId() + "' já está cadastrado(a)."
            );
        }
        entidades.put(entidade.getId(), entidade);
    }

    @Override
    public void editar(T entidade) throws EntidadeNaoEncontradaException, ValidacaoException {
        validarEntidade(entidade);
        if (!entidades.containsKey(entidade.getId())) {
            throw new EntidadeNaoEncontradaException(
                    nomeEntidade + " com ID '" + entidade.getId() + "' não encontrado(a)."
            );
        }
        entidades.put(entidade.getId(), entidade);
    }

    @Override
    public void excluir(String id) throws EntidadeNaoEncontradaException {
        if (!entidades.containsKey(id)) {
            throw new EntidadeNaoEncontradaException(
                    nomeEntidade + " com ID '" + id + "' não encontrado(a)."
            );
        }
        entidades.remove(id);
    }

    @Override
    public T consultar(String id) throws EntidadeNaoEncontradaException {
        T entidade = entidades.get(id);
        if (entidade == null) {
            throw new EntidadeNaoEncontradaException(
                    nomeEntidade + " com ID '" + id + "' não encontrado(a)."
            );
        }
        return entidade;
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(entidades.values());
    }

    /**
     * @return mapa interno de entidades (uso restrito a subclasses quando necessário)
     */
    protected Map<String, T> getEntidades() {
        return entidades;
    }
}
