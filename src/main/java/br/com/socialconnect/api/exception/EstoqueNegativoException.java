package br.com.socialconnect.api.exception;
public class EstoqueNegativoException extends RuntimeException {
    public EstoqueNegativoException() { super("O estoque nao pode ser negativo."); }
}
