package com.autenticacion.autenticacion.controller;

import com.autenticacion.autenticacion.dto.LoginRequest;
import com.autenticacion.autenticacion.dto.RegisterRequest;
import com.autenticacion.autenticacion.model.User;
import com.autenticacion.autenticacion.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // 1. Preparación (Datos de prueba)
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Usuario de Prueba");
        registerRequest.setEmail("test@prueba.com");
        registerRequest.setPassword("password123");

        // 2. Ejecución (Simula el POST a /auth/register)
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))) // Convierte el DTO a JSON

                // 3. Verificación (Comprueba la respuesta)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Usuario registrado exitosamente!"));

        // 4. Verificación extra (Comprueba la base de datos)
        User savedUser = userRepository.findByEmail("test@prueba.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Usuario de Prueba");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    void testLoginUser_Success() throws Exception {
        // 1. Preparación (Necesitamos un usuario ya registrado)
        User user = new User();
        user.setName("Usuario de Prueba");
        user.setEmail("test@prueba.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        // 2. Preparación (Datos de login)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@prueba.com");
        loginRequest.setPassword("password123");

        // 3. Ejecución (Simula el POST a /auth/login)
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))

                // 4. Verificación (Comprueba la respuesta)
                .andExpect(status().isOk())
                // Comprueba que la respuesta JSON tiene un campo "token" que no está vacío
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testLoginUser_BadCredentials() throws Exception {
        // 1. Preparación (Registramos un usuario)
        User user = new User();
        user.setName("Usuario de Prueba");
        user.setEmail("test@prueba.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        // 2. Preparación (Datos de login con contraseña INCORRECTA)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@prueba.com");
        loginRequest.setPassword("passwordINCORRECTA");

        // 3. Ejecución y Verificación
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}