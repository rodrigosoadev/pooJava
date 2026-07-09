package com.estoque.model;

import com.estoque.enums.UnidadeMedida;
import com.estoque.exceptions.ValidacaoException;
import com.estoque.interfaces.Alertavel;
import com.estoque.interfaces.Identificavel;
import com.estoque.interfaces.Validavel;
import com.estoque.util.Validador;

/**
 * Representa um produto controlado pelo estoque.
 * Implementa alertas de estoque mínimo e validação de preço e quantidades.
 */
public class Produto implements Identificavel, Alertavel, Validavel {

    private String id;
    private String nome;
    private String descricao;
    private double preco;
    private int quantidadeEstoque;
    private int estoqueMinimo;
    private UnidadeMedida unidadeMedida;
    private String categoriaId;
    private String fornecedorId;

    public Produto() {
    }

    public Produto(String id, String nome, String descricao, double preco, int quantidadeEstoque,
                   int estoqueMinimo, UnidadeMedida unidadeMedida, String categoriaId, String fornecedorId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        setPreco(preco);
        setQuantidadeEstoque(quantidadeEstoque);
        setEstoqueMinimo(estoqueMinimo);
        this.unidadeMedida = unidadeMedida;
        this.categoriaId = categoriaId;
        this.fornecedorId = fornecedorId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        }
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("Quantidade em estoque não pode ser negativa.");
        }
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        if (estoqueMinimo < 0) {
            throw new IllegalArgumentException("Estoque mínimo não pode ser negativo.");
        }
        this.estoqueMinimo = estoqueMinimo;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getFornecedorId() {
        return fornecedorId;
    }

    public void setFornecedorId(String fornecedorId) {
        this.fornecedorId = fornecedorId;
    }

    /**
     * {@inheritDoc}
     * Retorna {@code true} quando a quantidade em estoque é menor ou igual ao estoque mínimo.
     */
    @Override
    public boolean necessitaAlerta() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String gerarAlerta() {
        return String.format(
                "ALERTA: Produto '%s' (ID: %s) com estoque %d %s — mínimo configurado: %d.",
                nome, id, quantidadeEstoque, unidadeMedida, estoqueMinimo
        );
    }

    /**
     * Valida preço, quantidades e referências obrigatórias.
     *
     * @throws ValidacaoException se algum dado for inválido
     */
    @Override
    public void validar() throws ValidacaoException {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("Nome do produto é obrigatório.");
        }
        Validador.validarNaoNegativo(preco, "Preço");
        Validador.validarNaoNegativo(quantidadeEstoque, "Quantidade em estoque");
        Validador.validarNaoNegativo(estoqueMinimo, "Estoque mínimo");
        if (unidadeMedida == null) {
            throw new ValidacaoException("Unidade de medida é obrigatória.");
        }
        if (categoriaId == null || categoriaId.isBlank()) {
            throw new ValidacaoException("Categoria do produto é obrigatória.");
        }
        if (fornecedorId == null || fornecedorId.isBlank()) {
            throw new ValidacaoException("Fornecedor do produto é obrigatório.");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Produto{id='%s', nome='%s', preco=%.2f, estoque=%d, minimo=%d, unidade=%s}",
                id, nome, preco, quantidadeEstoque, estoqueMinimo, unidadeMedida
        );
    }
}
