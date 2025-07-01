package com.novidades.gestaodeprojetos.services;


import java.util.Date;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.novidades.gestaodeprojetos.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

//Essa classe pode ser reutilizada em diversos projetos

@Component
public class JWTService {

    //chave secreta usada pelo JWT para codificar e decodificar o token.
    private static final String chavePrivadaJWT = "secretKey";

   /**
    * Método para gerar o token jwt
    * @param authentication Autenticação do usuário.
    * @return Token
    */
    public String gerarToken(Authentication authentication){

        //tempo de expiração do token em milissegundos(86400000= 1 dia)
        //pode variar de acordo com a regra de negocio
        int tempoExpiracao = 86400000;

        //a data expiração é a data atual + o tempoExpiracao
        Date dataExpiracao = new Date(new Date().getTime() + tempoExpiracao);

        //pegando o usuario atual da autenticação
        Usuario usuario = (Usuario) authentication.getPrincipal();


        //pega todos os dados e retorna um token do JWT
        return Jwts.builder()
        .setSubject(usuario.getId().toString())
        .setIssuedAt(new Date())
        .setExpiration(dataExpiracao)
        .signWith(SignatureAlgorithm.HS512, chavePrivadaJWT)
        .compact();

    }


   /**
    *  Método para retornar o id do usuário dono do token
    * @param token do usuário
    * @return id do usuário
    */
    public Optional<Long> obterIsUsuario(String token){
        
        try {
            //retorna as permissões(claims) do token
            Claims claims = parse(token).getBody();

            //retorna o id de dentro do token se en contrar ou retornar null
            return Optional.ofNullable(Long.parseLong(claims.getSubject()));


        } catch (Exception e) {
            //se não encontrar nada devolve um optional null
            return Optional.empty();        }
    }

    //Sabe descobrir de dentro do token com base na chave privada qwual as permissões (claims) do usuário
    private Jws<Claims> parse(String token) {
        
       return Jwts.parser().setSigningKey(chavePrivadaJWT).parseClaimsJws(token);
    }
    
}
