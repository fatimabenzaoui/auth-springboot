package com.fb.auth.service;

import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.nio.charset.StandardCharsets;

@Service @AllArgsConstructor @Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private static final String USER = "user";
    private static final String ACCOUNT_ACTIVATION = "accountActivation";

    /**
     * Envoie un email avec le sujet et le contenu spécifiés
     *
     * @param to L'adresse email du destinataire
     * @param subject Le sujet de l'email
     * @param content Le contenu de l'email
     * @param isHtml Indique si le contenu est au format HTML
     */
    public void sendEmail(String to, String subject, String content, boolean isHtml) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom("no-reply@cabinetpoetique.com");
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.info("*** EMAIL SENT TO '{}'", to);
        } catch (MessagingException e) {
            log.info("*** EMAIL COULD NOT BE SENT TO '{}'", to, e);
        }
    }

    /**
     * Envoie un email de bienvenue à l'utilisateur spécifié
     *
     * @param user L'utilisateur à qui envoyer l'email de bienvenue
     */
    @Override
    public void sendWelcomeEmail(User user) {
        Context context = new Context();
        context.setVariable(USER, user);
        String content = templateEngine.process("email/welcomeEmail", context);
        String subject = "Bienvenue sur notre plateforme";
        this.sendEmail(user.getEmail(), subject, content, true);
    }

    /**
     * Envoie par email la clé d'activation à l'utilisateur spécifié
     *
     * @param user L'utilisateur à qui envoyer la clé d'activation
     */
    @Override
    public void sendActivationKey(User user, AccountActivation accountActivation) {
        Context context = new Context();
        context.setVariable(USER, user);
        context.setVariable(ACCOUNT_ACTIVATION, accountActivation);
        String content = templateEngine.process("email/activationEmail", context);
        String subject = "Activation de votre compte";
        this.sendEmail(user.getEmail(), subject, content, true);
    }

    /**
     * Envoie par email le lien pour réinitialiser son mot de passe à l'utilisateur spécifié
     *
     * @param user L'utilisateur à qui envoyer le lien pour réinitialiser son mot de passe
     */
    @Override
    public void sendPasswordResetEmail(User user) {
        Context context = new Context();
        context.setVariable(USER, user);
        String content = templateEngine.process("email/passwordResetEmail", context);
        String subject = "Réinitialisation du mot de passe";
        this.sendEmail(user.getEmail(), subject, content, true);
    }
}
