package com.fb.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Model Authority
 */
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode(callSuper = false)
@Table(name="authority")
public class Authority implements Serializable {

    @Serial
    private static final long serialVersionUID = -3460514827972751201L;

    @NotNull
    @Size(min = 1, max = 50)
    @Id
    @Column(name="authority_label", length = 50, unique = true)
    private String authorityLabel;

}
