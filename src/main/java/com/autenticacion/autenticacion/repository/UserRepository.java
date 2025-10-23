package com.autenticacion.autenticacion.repository;

import com.autenticacion.autenticacion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Spring Data JPA entiende este nombre de método y automáticamente
     * crea una consulta SQL: "SELECT * FROM users WHERE email = ?"
     *
     * Usamos Optional<User> porque el usuario puede no existir.
     */
    Optional<User> findByEmail(String email);

}