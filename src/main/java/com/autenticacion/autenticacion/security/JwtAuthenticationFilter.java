package com.autenticacion.autenticacion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsService userDetailsService; // Spring inyectará tu CustomUserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraer el token de la cabecera "Authorization"
        String token = extractTokenFromRequest(request);

        // 2. Validar el token
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {

            // 3. Obtener el username (email) del token
            String username = jwtProvider.getUsernameFromToken(token);

            // 4. Cargar los detalles del usuario desde la BD
            // Verificamos también que no exista ya una autenticación en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Crear un objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // La contraseña (credentials) es null porque ya validamos con JWT
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Establecer al usuario como autenticado en el Contexto de Seguridad
                // A partir de aquí, Spring Security sabe que la petición es válida.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continuar con la cadena de filtros
        // Si el token no era válido, el contexto de seguridad queda en "null"
        // y Spring Security denegará el acceso a los endpoints protegidos.
        filterChain.doFilter(request, response);
    }

    /**
     * Método helper para extraer el token JWT de la cabecera "Authorization".
     * Espera el formato "Bearer <token>".
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Devuelve solo el token
        }
        return null;
    }
}