package com.fb.auth.config;

import com.fb.auth.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component @AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtGenerator jwtGenerator;

    /**
     * Filtre les requêtes HTTP pour gérer l'authentification basée sur un token JWT
     *
     * @param request l'objet HttpServletRequest contenant les informations de la requête HTTP
     * @param response l'objet HttpServletResponse pour envoyer la réponse HTTP
     * @param filterChain l'objet FilterChain permettant de poursuivre la chaîne de filtres
     * @throws ServletException en cas d'erreur de traitement de la requête par le filtre
     * @throws IOException en cas d'erreur d'entrée/sortie lors du traitement de la requête
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // récupère le token, le surnom de l'utilisateur et le statut d'expiration du token
        String token;
        String username = null;
        boolean isTokenExpired = true;

        // récupère l'en-tête Authorization de la requête
        final String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")){
            // extrait le token à partir de l'en-tête Authorization
            token = authorization.substring(7);
            // vérifie si le token est expiré
            isTokenExpired = jwtGenerator.isTokenExpired(token);
            // extrait le surnom de l'utilisateur à partir du token
            username = jwtGenerator.getUsernameFromToken(token);
        }

        // vérifie si le token n'a pas expiré, si le surnom de l'utilisateur est présent et si aucune authentification n'est actuellement associée au contexte de sécurité
        if(!isTokenExpired && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // charge les détails de l'utilisateur à partir de la base de données en utilisant le surnom extrait du token
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            // crée un objet d'authentification basé sur les détails de l'utilisateur
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // définit l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        // poursuit la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
