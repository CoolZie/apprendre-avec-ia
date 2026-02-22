package com.exercice1.security.security;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
    
    /**
     * Simule l'envoi d'un email de vérification
     * En production : utiliser JavaMailSender ou SendGrid/Mailgun
     */
    public void sendVerificationEmail(String to, String token) {
        String verificationLink = "http://localhost:8080/api/auth/verify/" + token;
        
        log.info("╔════════════════════════════════════════╗");
        log.info("║     EMAIL DE VÉRIFICATION (SIMULATION)      ║");
        log.info("╠════════════════════════════════════════╣");
        log.info("║ To: {}", to);
        log.info("║ Subject: Vérifiez votre adresse email");
        log.info("║ ");
        log.info("║ Bonjour,");
        log.info("║ ");
        log.info("║ Cliquez sur le lien pour vérifier votre email :");
        log.info("║ {}", verificationLink);
        log.info("║ ");
        log.info("║ Ce lien expire dans 24 heures.");
        log.info("╚════════════════════════════════════════╝");
    }
    
    /**
     * Simule l'envoi d'un email de notification de changement de password
     */
    public void sendPasswordChangedEmail(String to, String username) {
        log.info("╔════════════════════════════════════════╗");
        log.info("║   MOT DE PASSE MODIFIÉ (SIMULATION)    ║");
        log.info("╠════════════════════════════════════════╣");
        log.info("║ To: {}", to);
        log.info("║ Subject: Votre mot de passe a été modifié");
        log.info("║ ");
        log.info("║ Bonjour {},", username);
        log.info("║ ");
        log.info("║ Votre mot de passe a été modifié avec succès.");
        log.info("║ ");
        log.info("║ Si vous n'êtes pas à l'origine de ce changement,");
        log.info("║ contactez immédiatement le support.");
        log.info("╚════════════════════════════════════════╝");
    }
}