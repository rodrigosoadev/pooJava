package com.estoque.util;

import com.estoque.exceptions.ValidacaoException;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utilitário com métodos estáticos de validação de CNPJ, e-mail, datas e valores numéricos.
 */
public final class Validador {

    private static final Pattern PADRAO_EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private Validador() {
    }

    /**
     * Valida o formato e os dígitos verificadores de um CNPJ.
     *
     * @param cnpj CNPJ com ou sem formatação
     * @throws ValidacaoException se o CNPJ for inválido
     */
    public static void validarCnpj(String cnpj) throws ValidacaoException {
        if (cnpj == null || cnpj.isBlank()) {
            throw new ValidacaoException("CNPJ não pode ser vazio.");
        }

        String numeros = cnpj.replaceAll("\\D", "");
        if (numeros.length() != 14) {
            throw new ValidacaoException("CNPJ deve conter 14 dígitos.");
        }

        if (numeros.chars().distinct().count() == 1) {
            throw new ValidacaoException("CNPJ inválido: todos os dígitos são iguais.");
        }

        int[] pesosPrimeiro = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesosSegundo = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int primeiroDigito = calcularDigitoVerificador(numeros.substring(0, 12), pesosPrimeiro);
        int segundoDigito = calcularDigitoVerificador(numeros.substring(0, 13), pesosSegundo);

        if (primeiroDigito != Character.getNumericValue(numeros.charAt(12))
                || segundoDigito != Character.getNumericValue(numeros.charAt(13))) {
            throw new ValidacaoException("CNPJ inválido: dígitos verificadores incorretos.");
        }
    }

    private static int calcularDigitoVerificador(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /**
     * Valida o formato de um endereço de e-mail.
     *
     * @param email e-mail a validar
     * @throws ValidacaoException se o formato for inválido
     */
    public static void validarEmail(String email) throws ValidacaoException {
        if (email == null || email.isBlank()) {
            throw new ValidacaoException("E-mail não pode ser vazio.");
        }
        if (!PADRAO_EMAIL.matcher(email.trim()).matches()) {
            throw new ValidacaoException("E-mail em formato inválido.");
        }
    }

    /**
     * Garante que a data informada não seja posterior à data atual.
     *
     * @param data data da movimentação
     * @throws ValidacaoException se a data for futura
     */
    public static void validarDataNaoFutura(LocalDate data) throws ValidacaoException {
        if (data == null) {
            throw new ValidacaoException("Data não pode ser nula.");
        }
        if (data.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data da movimentação não pode ser futura.");
        }
    }

    /**
     * Valida que um valor numérico é estritamente positivo.
     *
     * @param valor   valor a validar
     * @param campo   nome do campo para mensagem de erro
     * @throws ValidacaoException se o valor for menor ou igual a zero
     */
    public static void validarPositivo(double valor, String campo) throws ValidacaoException {
        if (valor <= 0) {
            throw new ValidacaoException(campo + " deve ser maior que zero.");
        }
    }

    /**
     * Valida que um valor inteiro é estritamente positivo.
     *
     * @param valor   valor a validar
     * @param campo   nome do campo para mensagem de erro
     * @throws ValidacaoException se o valor for menor ou igual a zero
     */
    public static void validarPositivo(int valor, String campo) throws ValidacaoException {
        if (valor <= 0) {
            throw new ValidacaoException(campo + " deve ser maior que zero.");
        }
    }

    /**
     * Valida que um valor numérico não é negativo (permite zero).
     *
     * @param valor   valor a validar
     * @param campo   nome do campo para mensagem de erro
     * @throws ValidacaoException se o valor for negativo
     */
    public static void validarNaoNegativo(double valor, String campo) throws ValidacaoException {
        if (valor < 0) {
            throw new ValidacaoException(campo + " não pode ser negativo.");
        }
    }

    /**
     * Valida que um valor inteiro não é negativo (permite zero).
     *
     * @param valor   valor a validar
     * @param campo   nome do campo para mensagem de erro
     * @throws ValidacaoException se o valor for negativo
     */
    public static void validarNaoNegativo(int valor, String campo) throws ValidacaoException {
        if (valor < 0) {
            throw new ValidacaoException(campo + " não pode ser negativo.");
        }
    }
}
