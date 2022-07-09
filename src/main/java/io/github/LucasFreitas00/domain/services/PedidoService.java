package io.github.LucasFreitas00.domain.services;

import io.github.LucasFreitas00.rest.dto.PedidoDTO;
import io.github.LucasFreitas00.domain.entities.Pedido;
import io.github.LucasFreitas00.domain.enums.StatusPedido;

import java.util.Optional;

public interface PedidoService {

    Pedido salvar(PedidoDTO dto);

    Optional<Pedido> obterPedidoCompleto(Integer id);

    void atualizaStatus(Integer id, StatusPedido statusPedido);
}
