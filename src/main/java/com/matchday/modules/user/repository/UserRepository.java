package com.matchday.modules.user.repository;

import com.matchday.modules.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);

}