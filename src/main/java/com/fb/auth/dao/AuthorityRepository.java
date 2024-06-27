package com.fb.auth.dao;

import com.fb.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO Authority
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    /**
     * Recherche un rôle avec son libellé
     *
     * @param authorityLabel Le libellé du rôle à rechercher
     * @return Le rôle correspondant au libellé spécifié, ou null si aucun rôle correspondant n'est trouvé
     */
    Authority findByAuthorityLabel(String authorityLabel);
}