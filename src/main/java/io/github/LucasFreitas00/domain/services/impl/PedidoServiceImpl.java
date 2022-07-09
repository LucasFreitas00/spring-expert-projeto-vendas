package io.github.LucasFreitas00.domain.services.impl;

import io.github.LucasFreitas00.domain.entities.Cliente;
import io.github.LucasFreitas00.domain.services.PedidoService;
import io.github.LucasFreitas00.rest.dto.PedidoDTO;
import io.github.LucasFreitas00.domain.entities.ItemPedido;
import io.github.LucasFreitas00.domain.entities.Pedido;
import io.github.LucasFreitas00.domain.entities.Produto;
import io.github.LucasFreitas00.domain.enums.StatusPedido;
import io.github.LucasFreitas00.domain.repositories.Clientes;
import io.github.LucasFreitas00.domain.repositories.ItensPedido;
import io.github.LucasFreitas00.domain.repositories.Pedidos;
import io.github.LucasFreitas00.domain.repositories.Produtos;
import io.github.LucasFreitas00.exceptions.PedidoNaoEncontradoException;
import io.github.LucasFreitas00.exceptions.RegraNegocioException;
import io.github.LucasFreitas00.rest.dto.ItemPedidoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private Pedidos pedidosRepository;
    @Autowired
    private Clientes clientesRepository;
    @Autowired
    private Produtos produtosRepository;
    @Autowired
    private ItensPedido itensPedidoRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido: " + idCliente));
        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);
        List<ItemPedido> itensPedido = converterItens(pedido, dto.getItens());
        pedidosRepository.save(pedido);
        itensPedidoRepository.saveAll(itensPedido);
        pedido.setItens(itensPedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return pedidosRepository.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        pedidosRepository
                .findById(id)
                .map(p -> {
                    p.setStatus(statusPedido);
                    return pedidosRepository.save(p);
                }).orElseThrow(() -> new PedidoNaoEncontradoException());
    }

    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens) {
        if (itens.isEmpty()) {
            throw new RegraNegocioException("Não é possível realizar um pedido sem itens");
        }
        return itens
                .stream()
                .map(
                        dto -> {
                            Integer idProduto = dto.getProduto();
                            Produto produto = produtosRepository
                                    .findById(dto.getProduto())
                                    .orElseThrow(() -> new RegraNegocioException("Código de produto inválido: " + idProduto));
                            ItemPedido itemPedido = new ItemPedido();
                            itemPedido.setQuantidade(dto.getQuantidade());
                            itemPedido.setPedido(pedido);
                            itemPedido.setProduto(produto);
                            return itemPedido;
                        }
                ).collect(Collectors.toList());
    }
}
