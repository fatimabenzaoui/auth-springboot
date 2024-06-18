package com.fb.auth.dto;

import lombok.Data;

/**
 * DTO qui permet de réinitialiser son mot de passe en cas d'oubli
 */
@Data
public class UserPasswordResetDTO {
    private String newPassword;
    private String confirmPassword;
}
