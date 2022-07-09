package io.github.LucasFreitas00.exceptions;

public class PedidoNaoEncontradoException extends RuntimeException {

    public PedidoNaoEncontradoException() {
        super("Pedido não encontrado.");
    }
}
