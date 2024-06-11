package com.fb.auth.service;

import com.fb.auth.dto.UserAuthenticationDTO;
import java.util.Map;

public interface UserAuthenticationService {
    Map<String, String> authenticate(UserAuthenticationDTO userAuthenticationDTO);
}
