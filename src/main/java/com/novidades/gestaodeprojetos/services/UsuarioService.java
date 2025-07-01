package com.novidades.gestaodeprojetos.services;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.novidades.gestaodeprojetos.model.Usuario;
import com.novidades.gestaodeprojetos.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repositorioUsuario;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    
}
