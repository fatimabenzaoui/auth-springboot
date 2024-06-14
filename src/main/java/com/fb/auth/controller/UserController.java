package com.fb.auth.controller;

import com.fb.auth.constant.AuthoritiesConstants;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path="user")
@AllArgsConstructor @Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Retourne la liste des utilisateurs
     *
     * @return ResponseEntity contenant la liste de tous les rôles et le code de statut HTTP approprié
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<UserDTO>> findAll() {
        log.debug("*** FIND ALL USERS");
        List<UserDTO> usersDTO = userService.findAll();
        return ResponseEntity.ok().body(usersDTO);
    }
}
