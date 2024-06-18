package com.fb.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.Serial;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Model User
 */
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode(callSuper = false) @Builder
@Table(name="users")
public class User extends Audit implements UserDetails {

    @Serial
    private static final long serialVersionUID = -7265724107958157470L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Size(min = 6, max = 100)
    @Column(length = 100, nullable = false)
    private String password;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Email
    @NotNull
    @Size(min = 5, max = 256)
    @Column(length = 256, unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean activated = false;

    @Size(max = 20)
    @Column(length = 20)
    @JsonIgnore
    private String activationKey;
    private Instant activationKeyExpiration;

    @Size(max = 36)
    @Column(length = 36)
    @JsonIgnore
    private String passwordResetKey;
    private Instant passwordResetKeyExpiration;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_authorities",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "authority_label", referencedColumnName = "authority_label") }
    )
    private Set<Authority> authorities = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getAuthorityLabel())).toList();
    }

    @Override
    @NotNull
    public String getPassword() {
        return this.password;
    }

    @Override
    @NotNull
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.activated;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.activated;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.activated;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }

}
