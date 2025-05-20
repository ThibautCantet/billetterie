package com.cantet.thibaut.payment.infrastructure.service;

import com.cantet.thibaut.payment.domain.CustomerSupport;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailCustomerSupport implements CustomerSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailCustomerSupport.class);

    private final EmailConfiguration emailConfiguration;

    public EmailCustomerSupport(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public void alertTransactionFailure(String transactionId, String cartId, Float amount) {
        try {
            var mailSender = initMailSender();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom("site-billetterie@billetterie.com", "Alerte site billetterie");
            helper.setTo("support@billetterie.com");
            helper.setSubject(String.format("Alerte: transaction %s échouée", transactionId));
            helper.setText(String.format("""
                    La transaction %s d'un montant de %s€ a échoué. Veuillez l'annuler manuellement.
                    """, transactionId, amount), false);

            mailSender.send(message);
            LOGGER.info("Email sent successfully to customer support");
        } catch (Exception e) {
            LOGGER.error("Error while sending email", e);
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
