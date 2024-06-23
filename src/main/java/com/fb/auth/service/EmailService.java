package com.fb.auth.service;

import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.User;

public interface EmailService {
    void sendWelcomeEmail(User user);
    void sendActivationKey(User user, AccountActivation accountActivation);
    void sendPasswordResetEmail(User user);
}
