package com.novidades.gestaodeprojetos.security;

import java.io.IOException;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;


    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    // método principal onde toda requisição bate antes de chegar no endpoint
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                //pega o token de dentro da rerquisição
                String token = obterToken(request);

                //pego o id do usuario que está dentro do token(dono do token)
                Optional<Long> id = jwtService.obterIsUsuario(token);


                //se não achar o id, é pq o usuário mandou o token errado
                if(!id.isPresent()){
                    throw new InputMismatchException("Token inválido.");
                }

                //pego o usuario dono do token pelo seu id
                UserDetails usuario = customUserDetailsService.obterUsuarioPorId(id.get());
       
                //verificamos se o usuario está se autenticando ou não
                UsernamePasswordAuthenticationToken autenticacao = 
                new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
    
                //mudando a autenticação para a própria requisição
                autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //passando a autenticação para o contexto do security
                // agora o spring toma contya de tudo
                SecurityContextHolder.getContext().setAuthentication(autenticacao);

            }

    private String obterToken(HttpServletRequest request){

        String token = request.getHeader("Authorization");

        //verifica se veio algo sem ser espaços em branco dentro do token
        if(org.springframework.util.StringUtils.hasText(token)){
            return null;

        }
        return token.substring(0, 7);

    }
    
}
