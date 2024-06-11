package com.fb.auth.service;

import com.fb.auth.entity.User;

public interface EmailService {
    void sendActivationKey(User user);
}
