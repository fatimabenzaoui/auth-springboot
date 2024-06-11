package com.fb.auth.service;

import com.fb.auth.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> findAll();
}
