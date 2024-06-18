package com.fb.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * DTO qui permet de modifier les informations personnelles de l'utilisateur actuellement connecté
 */
@Data
public class UserDetailsUpdateDTO {

    private String username;

    @Email(message = "Email should be valid")
    private String email;
}
