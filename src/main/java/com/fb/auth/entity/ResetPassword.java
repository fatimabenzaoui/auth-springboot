package com.fb.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode(callSuper = false) @Builder
@Table(name = "reset_passwords")
public class ResetPassword implements Serializable {

    @Serial
    private static final long serialVersionUID = 2386231220827674167L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_password_id")
    private Long resetPasswordId;

    @Size(max = 36)
    @Column(length = 36, nullable = false)
    private String resetKey;

    @Column(nullable = false)
    private Instant expirationDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;
}
