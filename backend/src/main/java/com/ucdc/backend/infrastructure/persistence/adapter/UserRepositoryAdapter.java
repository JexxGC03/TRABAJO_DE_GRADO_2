package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.model.User;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaUserRepository;
import com.ucdc.backend.infrastructure.persistence.mapper.UserJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpa;
    private final UserJpaMapper mapper;

    @Override
    public Optional<User> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var persisted = jpa.save(entity);       // <- usa SIEMPRE el retorno de JPA
        return mapper.toDomain(persisted);
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCitizenId(String citizenId) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public List<User> findAllClients() {
        return List.of();
    }

    @Override
    public Optional<User> findClientById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAllAdmins() {
        return List.of();
    }

    @Override
    public Optional<User> findAdminById(UUID id) {
        return Optional.empty();
    }

    private final JpaUserRepository userRepository;


}
