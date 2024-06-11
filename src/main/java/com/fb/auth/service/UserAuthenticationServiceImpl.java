package com.fb.auth.service;

import com.fb.auth.config.JwtGenerator;
import com.fb.auth.dto.UserAuthenticationDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

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

}
