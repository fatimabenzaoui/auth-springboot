package com.fb.auth.dao;

import com.fb.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String userEmail);
    User findByActivationKey(String activationKey);
    User findByUsername(String username);

}
