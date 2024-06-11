package com.fb.auth.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité de l'application (filtres de sécurité et autorisations d'accès aux différentes routes)
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

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

                // autorise l'accès à ces routes sans authentification
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/user/createAccount").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/activateAccount").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/requestNewActivationKey").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/removeNotActivatedAccounts").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/authenticate").permitAll()

                        // nécessite l'authentification pour toutes les autres ressources
                        .anyRequest().authenticated()
                )
                // choix d'une authentification stateless (la session n'enregistrera pas le user -> front : localStorage)
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // ajoute un filtre pour valider le token de toutes les requêtes
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Fournit un encodeur de mot de passe BCrypt
     *
     * @return Un bean BCryptPasswordEncoder utilisé pour crypter les mots de passe
     */
    @Bean
    public BCryptPasswordEncoder bCryptpasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Déclare un bean AuthenticationManager
     *
     * @param authenticationConfiguration La configuration d'authentification utilisée pour obtenir l'AuthenticationManager
     * @return L'AuthenticationManager obtenu à partir de la configuration spécifiée
     * @throws Exception Si une erreur survient lors de la récupération de l'AuthenticationManager à partir de la configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configure un fournisseur d'authentification personnalisé utilisant un objet DaoAuthenticationProvider
     * Cet objet utilise un service personnalisé (UserDetailsService) pour charger les détails de l'utilisateur à partir de la base de données
     * Il utilise également un encodeur de mot de passe BCrypt pour vérifier les mots de passe des utilisateurs
     * @return Un fournisseur d'authentification configuré avec le service utilisateur et l'encodeur de mot de passe appropriés
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.bCryptpasswordEncoder());
        return daoAuthenticationProvider;
    }
}
