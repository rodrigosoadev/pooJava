package com.estoque.model;

import com.estoque.enums.TipoMovimentacao;
import com.estoque.exceptions.EstoqueInsuficienteException;

import java.time.LocalDate;

/**
 * Movimentação de saída de produtos do estoque.
 * Valida disponibilidade antes de subtrair a quantidade do produto.
 */
public class Saida extends Movimentacao {

    public Saida() {
    }

    public Saida(String id, String produtoId, int quantidade, LocalDate data) {
        super(id, produtoId, quantidade, data);
    }

    @Override
    public TipoMovimentacao getTipo() {
        return TipoMovimentacao.SAIDA;
    }

    @Override
    public String getTipoDescricao() {
        return "SAIDA";
    }

    /**
     * Subtrai a quantidade da saída do estoque, desde que haja saldo suficiente.
     *
     * @param produto produto que terá a saída registrada
     * @throws EstoqueInsuficienteException se a quantidade solicitada exceder o estoque
     */
    @Override
    public void processar(Produto produto) throws EstoqueInsuficienteException {
        if (getQuantidade() > produto.getQuantidadeEstoque()) {
            throw new EstoqueInsuficienteException(String.format(
                    "Estoque insuficiente para o produto '%s'. Disponível: %d, solicitado: %d.",
                    produto.getNome(), produto.getQuantidadeEstoque(), getQuantidade()
            ));
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - getQuantidade());
    }
}
