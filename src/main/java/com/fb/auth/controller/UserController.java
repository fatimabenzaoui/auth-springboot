package com.fb.auth.controller;

import com.fb.auth.dto.UserDTO;
import com.fb.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="user")
@AllArgsConstructor @Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Crée un nouveau compte utilisateur
     *
     * @param userDTO Les données de l'utilisateur à enregistrer
     */
    @PostMapping(path="/createAccount")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@Valid @RequestBody UserDTO userDTO) {
        log.debug("*** REGISTER USER : {}", userDTO);
        userService.createAccount(userDTO);
    }
}
