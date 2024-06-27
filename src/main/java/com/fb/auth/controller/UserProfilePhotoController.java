package com.fb.auth.controller;

import com.fb.auth.service.UserProfilePhotoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path="profilePhoto")
@AllArgsConstructor @Slf4j
public class UserProfilePhotoController {

    private UserProfilePhotoService userProfilePhotoService;

    /**
     * Sauvegarde un fichier de photo de profil dans la base de données
     *
     * @param file le fichier de la photo de profil à sauvegarder
     */
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public void save(@RequestParam("profilePhoto") MultipartFile file) {
        userProfilePhotoService.save(file);
    }
}
