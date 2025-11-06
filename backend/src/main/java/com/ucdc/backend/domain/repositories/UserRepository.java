package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.model.*;
import com.ucdc.backend.domain.value.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    /**Methods CRUD
     */

    Optional<User> findById(UUID id);
    List<User> findAll();
    User save(User user);
    void deleteById(UUID id);

    /**Others Methods
     */
    boolean existsById(UUID id);
    Optional<User> findByEmail(Email email);
    boolean existsByCitizenId(String citizenId);
    boolean existsByEmail(String email);

    /**Methods for Type
     */
    List<User> findAllClients();
    Optional<User> findClientById(UUID id);
    List<User> findAllAdmins();
    Optional<User> findAdminById(UUID id);
}
