package com.fb.auth.dto;

import lombok.Data;

/**
 * DTO qui permet de modifier le mot de passe de l'utilisateur actuellement connecté
 */
@Data
public class UserPasswordUpdateDTO {
    private String currentPassword;
    private String newPassword;
}
