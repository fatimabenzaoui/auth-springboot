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
    /**
     * Vérifie si un utilisateur existe déjà dans la base de données avec le nom d'utilisateur spécifié
     *
     * @param username le nom d'utilisateur à vérifier
     * @return true si un utilisateur avec ce nom d'utilisateur existe, false sinon
     */
    Boolean existsByUsername(String username);

    /**
     * Vérifie si un utilisateur existe déjà dans la base de données avec l'adresse email spécifiée
     *
     * @param userEmail l'adresse email à vérifier
     * @return true si un utilisateur avec cette adresse email existe, false sinon
     */
    Boolean existsByEmail(String userEmail);

    /**
     * Récupère un utilisateur dans la base de données avec son nom d'utilisateur
     *
     * @param username le nom d'utilisateur à rechercher
     * @return l'utilisateur correspondant au nom d'utilisateur spécifié, ou null s'il n'existe pas
     */
    User findByUsername(String username);

    /**
     * Récupère les utilisateurs non activés avec des clés d'activation expirées
     * Cette méthode effectue une jointure entre les entités User et AccountActivation
     * pour trouver les utilisateurs dont le compte n'est pas activé et dont la clé
     * d'activation a expiré avant la date spécifiée
     *
     * @param date la date limite pour laquelle les clés d'activation sont considérées comme expirées
     * @return une liste d'utilisateurs non activés avec des clés d'activation expirées
     */
    @Query("""
        SELECT u FROM User u
        JOIN AccountActivation a
        ON u.userId = a.user.userId
        WHERE u.activated = false
        AND a.activationKey IS NOT NULL
        AND a.expirationDate < :date
    """)
    List<User> findAllNotActivatedUsersWithActivationKey(@Param("date") Instant date);

    /**
     * Récupère un utilisateur dans la base de données avec son adresse email
     *
     * @param email l'adresse email à rechercher
     * @return l'utilisateur correspondant à l'adresse email spécifiée, ou null s'il n'existe pas
     */
    User findByEmail(String email);
}
