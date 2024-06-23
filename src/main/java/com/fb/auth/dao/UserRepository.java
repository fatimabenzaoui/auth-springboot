package com.fb.auth.dao;

import com.fb.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    User findByUsername(String username);
    @Query("""
        SELECT u FROM User u
        JOIN AccountActivation a
        ON u.userId = a.user.userId
        WHERE u.activated = false
        AND a.activationKey IS NOT NULL
        AND a.expirationDate < :date
    """)
    List<User> findAllNotActivatedUsersWithActivationKey(@Param("date") Instant date);
    User findByEmail(String email);
}
