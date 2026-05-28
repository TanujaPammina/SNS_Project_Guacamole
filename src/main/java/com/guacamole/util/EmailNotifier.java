package com.guacamole.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * Sends email alerts for security events (failed logins, after-hours access,
 * suspicious activity).
 *
 * Configure via system properties:
 *   -Dguac.mail.host=smtp.example.com
 *   -Dguac.mail.port=587
 *   -Dguac.mail.user=alerts@example.com
 *   -Dguac.mail.password=secret
 *   -Dguac.mail.from=alerts@example.com
 *   -Dguac.mail.to=admin@example.com
 */
public class EmailNotifier {

    private static final String SMTP_HOST = System.getProperty("guac.mail.host",     "localhost");
    private static final String SMTP_PORT = System.getProperty("guac.mail.port",     "587");
    private static final String MAIL_USER = System.getProperty("guac.mail.user",     "");
    private static final String MAIL_PASS = System.getProperty("guac.mail.password", "");
    private static final String MAIL_FROM = System.getProperty("guac.mail.from",     "guacamole-admin@localhost");
    private static final String MAIL_TO   = System.getProperty("guac.mail.to",       "");

    public static void sendAlert(String subject, String body) {
        if (MAIL_TO.isBlank()) {
            System.out.println("[EmailNotifier] No recipient configured — skipping alert: " + subject);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            SMTP_PORT);
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USER, MAIL_PASS);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(MAIL_FROM));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MAIL_TO));
            msg.setSubject("[Guacamole Admin Alert] " + subject);
            msg.setText(body);
            Transport.send(msg);
        } catch (MessagingException e) {
            System.err.println("[EmailNotifier] Failed to send alert: " + e.getMessage());
        }
    }

    private EmailNotifier() {}
}
