package com.fb.auth.service;

import com.fb.auth.dao.ProfilePhotoRepository;
import com.fb.auth.entity.ProfilePhoto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service @AllArgsConstructor @Transactional
public class UserProfilePhotoServiceImpl implements UserProfilePhotoService {

    private final ProfilePhotoRepository profilePhotoRepository;

    /**
     * Sauvegarde un fichier de photo de profil dans la base de données
     *
     * @param file le fichier de la photo de profil à sauvegarder
     * @return l'entité ProfilePhoto sauvegardée
     */
    @Override
    public ProfilePhoto save(MultipartFile file) {
        // crée une nouvelle instance de ProfilePhoto
        ProfilePhoto profilePhoto = new ProfilePhoto();
        // assigne le nom du fichier à partir du nom original du fichier uploadé
        profilePhoto.setFileName(file.getOriginalFilename());
        // assigne le type du fichier à partir du type de contenu du fichier uploadé
        profilePhoto.setFileType(file.getContentType());
        try {
            // assigne le contenu du fichier en tant que tableau de bytes
            profilePhoto.setPhoto(file.getBytes());
        } catch (IOException e) {
            // gère l'exception IOException si la lecture des bytes échoue
            e.printStackTrace();
        }
        // sauvegarde l'instance de ProfilePhoto dans le repository et retourne l'objet sauvegardé
        return profilePhotoRepository.save(profilePhoto);
    }
}
