package com.fb.auth.controller;

import com.fb.auth.dto.UserAuthenticationDTO;
import com.fb.auth.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
