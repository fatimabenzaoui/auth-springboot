package com.fb.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

/**
 * DTO User
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String password;
    private boolean activated = false;
    private Set<String> authorities;

    private String activationKey;
    private Instant activationKeyExpiration;

    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
}
