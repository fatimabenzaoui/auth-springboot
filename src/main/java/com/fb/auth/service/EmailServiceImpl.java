package com.fb.auth.service;

import com.fb.auth.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * Envoie par email la clé d'activation à l'utilisateur spécifié
     *
     * @param user L'utilisateur à qui envoyer la clé d'activation
     */
    @Override
    public void sendKeyActivation(User user) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("no-reply@cabinetpoetique.com");
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Your activation key");
        String message = String.format(
                "Hello %s <br> Your activation key is : %s <br> See you soon.",
                user.getUsername(),
                user.getActivationKey()
        );
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
    }
}
