package com.fb.auth.dto;

import lombok.Data;

/**
 * DTO qui permet de s'authentifier
 */
@Data
public class AuthenticationDTO {
    private String username;
    private String password;
}
