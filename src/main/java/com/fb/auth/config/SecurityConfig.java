package com.fb.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application (filtres de sécurité et autorisations d'accès aux différentes routes)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Configure les filtres de sécurité (filtre de sécurité pour désactiver CSRF) et autorise l'accès à certaines routes sans authentification
     *
     * @param httpSecurity Le paramètre HttpSecurity utilisé pour configurer la sécurité HTTP
     * @return Un objet SecurityFilterChain pour gérer les filtres de sécurité
     * @throws Exception Si une exception survient lors de la configuration de la sécurité
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // désactive CSRF pour une authentification stateless
                .csrf(AbstractHttpConfigurer::disable)
                // autorise l'accès à des routes sans authentification
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/user/createAccount").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/activateAccount").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    /**
     * Fournit un encodeur de mot de passe BCrypt
     *
     * @return Un bean BCryptPasswordEncoder utilisé pour crypter les mots de passe
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
