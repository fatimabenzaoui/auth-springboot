package com.fb.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration pour l'audit JPA, permettant de définir un fournisseur d'informations sur l'auditeur
 * L'annotation @EnableJpaAuditing active la prise en charge de l'audit JPA et spécifie la référence
 * du fournisseur d'informations sur l'auditeur à utiliser
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {
    /**
     * Configuration pour fournir un fournisseur d'informations sur l'auditeur
     * Cette méthode retourne une instance de AuditorAwareImpl qui implémente l'interface AuditorAware
     *
     * @return Un bean AuditorAware<String> permettant de fournir des informations sur l'auditeur
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
