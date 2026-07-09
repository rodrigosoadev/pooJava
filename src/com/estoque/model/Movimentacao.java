package com.estoque.model;

import com.estoque.enums.TipoMovimentacao;
import com.estoque.exceptions.EstoqueInsuficienteException;
import com.estoque.exceptions.ValidacaoException;
import com.estoque.interfaces.Identificavel;
import com.estoque.util.Validador;

import java.time.LocalDate;

/**
 * Classe abstrata base para movimentações de estoque (entrada e saída).
 * Define o contrato polimórfico {@link #processar(Produto)} aplicado pelo {@code EstoqueService}.
 */
public abstract class Movimentacao implements Identificavel {

    private String id;
    private String produtoId;
    private int quantidade;
    private LocalDate data;

    protected Movimentacao() {
    }

    protected Movimentacao(String id, String produtoId, int quantidade, LocalDate data) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.data = data;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    /**
     * @return tipo da movimentação (ENTRADA ou SAIDA)
     */
    public abstract TipoMovimentacao getTipo();

    /**
     * @return descrição textual do tipo de movimentação
     */
    public abstract String getTipoDescricao();

    /**
     * @param produto produto alvo da movimentação
     * @throws EstoqueInsuficienteException se a saída exceder o estoque disponível
     */
    public abstract void processar(Produto produto) throws EstoqueInsuficienteException;

    /**
     * Valida dados comuns da movimentação.
     * @throws ValidacaoException se quantidade ou data forem inválidas
     */
    public void validar() throws ValidacaoException {
        if (produtoId == null || produtoId.isBlank()) {
            throw new ValidacaoException("Produto da movimentação é obrigatório.");
        }
        Validador.validarPositivo(quantidade, "Quantidade da movimentação");
        Validador.validarDataNaoFutura(data);
    }

    @Override
    public String toString() {
        return String.format(
                "Movimentacao{id='%s', tipo=%s, produtoId='%s', quantidade=%d, data=%s}",
                id, getTipoDescricao(), produtoId, quantidade, data
        );
    }
}
