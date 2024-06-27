package com.fb.auth.dao;

import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private AccountActivationRepository accountActivationRepository;

    /**
     * Teste la méthode existsByUsername de UserRepository
     * Vérifie que la méthode retourne true lorsqu'un utilisateur avec le nom d'utilisateur spécifié existe dans la base de données
     */
    @Test
    void existsByUsername() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedBy("system");
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername(username);

        assertTrue(exists);
    }

    /**
     * Teste la méthode existsByEmail de UserRepository
     * Vérifie que la méthode retourne true lorsqu'un utilisateur avec l'adresse email spécifiée existe dans la base de données
     */
    @Test
    void existsByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail(email);
        user.setCreatedBy("system");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail(email);

        assertTrue(exists);
    }

    /**
     * Teste la méthode findByUsername de UserRepository
     * Vérifie que la méthode retourne un utilisateur lorsque celui-ci existe dans la base de données avec le nom d'utilisateur spécifié
     */
    @Test
    void findByUsername() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedBy("system");
        userRepository.save(user);

        Optional<User> foundUser = Optional.ofNullable(userRepository.findByUsername(username));

        assertTrue(foundUser.isPresent());
        assertEquals(username, foundUser.get().getUsername());
    }

    /**
     * Teste la méthode findAllNotActivatedUsersWithActivationKey de UserRepository
     * Vérifie que la méthode retourne uniquement les utilisateurs non activés avec des clés d'activation expirées
     */
    @Test
    void findAllNotActivatedUsersWithActivationKey() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("password1");
        user1.setEmail("user1@example.com");
        user1.setCreatedBy("system");
        user1.setActivated(false);
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password2");
        user2.setEmail("user2@example.com");
        user2.setCreatedBy("system");
        user2.setActivated(false);
        userRepository.save(user1);
        userRepository.save(user2);
        AccountActivation activation1 = new AccountActivation();
        activation1.setUser(user1);
        activation1.setActivationKey("activationKey1");
        activation1.setExpirationDate(Instant.now().minus(Duration.ofDays(1))); // Expiration passée
        AccountActivation activation2 = new AccountActivation();
        activation2.setUser(user2);
        activation2.setActivationKey("activationKey2");
        activation2.setExpirationDate(Instant.now().plus(Duration.ofDays(1))); // Expiration future
        accountActivationRepository.save(activation1);
        accountActivationRepository.save(activation2);

        Instant currentDate = Instant.now();
        List<User> notActivatedUsers = userRepository.findAllNotActivatedUsersWithActivationKey(currentDate);

        assertEquals(1, notActivatedUsers.size());
        assertEquals("user1", notActivatedUsers.get(0).getUsername());
    }

    /**
     * Teste la méthode findByEmail de UserRepository
     * Vérifie que la méthode retourne un utilisateur lorsque celui-ci existe dans la base de données avec l'adresse email spécifiée
     */
    @Test
    void findByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail(email);
        user.setCreatedBy("system");
        userRepository.save(user);

        Optional<User> foundUser = Optional.ofNullable(userRepository.findByEmail(email));

        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }
}