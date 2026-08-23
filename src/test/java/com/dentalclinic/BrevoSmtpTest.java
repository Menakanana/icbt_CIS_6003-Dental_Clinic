package com.dentalclinic;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class BrevoSmtpTest {

    @Test
    public void testBrevoSmtpConnection() {
        System.out.println("=================================================");
        System.out.println("[BREVO-DIAGNOSTIC] Testing Live SMTP Connection to Brevo...");

        String host = "smtp-relay.brevo.com";
        int port = 587;
        String username = "";
        String password = "";
        String fromEmail = "";

        File envFile = new File(".env");
        if (envFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("SPRING_MAIL_HOST=")) host = line.substring("SPRING_MAIL_HOST=".length()).trim();
                    if (line.startsWith("SPRING_MAIL_PORT=")) port = Integer.parseInt(line.substring("SPRING_MAIL_PORT=".length()).trim());
                    if (line.startsWith("SPRING_MAIL_USERNAME=")) username = line.substring("SPRING_MAIL_USERNAME=".length()).trim();
                    if (line.startsWith("SPRING_MAIL_PASSWORD=")) password = line.substring("SPRING_MAIL_PASSWORD=".length()).trim();
                    if (line.startsWith("SPRING_MAIL_FROM=")) fromEmail = line.substring("SPRING_MAIL_FROM=".length()).trim();
                }
            } catch (Exception ex) {
                System.err.println("Error reading .env: " + ex.getMessage());
            }
        }

        System.out.println("[BREVO-DIAGNOSTIC] Host: " + host + ", Port: " + port);
        System.out.println("[BREVO-DIAGNOSTIC] Username: " + username);
        System.out.println("[BREVO-DIAGNOSTIC] From: " + fromEmail);
        System.out.println("[BREVO-DIAGNOSTIC] Password Length: " + (password != null ? password.length() : 0));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "true"); // Enable full JavaMail SMTP debug protocol logging!

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Sunrise Dental Test");
            helper.setTo(fromEmail);
            helper.setSubject("Sunrise Dental Clinic - Diagnostic Brevo Test Email");
            helper.setText("<h1>Brevo SMTP Test Success</h1><p>If you see this, real SMTP email delivery works!</p>", true);

            mailSender.send(message);
            System.out.println("=================================================");
            System.out.println("[BREVO-DIAGNOSTIC] SUCCESS! Email sent successfully via Brevo SMTP!");
            System.out.println("=================================================");
        } catch (Exception ex) {
            System.err.println("=================================================");
            System.err.println("[BREVO-DIAGNOSTIC] FAILED! SMTP Exception: " + ex.getMessage());
            System.err.println("=================================================");
        }

        System.out.println("=================================================");
        System.out.println("[BREVO-DIAGNOSTIC] Testing Live REST API Connection to Brevo...");
        try {
            String jsonPayload = """
                {
                  "sender": {"name": "Sunrise Dental Test", "email": "%s"},
                  "to": [{"email": "%s"}],
                  "subject": "Sunrise Dental Clinic - Diagnostic Brevo REST API Test Email",
                  "htmlContent": "<h1>Brevo REST API Test Success</h1><p>If you see this, real REST API email delivery works!</p>"
                }
                """.formatted(fromEmail, fromEmail);

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", password)
                    .header("content-type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.out.println("[BREVO-DIAGNOSTIC] REST API Response Code: " + response.statusCode());
            System.out.println("[BREVO-DIAGNOSTIC] REST API Response Body: " + response.body());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("=================================================");
                System.out.println("[BREVO-DIAGNOSTIC] SUCCESS! Email sent successfully via Brevo REST API!");
                System.out.println("=================================================");
            } else {
                System.err.println("[BREVO-DIAGNOSTIC] FAILED! Brevo REST API error code: " + response.statusCode());
            }
        } catch (Exception ex) {
            System.err.println("[BREVO-DIAGNOSTIC] REST API Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
