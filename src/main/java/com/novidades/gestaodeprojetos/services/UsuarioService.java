package com.novidades.gestaodeprojetos.services;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.novidades.gestaodeprojetos.model.Usuario;
import com.novidades.gestaodeprojetos.repository.UsuarioRepository;
import com.novidades.gestaodeprojetos.security.JWTService;
import com.novidades.gestaodeprojetos.view.model.usuario.LoginResponse;

@Service
public class UsuarioService {

    private static final String headerPrefix = "Bearer ";

    private final UsuarioRepository repositorioUsuario;
    private final JWTService jwtService;

    public UsuarioService(UsuarioRepository repositorioUsuario, JWTService jwtService) {
        this.repositorioUsuario = repositorioUsuario;
        this.jwtService = jwtService;
    }

    public List<Usuario> obterTodos() {
        return repositorioUsuario.findAll();
    }

    public Optional<Usuario> obterPorId(Long id) {
        return repositorioUsuario.findById(id);
    }

    public Optional<Usuario> obterPorEmail(String email) {
        return repositorioUsuario.findByEmail(email);
    }

    public Usuario adicionar(Usuario usuario, PasswordEncoder passwordEncoder) {
        usuario.setId(null);

        if (obterPorEmail(usuario.getEmail()).isPresent()) {
            throw new InputMismatchException("Já existe um usuário com esse email: " + usuario.getEmail());
        }

        String senha = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senha);

        return repositorioUsuario.save(usuario);
    }

    // Recebe o AuthenticationManager como parâmetro para quebrar ciclo
    public LoginResponse logar(String email, String senha, AuthenticationManager authenticationManager) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, senha, Collections.emptyList()));

        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        String token = headerPrefix + jwtService.gerarToken(autenticacao);

        Usuario usuario = repositorioUsuario.findByEmail(email).orElseThrow(() ->
            new RuntimeException("Usuário não encontrado com email: " + email)
        );

        return new LoginResponse(token, usuario);
    }
}
