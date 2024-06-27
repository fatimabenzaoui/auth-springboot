package com.fb.auth.controller;

import com.fb.auth.constant.AuthoritiesConstants;
import com.fb.auth.dto.UserAuthenticationDTO;
import com.fb.auth.dto.UserDetailsUpdateDTO;
import com.fb.auth.dto.UserPasswordResetDTO;
import com.fb.auth.dto.UserPasswordUpdateDTO;
import com.fb.auth.service.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path="user")
@AllArgsConstructor
@Slf4j
public class UserAuthenticationController {

    private final UserAuthenticationService userAuthenticationService;

    /**
     * Authentifie l'utilisateur
     *
     * @param userAuthenticationDTO Les informations d'authentification fournies par l'utilisateur (nom d'utilisateur et mot de passe)
     * @return Un objet ResponseEntity contenant un objet Map avec le JWT si l'authentification réussit, ou une réponse HTTP 401 Unauthorized si l'authentification échoue
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody UserAuthenticationDTO userAuthenticationDTO) {
        Map<String, String> tokenMap = userAuthenticationService.authenticate(userAuthenticationDTO);
        if (!tokenMap.isEmpty()) {
            return ResponseEntity.ok(tokenMap);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Gère la demande de réinitialisation de mot de passe en envoyant un email de réinitialisation à l'utilisateur
     *
     * @param email L'email de l'utilisateur demandant la réinitialisation du mot de passe
     * @return Une réponse HTTP indiquant que l'email de réinitialisation a été envoyé
     */
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> requestResetPassword(@RequestParam("email") String email) {
        userAuthenticationService.requestResetPassword(email);
        return ResponseEntity.ok("Password reset email sent.");
    }

    /**
     * Réinitialise le mot de passe de l'utilisateur en utilisant une clé de réinitialisation et les nouvelles informations de mot de passe fournies
     *
     * @param passwordResetKey La clé de réinitialisation envoyée à l'utilisateur pour vérifier l'authenticité de la demande
     * @param userPasswordResetDTO Un objet contenant le nouveau mot de passe et la confirmation de celui-ci
     * @return Une réponse HTTP indiquant que le mot de passe a été réinitialisé avec succès
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("key") String passwordResetKey, @RequestBody UserPasswordResetDTO userPasswordResetDTO) {
        userAuthenticationService.resetPassword(passwordResetKey, userPasswordResetDTO.getNewPassword(), userPasswordResetDTO.getConfirmPassword());
        return ResponseEntity.ok("Password successfully reset.");
    }

    /**
     * Met à jour le mot de passe de l'utilisateur actuellement connecté
     *
     * @param userPasswordUpdateDTO l'objet contenant le mot de passe actuel et le nouveau mot de passe
     * @return une réponse HTTP indiquant que le mot de passe a été modifié avec succès
     */
    @PutMapping("/updatePassword")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.EDITOR + "', '" + AuthoritiesConstants.CUSTOMER + "')")
    public ResponseEntity<String> updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO) {
        userAuthenticationService.updatePassword(userPasswordUpdateDTO.getCurrentPassword(), userPasswordUpdateDTO.getNewPassword());
        return ResponseEntity.ok("Password updated successfully.");
    }

    /**
     * Met à jour les informations personnelles de l'utilisateur actuellement connecté
     *
     * @param userDetailsUpdateDTO l'objet contenant le surnom de l'utilisateur et son email
     * @return une réponse HTTP indiquant que les informations personnelles de l'utilisateur ont été mis à jour avec succès
     */
    @PutMapping("/updateUserDetails")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.EDITOR + "', '" + AuthoritiesConstants.CUSTOMER + "')")
    public ResponseEntity<String> updateUserDetails(@Valid @RequestBody UserDetailsUpdateDTO userDetailsUpdateDTO) {
        userAuthenticationService.updateUserDetails(userDetailsUpdateDTO);
        return ResponseEntity.ok("User Details updated successfully.");
    }
}
