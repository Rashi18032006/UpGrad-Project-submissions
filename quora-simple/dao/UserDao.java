package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUuid(String uuid);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
