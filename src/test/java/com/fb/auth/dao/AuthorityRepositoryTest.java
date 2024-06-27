package com.fb.auth.dao;

import com.fb.auth.entity.Authority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class AuthorityRepositoryTest {

    @Autowired
    AuthorityRepository authorityRepository;

    /**
     * Teste la méthode findByAuthorityLabel de AuthorityRepository
     * Vérifie que la méthode retourne un libellé de rôle lorsque celui-ci existe dans la base de données avec le libellé du rôle spécifié
     */
    @Test
    void findByAuthorityLabel() {
        String authorityLabel = "ROLE_ADMIN";
        Authority expectedAuthority = new Authority();
        expectedAuthority.setAuthorityLabel(authorityLabel);
        authorityRepository.save(expectedAuthority);

        Optional<Authority> foundAuthority = Optional.ofNullable(authorityRepository.findByAuthorityLabel(authorityLabel));

        assertTrue(foundAuthority.isPresent());
        assertEquals(authorityLabel, foundAuthority.get().getAuthorityLabel());
    }
}