package br.com.socialconnect.api.exception;

public class OrdenacaoInvalidaException extends RuntimeException {
    public OrdenacaoInvalidaException() {
        super("Campo de ordenacao invalido. Use idProduto, nome, categoria, estoqueAtual, estoqueMinimo, unidadeMedida ou dataCadastro.");
    }
}