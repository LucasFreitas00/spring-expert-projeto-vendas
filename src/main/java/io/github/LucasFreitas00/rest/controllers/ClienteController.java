package io.github.LucasFreitas00.rest.controllers;

import io.github.LucasFreitas00.domain.entities.Cliente;
import io.github.LucasFreitas00.domain.repositories.Clientes;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Api("Api Clientes")
public class ClienteController {

    @Autowired
    private Clientes clientesRepository;

    @GetMapping("{id}")
    @ApiOperation("Obter dados de um cliente")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente encontrado"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Cliente não encontrado para o ID informado")
    })
    public Cliente getClienteById(@PathVariable @ApiParam(value = "ID do cliente", example = "1") Integer id) {
        return clientesRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salvar um novo cliente")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cliente salvo com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação")
    })
    public Cliente save(@RequestBody @Valid @ApiParam(value = "Dados do novo cliente") Cliente cliente) {
        return clientesRepository.save(cliente);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Remover um cliente")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cliente removido com sucesso"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Cliente não encontrado para o ID informado")
    })
    public void delete(@PathVariable @ApiParam(value = "ID do cliente", example = "1") Integer id) {
        clientesRepository.findById(id)
                .map(cliente -> {
                    clientesRepository.delete(cliente);
                    return cliente;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"));

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Atualizar dados de um cliente")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cliente atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Cliente não encontrado para o ID informado")
    })
    public void update(@PathVariable @ApiParam(value = "ID do cliente", example = "1") Integer id,
                       @RequestBody @Valid @ApiParam(value = "Novos dados do cliente") Cliente cliente) {
        clientesRepository
                .findById(id)
                .map(clienteExistente -> {
                    cliente.setId(clienteExistente.getId());
                    clientesRepository.save(cliente);
                    return cliente;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente não encontrado"));
    }

    @GetMapping
    @ApiOperation("Obter dados de alguns ou de todos os clientes")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cliente(s) encontrado(s)"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação")
    })
    public List<Cliente> find(@ApiParam(value = "Algum parâmetro para filtrar os clientes") Cliente filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);
        Example example = Example.of(filtro, matcher);
        return clientesRepository.findAll(example);
    }

}
