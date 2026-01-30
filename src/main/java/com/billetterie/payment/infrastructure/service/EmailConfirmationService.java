package com.billetterie.payment.infrastructure.service;

import com.billetterie.payment.domain.ConfirmationService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailConfirmationService implements ConfirmationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfirmationService.class);

    private final EmailConfiguration emailConfiguration;

    public EmailConfirmationService(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public void send(String email, String orderId, float amount) {
        try {
            var mailSender = initMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("site-billetterie@billetterie.com", "Alerte site billetterie");
            helper.setTo(email);
            helper.setSubject(String.format("Confirmation de votre commande %s", orderId));
            helper.setText(String.format("""
                    Nous vous confirmons la commande %s d'un montant de %s€.
                    """, orderId, amount), false);

            mailSender.send(message);
            LOGGER.info("Email sent successfully to client");
        } catch (Exception e) {
            LOGGER.error("Error while sending confirmation email", e);
        }

    }

    private JavaMailSenderImpl initMailSender() {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfiguration.host());
        mailSender.setPort(emailConfiguration.port());
        mailSender.setUsername(emailConfiguration.username());
        mailSender.setPassword(emailConfiguration.password());
        return mailSender;
    }
}
