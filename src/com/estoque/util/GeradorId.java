package com.estoque.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Gera identificadores sequenciais por tipo de entidade no formato PREFIXO + número com zeros à esquerda.
 * Exemplo: PRD001, FOR002, CAT001, MOV015.
 */
public final class GeradorId {

    private static final Map<String, Integer> contadores = new HashMap<>();

    private GeradorId() {
    }

    /**
     * Gera o próximo ID para o prefixo informado.
     *
     * @param prefixo prefixo do tipo de entidade (ex.: PRD, FOR, CAT, MOV)
     * @return identificador gerado (ex.: PRD001)
     */
    public static synchronized String gerar(String prefixo) {
        int sequencial = contadores.getOrDefault(prefixo, 0) + 1;
        contadores.put(prefixo, sequencial);
        return String.format("%s%03d", prefixo, sequencial);
    }

    /**
     * @return próximo ID de produto (ex.: PRD001)
     */
    public static String gerarIdProduto() {
        return gerar("PRD");
    }

    /**
     * @return próximo ID de fornecedor (ex.: FOR001)
     */
    public static String gerarIdFornecedor() {
        return gerar("FOR");
    }

    /**
     * @return próximo ID de categoria (ex.: CAT001)
     */
    public static String gerarIdCategoria() {
        return gerar("CAT");
    }

    /**
     * @return próximo ID de movimentação (ex.: MOV001)
     */
    public static String gerarIdMovimentacao() {
        return gerar("MOV");
    }
}
