package com.estoque.model;

import com.estoque.exceptions.ValidacaoException;
import com.estoque.interfaces.Identificavel;
import com.estoque.interfaces.Validavel;
import com.estoque.util.Validador;

/**
 * Representa um fornecedor de produtos com dados de contato e documento.
 */
public class Fornecedor implements Identificavel, Validavel {

    private String id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;

    public Fornecedor() {
    }

    public Fornecedor(String id, String nome, String cnpj, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Valida CNPJ e e-mail utilizando a classe {@link Validador}.
     *
     * @throws ValidacaoException se CNPJ, e-mail ou nome forem inválidos
     */
    @Override
    public void validar() throws ValidacaoException {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("Nome do fornecedor é obrigatório.");
        }
        Validador.validarCnpj(cnpj);
        Validador.validarEmail(email);
        if (telefone == null || telefone.isBlank()) {
            throw new ValidacaoException("Telefone do fornecedor é obrigatório.");
        }
    }

    @Override
    public String toString() {
        return String.format("Fornecedor{id='%s', nome='%s', cnpj='%s', email='%s', telefone='%s'}",
                id, nome, cnpj, email, telefone);
    }
}
