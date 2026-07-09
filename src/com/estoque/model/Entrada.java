package com.estoque.model;

import com.estoque.enums.TipoMovimentacao;

import java.time.LocalDate;

/**
 * Movimentação de entrada de produtos no estoque.
 * Ao processar, soma a quantidade informada ao estoque do produto.
 */
public class Entrada extends Movimentacao {

    public Entrada() {
    }

    public Entrada(String id, String produtoId, int quantidade, LocalDate data) {
        super(id, produtoId, quantidade, data);
    }

    @Override
    public TipoMovimentacao getTipo() {
        return TipoMovimentacao.ENTRADA;
    }

    @Override
    public String getTipoDescricao() {
        return "ENTRADA";
    }

    /**
     * Soma a quantidade da entrada ao estoque do produto.
     *
     * @param produto produto que receberá a entrada
     */
    @Override
    public void processar(Produto produto) {
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + getQuantidade());
    }
}
