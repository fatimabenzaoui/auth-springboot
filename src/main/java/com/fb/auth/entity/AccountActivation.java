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
@Table(name = "account_activations")
public class AccountActivation implements Serializable {

    @Serial
    private static final long serialVersionUID = -8885329785535431598L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_activation_id")
    private Long accountActivationId;

    @Size(max = 20)
    @Column(length = 20, nullable = false)
    @JsonIgnore
    private String activationKey;

    @Column(nullable = false)
    private Instant expirationDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
