package com.docu_sign.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docu_sign.entity.User;

public interface UserRepository extends JpaRepository<User,UUID> {

    Optional<User> findByEmail(String email);
    
}
