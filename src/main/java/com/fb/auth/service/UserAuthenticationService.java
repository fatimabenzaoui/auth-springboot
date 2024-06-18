package com.fb.auth.service;

import com.fb.auth.dto.UserAuthenticationDTO;
import java.util.Map;

public interface UserAuthenticationService {
    Map<String, String> authenticate(UserAuthenticationDTO userAuthenticationDTO);
    void forgotPassword(String email);
    void resetPassword(String passwordResetKey, String newPassword, String confirmPassword);
    void updatePassword(String currentPassword, String newPassword);
}
