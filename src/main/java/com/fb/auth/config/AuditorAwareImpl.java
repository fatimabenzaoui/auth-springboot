package com.fb.auth.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implémentation de l'interface AuditorAware pour fournir des informations sur l'auditeur
 * actuellement authentifié dans l'application
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    /**
     * Retourne le nom de l'auditeur actuellement authentifié
     * en récupérant l'objet Authentication à partir du contexte de sécurité
     *
     * @return Un Optional contenant le nom de l'auditeur actuellement authentifié
     */
    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }
        return Optional.of(authentication.getName());
    }
}
