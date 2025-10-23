package com.autenticacion.autenticacion.service;

import com.autenticacion.autenticacion.dto.RegisterRequest;
import com.autenticacion.autenticacion.model.User;
import com.autenticacion.autenticacion.repository.UserRepository;
import com.autenticacion.autenticacion.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class) // Habilita Mockito
class AuthServiceTest {

    @Mock // Crea un "doble de prueba" (mock) del repositorio
    private UserRepository userRepository;

    @Mock // Mock del encoder
    private PasswordEncoder passwordEncoder;

    @Mock // Mock del provider de JWT
    private JwtProvider jwtProvider;

    @InjectMocks // Crea una instancia de AuthService e inyecta los @Mocks de arriba
    private AuthService authService;

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // 1. Preparación (Definimos el comportamiento de los mocks)
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existente@prueba.com");

        // Cuando se llame a findByEmail, simula que el usuario SÍ existe
        when(userRepository.findByEmail("existente@prueba.com"))
                .thenReturn(Optional.of(new User()));

        // 2. Ejecución y Verificación
        // Comprueba que se lanza la excepción que esperamos
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El email ya está en uso.");

        // Verifica que el método save NUNCA fue llamado
        verify(userRepository, never()).save(any(User.class));
    }
}