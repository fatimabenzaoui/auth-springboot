package com.fb.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtGenerator {

    // Clé secrète utilisée pour signer le JWT
    @Value("${jwt.secretKey}")
    private String secretKey;


    /**
     * Génère un JWT pour l'utilisateur donné
     *
     * @param userDetails L'utilisateur pour lequel générer le JWT
     * @return Un map contenant le JWT généré avec la clé "bearer"
     */
    public Map<String, String> generateJWT(UserDetails userDetails) {
        // temps actuel en millisecondes
        final long currentTime = System.currentTimeMillis();
        // temps d'expiration du token (30 minutes (30 minutes X 60 secondes X 1000 millisecondes)
        final long expirationTime = currentTime + 30 * 60 * 1000;

        // crée les claims (informations à inclure dans le JWT)
        final Map<String, Object> claims = Map.of(
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, userDetails.getUsername(),
                Claims.ISSUED_AT, new Date(currentTime)
        );

        // génère le JWT en utilisant les claims et la clé secrète
        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(userDetails.getUsername())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
        // retourne le JWT dans un map avec la clé "bearer"
        return Map.of("bearer", bearer);
    }

    /**
     * Récupère la clé secrète à partir de la clé secrète codée en base64
     *
     * @return La clé secrète utilisée pour signer le JWT
     */
    private Key getKey() {
        // décodage de la clé secrète depuis la clé secrète codée en base64
        final byte[] decoder = Decoders.BASE64.decode(secretKey);
        // crée et retourne la clé secrète à partir du décodage
        return Keys.hmacShaKeyFor(decoder);
    }

    /**
     * Extrait le surnom de l'utilisateur du JWT
     *
     * @param token Le JWT à partir duquel extraire le surnom de l'utilisateur
     * @return Le surnom de l'utilisateur extrait du JWT
     */
    public String getUsernameFromToken(String token) {
        return this.getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Vérifie si le JWT est expiré
     *
     * @param token Le JWT à vérifier
     * @return true si le JWT est expiré
     */
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getClaimFromToken(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    /**
     * Récupère une information spécifique du JWT en utilisant une fonction de réclamation
     *
     * @param token Le JWT à partir duquel récupérer la réclamation
     * @param function La fonction de réclamation à appliquer sur les réclamations du JWT
     * @return La réclamation spécifique extraite du JWT
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> function) {
        Claims claims = getAllClaimsFromToken(token);
        return function.apply(claims);
    }

    /**
     * Récupère toutes les informations du JWT
     *
     * @param token Le JWT à partir duquel récupérer toutes les informations
     * @return Toutes les informations du JWT
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(this.getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

}
