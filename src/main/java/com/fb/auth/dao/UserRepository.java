package com.fb.auth.dao;

import com.fb.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * DAO User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String userEmail);
    User findByActivationKey(String activationKey);
    User findByUsername(String username);
    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    User findByEmail(String email);
    User findByPasswordResetKey(String passwordResetKey);
}
