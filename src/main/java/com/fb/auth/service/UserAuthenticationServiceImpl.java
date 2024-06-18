package com.fb.auth.service;

import com.fb.auth.config.JwtGenerator;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserAuthenticationDTO;
import com.fb.auth.entity.User;
import com.fb.auth.exception.InvalidPasswordResetKeyException;
import com.fb.auth.exception.PasswordMismatchException;
import com.fb.auth.exception.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Authentifie un utilisateur en utilisant les informations d'authentification fournies et génère un JWT si l'authentification est réussie
     *
     * @param userAuthenticationDTO Un objet contenant le nom d'utilisateur et le mot de passe pour l'authentification
     * @return Une carte (Map) contenant les informations du JWT si l'authentification réussit, sinon une carte vide
     */
    @Override
    public Map<String, String> authenticate(UserAuthenticationDTO userAuthenticationDTO) {
        // utilise l'objet AuthenticationManager pour tenter d'authentifier l'utilisateur avec les informations fournies dans userAuthenticationDTO
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userAuthenticationDTO.getUsername(), userAuthenticationDTO.getPassword())
        );
        // vérifie si l'objet Authentication est authentifié
        if (authentication.isAuthenticated()) {
            // si oui, charge les détails de l'utilisateur (UserDetails) à partir du nom d'utilisateur fourni
            UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(userAuthenticationDTO.getUsername());
            // si oui, génère un JWT en utilisant les détails de l'utilisateur et le retourne sous forme de carte (Map)
            return jwtGenerator.generateJWT(userDetails);
        }
        // si l'authentification échoue, retourne une carte vide
        return Collections.emptyMap();
    }

    /**
     * Génère une clé de réinitialisation du mot de passe et envoie un email à l'utilisateur pour réinitialiser son mot de passe
     *
     * @param email L'email de l'utilisateur demandant la réinitialisation du mot de passe
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec l'email donné
     */
    @Override
    public void forgotPassword(String email) {
        // recherche l'utilisateur avec son email
        User user = userRepository.findByEmail(email);
        // si aucun utilisateur n'est trouvé, lance une exception UsernameNotFoundException
        if (user == null) {
            throw new UsernameNotFoundException("*** NO USER FOUND WITH EMAIL : " + email);
        }
        // génère une clé de réinitialisation de mot de passe unique
        String passwordResetKey = UUID.randomUUID().toString();
        // assigne la clé de réinitialisation à l'utilisateur et fixe une date d'expiration pour la clé (24 heures)
        user.setPasswordResetKey(passwordResetKey);
        user.setPasswordResetKeyExpiration(Instant.now().plus(24, ChronoUnit.HOURS));
        // sauvegarde les modifications de l'utilisateur dans le dépôt
        userRepository.save(user);
        // envoie un email à l'utilisateur avec un lien pour réinitialiser son mot de passe
        this.emailService.sendPasswordResetEmail(user);
    }

    /**
     * Réinitialise le mot de passe de l'utilisateur si la clé de réinitialisation est valide et si les mots de passe fournis correspondent
     *
     * @param passwordResetKey La clé de réinitialisation du mot de passe envoyée à l'utilisateur
     * @param newPassword Le nouveau mot de passe choisi par l'utilisateur
     * @param confirmPassword La confirmation du nouveau mot de passe
     * @throws InvalidPasswordResetKeyException si la clé de réinitialisation est invalide ou expirée
     * @throws PasswordMismatchException si les mots de passe fournis ne correspondent pas
     */
    @Override
    public void resetPassword(String passwordResetKey, String newPassword, String confirmPassword) {
        // recherche l'utilisateur avec la clé de réinitialisation du mot de passe
        User user = userRepository.findByPasswordResetKey(passwordResetKey);
        // vérifie si l'utilisateur existe et si la clé de réinitialisation n'est pas expirée
        if (user == null || user.getPasswordResetKeyExpiration().isBefore(Instant.now())) {
            throw new InvalidPasswordResetKeyException("*** INVALID OR EXPIRED PASSWORD RESET KEY");
        }
        // vérifie si les nouveaux mots de passe fournis correspondent
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("*** PASSWORDS DO NOT MATCH");
        }
        // encode le nouveau mot de passe et l'assigne à l'utilisateur
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        // supprime la clé de réinitialisation du mot de passe et sa date d'expiration après utilisation
        user.setPasswordResetKey(null);
        user.setPasswordResetKeyExpiration(null);
        // sauvegarde les modifications de l'utilisateur dans la base de données
        userRepository.save(user);
    }

    /**
     * Met à jour le mot de passe de l'utilisateur actuellement connecté
     *
     * @param currentPassword le mot de passe actuel de l'utilisateur
     * @param newPassword le nouveau mot de passe que l'utilisateur souhaite définir
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé dans la base de données
     * @throws PasswordMismatchException si le mot de passe actuel est incorrect
     */
    @Override
    public void updatePassword(String currentPassword, String newPassword) {
        // récupère l'utilisateur connecté à partir du contexte de sécurité actuel
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        // vérifie si l'utilisateur connecté existe bien dans la base de données
        if (user == null) {
            throw new UsernameNotFoundException("*** USER NOT FOUND");
        }
        // vérifie si l'ancien mot de passe est correct
        if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordMismatchException("*** CURRENT PASSWORD IS INCORRECT");
        }
        // crypte le nouveau mot de passe
        String encodedNewPassword = bCryptPasswordEncoder.encode(newPassword);
        // sauvegarde le nouveau mot de passe
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

}
