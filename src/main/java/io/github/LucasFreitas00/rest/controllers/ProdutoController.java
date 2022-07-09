package io.github.LucasFreitas00.rest.controllers;

import io.github.LucasFreitas00.domain.entities.Produto;
import io.github.LucasFreitas00.domain.repositories.Produtos;
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
@RequestMapping("/api/produtos")
@Api("Api Produtos")
public class ProdutoController {

    @Autowired
    private Produtos produtosRepository;

    @GetMapping("{id}")
    @ApiOperation("Obter dados de um produto")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto encontrado"),
            @ApiResponse(code = 401, message = "Requisição não autorizada"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Produto não encontrado para o ID informado")
    })
    public Produto getClienteById(@PathVariable @ApiParam(value = "ID do produto", example = "1") Integer id) {
        return produtosRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Produto não encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Cadastrar um produto")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Produto cadastrado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
            @ApiResponse(code = 401, message = "Requisição não autorizada"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação")
        })
    public Produto save(@RequestBody @Valid @ApiParam(value = "Dados do produto a ser cadastrado") Produto produto) {
        return produtosRepository.save(produto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Remover um produto")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Produto removido com sucesso"),
            @ApiResponse(code = 401, message = "Requisição não autorizada"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Produto não encontrado para o ID informado")
    })
    public void delete(@PathVariable @ApiParam(value = "ID do produto", example = "1") Integer id) {
        produtosRepository.findById(id)
                .map(produto -> {
                    produtosRepository.delete(produto);
                    return produto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado"));

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Atualizar dados de um produto")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Produto atualizado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
            @ApiResponse(code = 401, message = "Requisição não autorizada"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação"),
            @ApiResponse(code = 404, message = "Produto não encontrado para o ID informado")
    })
    public void update(@PathVariable Integer id,
                       @RequestBody @Valid Produto produto) {
        produtosRepository
                .findById(id)
                .map(produtoExistente -> {
                    produto.setId(produtoExistente.getId());
                    produtosRepository.save(produto);
                    return Void.TYPE;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado"));
    }

    @GetMapping
    @ApiOperation("Obter dados de alguns ou de todos os produtos")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Produto(s) encontrados"),
            @ApiResponse(code = 401, message = "Requisição não autorizada"),
            @ApiResponse(code = 403, message = "Necessária uma autenticação")
    })
    public List<Produto> find(@ApiParam(value = "Algum parâmetro para filtrar os produtos") Produto filtro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);

        Example example = Example.of(filtro, matcher);
        return produtosRepository.findAll(example);
    }
}
