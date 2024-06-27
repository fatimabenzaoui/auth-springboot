package com.fb.auth.dao;

import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountActivationRepository extends JpaRepository<AccountActivation, Long> {
    /**
     * Recherche une entité AccountActivation avec sa clé d'activation
     *
     * @param activationKey La clé d'activation à rechercher
     * @return L'entité AccountActivation correspondant à la clé d'activation spécifiée, ou null si aucune entité n'est trouvée
     */
    AccountActivation findByActivationKey(String activationKey);

    /**
     * Recherche une entité AccountActivation avec l'utilisateur associé
     *
     * @param user L'utilisateur pour lequel rechercher l'entité AccountActivation
     * @return L'entité AccountActivation correspondant à l'utilisateur spécifié, ou null si aucune entité n'est trouvée
     */
    AccountActivation findByUser(User user);

    /**
     * Supprime une entité AccountActivation avec l'utilisateur associé
     *
     * @param user L'utilisateur pour lequel supprimer l'entité AccountActivation
     */
    void deleteByUser(User user);
}
