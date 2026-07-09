package com.estoque.service;

import com.estoque.exceptions.EntidadeDuplicadaException;
import com.estoque.exceptions.EntidadeNaoEncontradaException;
import com.estoque.exceptions.EstoqueInsuficienteException;
import com.estoque.exceptions.ValidacaoException;
import com.estoque.model.Movimentacao;
import com.estoque.model.Produto;
import com.estoque.repository.MovimentacaoRepository;
import com.estoque.repository.ProdutoRepository;

/**
 * Serviço de regras de negócio do estoque.
 * Coordena repositórios e aplica movimentações de forma polimórfica via {@link Movimentacao#processar(Produto)}.
 */
public class EstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EstoqueService(ProdutoRepository produtoRepository, MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Registra uma movimentação de estoque aplicando-a ao produto de forma polimórfica.
     *
     * @param movimentacao entrada ou saída a registrar
     * @throws EntidadeNaoEncontradaException se o produto não existir
     * @throws EstoqueInsuficienteException     se a saída exceder o estoque
     * @throws ValidacaoException             se os dados da movimentação forem inválidos
     * @throws EntidadeDuplicadaException     se o ID da movimentação já existir
     */
    public void registrarMovimentacao(Movimentacao movimentacao)
            throws EntidadeNaoEncontradaException, EstoqueInsuficienteException,
            ValidacaoException, EntidadeDuplicadaException {

        movimentacao.validar();
        Produto produto = produtoRepository.consultar(movimentacao.getProdutoId());

        movimentacao.processar(produto);

        produtoRepository.editar(produto);
        movimentacaoRepository.cadastrar(movimentacao);
    }
}
