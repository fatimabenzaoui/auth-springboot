package com.fb.auth.dto;

import com.fb.auth.entity.Authority;
import lombok.Data;

import java.util.Set;

/**
 * DTO User
 */
@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String password;
    private Set<Authority> authorities;
    private boolean activated = false;
    private String activationKey;
}
