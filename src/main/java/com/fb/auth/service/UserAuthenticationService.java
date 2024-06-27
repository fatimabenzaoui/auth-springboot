package com.fb.auth.service;

import com.fb.auth.dto.UserAuthenticationDTO;
import com.fb.auth.dto.UserDetailsUpdateDTO;

import java.util.Map;

public interface UserAuthenticationService {
    Map<String, String> authenticate(UserAuthenticationDTO userAuthenticationDTO);
    void requestResetPassword(String email);
    void resetPassword(String passwordResetKey, String newPassword, String confirmPassword);
    void updatePassword(String currentPassword, String newPassword);
    void updateUserDetails(UserDetailsUpdateDTO userDetailsUpdateDTO);
}
