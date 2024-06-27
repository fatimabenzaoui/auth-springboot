package com.fb.auth.service;

import com.fb.auth.entity.ProfilePhoto;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfilePhotoService {
    ProfilePhoto save(MultipartFile file);
}
