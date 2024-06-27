package com.fb.auth.dao;

import com.fb.auth.entity.ProfilePhoto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ProfilePhotoRepositoryTest {

    @Autowired
    ProfilePhotoRepository profilePhotoRepository;

    /**
     * Teste la méthode findByFileName de ProfilePhotoRepository
     * Vérifie que la méthode retourne une photo de profil lorsque celle-ci existe dans la base de données avec le libellé de la photo de profil spécifié
     */
    @Test
    void findByFileName() {
        String fileName = "profile_pic.jpg";
        ProfilePhoto expectedProfilePhoto = new ProfilePhoto();
        expectedProfilePhoto.setFileName(fileName);
        profilePhotoRepository.save(expectedProfilePhoto);

        Optional<ProfilePhoto> foundProfilePhoto = Optional.ofNullable(profilePhotoRepository.findByFileName(fileName));

        assertTrue(foundProfilePhoto.isPresent());
        assertEquals(fileName, foundProfilePhoto.get().getFileName());
    }
}