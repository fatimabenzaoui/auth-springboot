package com.fb.auth.dao;

import com.fb.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO Authority
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    Boolean existsByAuthorityLabel(String authorityLabel);
    Authority findByAuthorityLabel(String authorityLabel);
}