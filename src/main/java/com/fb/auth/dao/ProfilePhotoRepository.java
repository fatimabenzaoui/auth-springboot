package com.fb.auth.dao;

import com.fb.auth.entity.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
    /**
     * Recherche une photo de profil avec le libellé de la photo spécifié
     *
     * @param fileName Le libellé de la photo à rechercher
     * @return La photo de profil correspondant au libellé de la photo spécifié, ou null si aucune photo n'est trouvée
     */
    ProfilePhoto findByFileName(String fileName);
}
