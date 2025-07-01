package com.novidades.gestaodeprojetos.services;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String headerPrefix = "Bearer";

    @Autowired
    private UsuarioRepository repositorioUsuario;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired  
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public List<Usuario> obterTodos(){
        return repositorioUsuario.findAll();

    }

      public Optional<Usuario> obterPorId(Long id){
        return repositorioUsuario.findById(id);

    }

       public Optional<Usuario> obterPorEmail(String email){
        return repositorioUsuario.findByEmail(email);

    }

    public Usuario adicionar(Usuario usuario){
        usuario.setId(null);

        if(obterPorEmail(usuario.getEmail()).isPresent()){
            throw new InputMismatchException("Já existe um usuário com esse email: " + usuario.getEmail());

        }

        //codificando a senha do suuario, gerando um hash
        String senha = passwordEncoder.encode(usuario.getSenha());

        //adiciona a senha no usuario
        usuario.setSenha(senha);

        //salvando o usuario com a nova senha codificada
        return repositorioUsuario.save(usuario);
    }


    public LoginResponse logar(String email, String senha){

        //Aqui a autenticação acontece
        Authentication autenticacao = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, senha, Collections.emptyList()));

            //Aqui passoa nova autenticação para o spring security cuidar
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        //gera o token do usuario para devolver
        // Bearer acft357h829kjsn.ahsvdt675ssjsusbsbhhs.ajsusvvvvstg
        String token = headerPrefix + jwtService.gerarToken(autenticacao);

        Usuario usuario = repositorioUsuario.findByEmail(email).get();

        return new LoginResponse(token, usuario);

    }
    
}
