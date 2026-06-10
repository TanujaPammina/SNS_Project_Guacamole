package com.guacamole.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * Sends email notifications — security alerts and password reset emails.
 *
 * Configure via system properties or db.properties equivalent:
 *   -Dguac.mail.host=smtp.gmail.com
 *   -Dguac.mail.port=587
 *   -Dguac.mail.user=your-email@gmail.com
 *   -Dguac.mail.password=your-app-password
 *   -Dguac.mail.from=your-email@gmail.com
 *   -Dguac.mail.to=admin@example.com       (for security alerts only)
 *
 * For Gmail: use an App Password (not your account password).
 * Enable at: https://myaccount.google.com/apppasswords
 */
public class EmailNotifier {

    private static final String SMTP_HOST = getConfig("guac.mail.host",     "smtp.gmail.com");
    private static final String SMTP_PORT = getConfig("guac.mail.port",     "587");
    private static final String MAIL_USER = getConfig("guac.mail.user",     "");
    private static final String MAIL_PASS = getConfig("guac.mail.password", "");
    private static final String MAIL_FROM = getConfig("guac.mail.from",     "");
    private static final String MAIL_TO   = getConfig("guac.mail.to",       "");

    /**
     * Sends a security alert to the configured admin email (guac.mail.to).
     * Used for: failed logins, after-hours access, suspicious activity.
     */
    public static void sendAlert(String subject, String body) {
        if (MAIL_TO.isBlank()) {
            System.out.println("[EmailNotifier] No alert recipient configured — skipping: " + subject);
            return;
        }
        send(MAIL_TO, "[Guacamole Alert] " + subject, body);
    }

    /**
     * Sends a password reset email to a specific recipient.
     * Used for: forgot password flow.
     */
    public static void sendPasswordReset(String toEmail, String subject, String body) {
        send(toEmail, subject, body);
    }

    // ── Core send method ──────────────────────────────────────────────────────

    private static void send(String to, String subject, String body) {
        if (MAIL_USER.isBlank() || MAIL_PASS.isBlank()) {
            System.out.println("[EmailNotifier] Mail not configured — printing to console:");
            System.out.println("  TO:      " + to);
            System.out.println("  SUBJECT: " + subject);
            System.out.println("  BODY:\n" + body);
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            SMTP_PORT);
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust",       SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAIL_USER, MAIL_PASS);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(MAIL_FROM.isBlank() ? MAIL_USER : MAIL_FROM));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("[EmailNotifier] Email sent to: " + to);
        } catch (MessagingException e) {
            System.err.println("[EmailNotifier] Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    private static String getConfig(String key, String defaultValue) {
        // Check system property first, then mail.properties on classpath
        String val = System.getProperty(key);
        if (val != null && !val.isBlank()) return val;
        return defaultValue;
    }

    private EmailNotifier() {}
}
