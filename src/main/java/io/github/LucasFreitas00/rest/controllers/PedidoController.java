package io.github.LucasFreitas00.rest.controllers;

import io.github.LucasFreitas00.rest.dto.PedidoDTO;
import io.github.LucasFreitas00.domain.entities.ItemPedido;
import io.github.LucasFreitas00.domain.entities.Pedido;
import io.github.LucasFreitas00.domain.enums.StatusPedido;
import io.github.LucasFreitas00.domain.services.PedidoService;
import io.github.LucasFreitas00.rest.dto.AtualizacaoStatusPedidoDTO;
import io.github.LucasFreitas00.rest.dto.InformacaoItemPedidoDTO;
import io.github.LucasFreitas00.rest.dto.InformacoesPedidoDTO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@Api("Api Pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Cadastrar um pedido")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Pedido cadastrado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
    })
    public Integer save(@RequestBody @Valid @ApiParam(value = "Dados do pedido a ser cadastrado") PedidoDTO dto) {
        Pedido pedido = service.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("{id}")
    @ApiOperation("Obter dados de um pedido")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Pedido encontrado"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Pedido não encontrado para o ID informado")
    })
    public InformacoesPedidoDTO getById(@PathVariable @ApiParam(value = "ID do pedido", example = "1") Integer id) {
        return service
                .obterPedidoCompleto(id)
                .map(p -> converter(p))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Atualizar status de um pedido")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Pedido atualizado com sucesso"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Pedido não encontrado para o ID informado")
    })
    public void updateStatus(@PathVariable @ApiParam(value = "ID do pedido", example = "1") Integer id,
                             @RequestBody @ApiParam(value = "Novo status do pedido", example = "CANCELADO") AtualizacaoStatusPedidoDTO dto) {
        service.atualizaStatus(id, StatusPedido.valueOf(dto.getNovoStatus()));
    }

    private InformacoesPedidoDTO converter(Pedido pedido) {
        return InformacoesPedidoDTO
                .builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .status(pedido.getStatus().name())
                .itens(converter(pedido.getItens()))
                .build();
    }

    private List<InformacaoItemPedidoDTO> converter(List<ItemPedido> itens) {
        if (itens.isEmpty()) {
            return Collections.emptyList();
        }
        return itens.stream().map(
                item -> InformacaoItemPedidoDTO
                        .builder()
                        .descricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPreco())
                        .quantidade(item.getQuantidade())
                        .build()
        ).collect(Collectors.toList());
    }
}
