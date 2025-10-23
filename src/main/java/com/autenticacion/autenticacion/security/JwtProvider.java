package com.autenticacion.autenticacion.security;

// package com.tuempresa.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    // ¡IMPORTANTE! Esta clave debe ser secreta y estar en una variable de entorno.
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long expirationTime = 1000 * 60 * 60 * 24; // 24 horas

    // --- MÉTODO EXISTENTE ---
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // --- NUEVO: Parsea el token para obtener todos los "claims" (datos) ---
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --- NUEVO: Extrae el username (que guardamos en el "Subject") ---
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    // --- NUEVO: Valida si el token es correcto (firma) y no ha expirado ---
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Aquí puedes loguear las excepciones específicas si quieres más detalle
            // (ExpiredJwtException, MalformedJwtException, SignatureException, etc.)
            log.error("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }
}
