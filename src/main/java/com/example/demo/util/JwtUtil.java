package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Clave secreta para firmar el token
    private final long expirationTime = 86400000; // 24 horas en milisegundos

    // Genera un token JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())); // Agrega los roles al token

        return Jwts.builder()
                .setClaims(claims) // Agrega los claims (información adicional)
                .setSubject(userDetails.getUsername()) // Nombre de usuario como sujeto
                .setIssuedAt(new Date()) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Fecha de expiración
                .signWith(secretKey) // Firma el token con la clave secreta
                .compact(); // Convierte el token en una cadena
    }

    // Valida el token JWT
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Verifica que el nombre de usuario en el token coincida con el UserDetails
            String username = claims.getSubject();
            return username.equals(userDetails.getUsername()) && !isTokenExpired(claims);
        } catch (Exception e) {
            // Lanza una excepción para manejar errores específicos
            throw new RuntimeException("Token inválido: " + e.getMessage());
        }
    }

    // Extrae el nombre de usuario del token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Verifica si el token ha expirado
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}