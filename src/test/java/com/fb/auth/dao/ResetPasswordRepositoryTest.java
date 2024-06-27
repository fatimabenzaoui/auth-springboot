package com.fb.auth.dao;

import com.fb.auth.entity.ResetPassword;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ResetPasswordRepositoryTest {

    @Autowired
    ResetPasswordRepository resetPasswordRepository;

    /**
     * Teste la méthode findByResetKey de ResetPasswordRepository
     * Vérifie que la méthode retourne une entité ResetPassword lorsque celle-ci existe dans la base de données avec la clé de réinitialisation du mot de passe spécifiée
     */
    @Test
    void findByResetKey() {
        String resetKey = "reset-key";
        ResetPassword expectedResetPassword = new ResetPassword();
        expectedResetPassword.setResetKey(resetKey);
        expectedResetPassword.setExpirationDate(Instant.now());
        resetPasswordRepository.save(expectedResetPassword);

        Optional<ResetPassword> foundResetPassword = Optional.ofNullable(resetPasswordRepository.findByResetKey(resetKey));

        assertTrue(foundResetPassword.isPresent());
        assertEquals(resetKey, foundResetPassword.get().getResetKey());
    }
}