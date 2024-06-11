package com.fb.auth.dto;

import lombok.Data;

import java.time.Instant;
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
    private Set<String> authorities;
    
    private boolean activated = false;
    private String activationKey;
    private Instant expirationKeyDate;

    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
}
