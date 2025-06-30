package com.novidades.gestaodeprojetos.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;


@Entity
@SequenceGenerator(name = "generator_usuario", sequenceName = "sequence_usuario", initialValue = 1, allocationSize = 1)
public class Usuario implements UserDetails{
    
    @Id //determinando como chave primária
    @GeneratedValue(strategy =  GenerationType.SEQUENCE, generator = "generator_usuario")
    private Long id;

    @Column(nullable = false)//determinando que essa coluna não pode ser vazia
    private String nome;

    @Column(nullable = false, unique = true)
    //Não pode ser null e nem repetido
    private String email;

    @Column(nullable = false)
    //não pode ser null
    private String senha;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    //daqui pra baixo é implementação do user details
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

     @Override
    public boolean isAccountNonExpired() {
        return true;
    }
     @Override
    public boolean isAccountNonLocked() {
        return true;
    }

     @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

     @Override
    public boolean isEnabled() {
        return true;
    }

}
