package com.fb.auth.dto;

import lombok.Data;

/**
 * DTO qui permet de s'authentifier
 */
@Data
public class UserAuthenticationDTO {
    private String username;
    private String password;
}
