package com.fb.auth.dao;

import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.User;
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
class AccountActivationRepositoryTest {

    @Autowired
    AccountActivationRepository accountActivationRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Teste la méthode findByActivationKey de AccountActivationRepository
     * Vérifie que la méthode retourne une entité AccountActivation lorsque celle-ci existe dans la base de données avec la clé d'activation spécifiée
     */
    @Test
    void findByActivationKey() {
        String activationKey = "activation-key";
        AccountActivation expectedAccountActivation = new AccountActivation();
        expectedAccountActivation.setActivationKey(activationKey);
        expectedAccountActivation.setExpirationDate(Instant.now());
        accountActivationRepository.save(expectedAccountActivation);

        Optional<AccountActivation> foundAccountActivation = Optional.ofNullable(accountActivationRepository.findByActivationKey(activationKey));

        assertTrue(foundAccountActivation.isPresent());
        assertEquals(activationKey, foundAccountActivation.get().getActivationKey());
    }

    /**
     * Teste la méthode findByUser de AccountActivationRepository
     * Vérifie que la méthode retourne une entité AccountActivation lorsque celle-ci existe dans la base de données pour l'utilisateur spécifié
     */
    @Test
    void findByUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedBy("system");
        userRepository.save(user);
        AccountActivation expectedAccountActivation = new AccountActivation();
        expectedAccountActivation.setUser(user);
        expectedAccountActivation.setActivationKey("activation-key");
        expectedAccountActivation.setExpirationDate(Instant.now());
        accountActivationRepository.save(expectedAccountActivation);

        Optional<AccountActivation> foundAccountActivation = Optional.ofNullable(accountActivationRepository.findByUser(user));

        assertTrue(foundAccountActivation.isPresent());
        assertEquals(user, foundAccountActivation.get().getUser());
    }

    /**
     * Teste la méthode deleteByUser de AccountActivationRepository
     * Vérifie que la méthode supprime une entité AccountActivation associée à l'utilisateur spécifié
     */
    @Test
    void deleteByUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedBy("system");
        userRepository.save(user);
        AccountActivation expectedAccountActivation = new AccountActivation();
        expectedAccountActivation.setUser(user);
        expectedAccountActivation.setActivationKey("activation-key");
        expectedAccountActivation.setExpirationDate(Instant.now());
        accountActivationRepository.save(expectedAccountActivation);

        accountActivationRepository.deleteByUser(user);
        Optional<AccountActivation> foundAccountActivation = Optional.ofNullable(accountActivationRepository.findByUser(user));

        assertFalse(foundAccountActivation.isPresent());
    }
}