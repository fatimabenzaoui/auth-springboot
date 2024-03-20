package com.fb.auth.service;

import com.fb.auth.dto.UserDTO;

import java.util.Map;

public interface UserService {
    void createAccount(UserDTO userDTO);
    void activateAccount(Map<String, String> activation);
    void requestNewActivationKey(String username);
    void removeNotActivatedAccounts();
}
