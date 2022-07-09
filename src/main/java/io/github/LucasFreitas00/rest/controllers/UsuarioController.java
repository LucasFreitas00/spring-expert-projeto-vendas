package io.github.LucasFreitas00.rest.controllers;

import io.github.LucasFreitas00.domain.services.impl.UsuarioServiceImpl;
import io.github.LucasFreitas00.domain.entities.Usuario;
import io.github.LucasFreitas00.exceptions.SenhaInvalidaException;
import io.github.LucasFreitas00.rest.dto.CredenciaisDTO;
import io.github.LucasFreitas00.rest.dto.TokenDTO;
import io.github.LucasFreitas00.security.jwt.JwtService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Api("Api Usuários")
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salvar um novo usuário")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Usuário salvo com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação")
    })
    public Usuario salvar(@RequestBody @Valid @ApiParam(value = "Dados do novo usuário") Usuario usuario) {
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        return usuarioService.salvar(usuario);
    }

    @PostMapping("auth")
    @ApiOperation("Autenticar um usuário")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Usuário autenticado com sucesso"),
            @ApiResponse(code = 400, message = "Erro de validação"),
            @ApiResponse(code = 401, message = "Requisição não autorizada. Usuário ou senha inválido")
    })
    public TokenDTO autenticar(@RequestBody @Valid @ApiParam(value = "Credenciais do usuário") CredenciaisDTO credenciais) {
        try {
            Usuario usuario = Usuario.builder()
                    .login(credenciais.getLogin())
                    .senha(credenciais.getSenha())
                    .build();
            UserDetails usuarioAutenticado = usuarioService.autenticar(usuario);
            String token = jwtService.gerarToken(usuario);
            return new TokenDTO(usuario.getLogin(), token);
        } catch (UsernameNotFoundException | SenhaInvalidaException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
