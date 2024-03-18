package com.fb.auth.service;

import com.fb.auth.entity.User;

public interface EmailService {
    void sendKeyActivation(User user);
}
