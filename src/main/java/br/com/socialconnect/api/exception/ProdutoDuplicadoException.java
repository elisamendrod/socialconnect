package br.com.socialconnect.api.exception;
public class ProdutoDuplicadoException extends RuntimeException {
    public ProdutoDuplicadoException() { super("Ja existe um produto com esse nome."); }
}
