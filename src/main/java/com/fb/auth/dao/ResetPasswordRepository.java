package com.fb.auth.dao;

import com.fb.auth.entity.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    /**
     * Recherche une entité ResetPassword avec sa clé de réinitialisation
     *
     * @param resetKey La clé de réinitialisation à rechercher
     * @return L'entité ResetPassword correspondant à la clé de réinitialisation spécifiée, ou null si aucune entité n'est trouvée
     */
    ResetPassword findByResetKey(String resetKey);
}
