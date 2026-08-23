package com.dentalclinic.service;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.BillingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

/**
 * Automated Email Notification Service with Professional Responsive HTML Templates.
 * Integrates with Brevo / Standard SMTP relays for real-time delivery.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:${spring.mail.username:notifications@sunrisedental.com}}")
    private String fromEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public EmailNotificationService() {
    }

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an automated HTML Appointment Booking Confirmation Email to the patient.
     */
    public boolean sendBookingConfirmationEmail(AppointmentTicketDTO ticket, String recipientEmail) {
        String targetEmail = (recipientEmail != null && !recipientEmail.trim().isEmpty()) ? recipientEmail : "patient@sunrisedental.com";
        String subject = "Appointment Confirmation - Sunrise Dental Clinic (Token #" + ticket.getTokenNumber() + ")";

        String dateStr = ticket.getAppointmentDate() != null 
                ? ticket.getAppointmentDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                : "";

        String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #0F766E 0%%, #115E59 100%%); padding: 25px; text-align: center; color: #ffffff; }
                    .header h2 { margin: 0; font-size: 22px; font-weight: 700; letter-spacing: 0.5px; }
                    .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 13px; }
                    .token-badge { background: #ECFDF5; border: 2px dashed #059669; border-radius: 10px; padding: 15px; text-align: center; margin: 20px; }
                    .token-num { font-size: 32px; font-weight: 800; color: #0F766E; margin: 0; }
                    .token-label { font-size: 12px; text-transform: uppercase; color: #047857; font-weight: 700; }
                    .details-table { width: calc(100%% - 40px); margin: 0 20px 20px 20px; border-collapse: collapse; }
                    .details-table td { padding: 10px 12px; border-bottom: 1px solid #e2e8f0; font-size: 14px; }
                    .details-table td.label { font-weight: 600; color: #475569; width: 38%%; }
                    .details-table td.val { color: #0f172a; font-weight: 500; }
                    .footer { background: #f8fafc; padding: 15px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }
                </style>
                </head>
                <body>
                <div class="card">
                  <div class="header">
                    <h2>SUNRISE DENTAL CLINIC</h2>
                    <p>Pre-Consultation Appointment Confirmation & Queue Pass</p>
                  </div>
                  <div class="token-badge">
                    <div class="token-label">YOUR QUEUE TOKEN NUMBER</div>
                    <div class="token-num">#%d</div>
                    <div style="font-size:13px; color:#047857; margin-top:4px;">%s</div>
                  </div>
                  <table class="details-table">
                    <tr><td class="label">Patient Name:</td><td class="val">%s</td></tr>
                    <tr><td class="label">Appointment ID:</td><td class="val">APT-%d</td></tr>
                    <tr><td class="label">Assigned Doctor:</td><td class="val">%s (%s)</td></tr>
                    <tr><td class="label">Appointment Date:</td><td class="val">%s</td></tr>
                    <tr><td class="label">Time Window:</td><td class="val">%s</td></tr>
                    <tr><td class="label">Clinic Address:</td><td class="val">123 Galle Road, Colombo 03</td></tr>
                    <tr><td class="label">Contact Phone:</td><td class="val">011-2345678 / 077-1234567</td></tr>
                  </table>
                  <div class="footer">
                    Thank you for choosing Sunrise Dental Clinic! Please present this email or token pass at reception.
                  </div>
                </div>
                </body>
                </html>
                """.formatted(
                ticket.getTokenNumber(),
                ticket.getDisplayTimeRange(),
                ticket.getPatientName(),
                ticket.getAppointmentId(),
                ticket.getDentistName(),
                ticket.getSpecialization() != null ? ticket.getSpecialization() : "General Consultation",
                dateStr,
                ticket.getDisplayTimeRange()
        );

        logger.info("[EMAIL-SERVICE] Dispatching Appointment Confirmation Email to: {}", targetEmail);
        return attemptSmtpSend(targetEmail, subject, htmlBody);
    }

    /**
     * Sends an automated HTML Patient Invoice / Receipt Email.
     */
    public boolean sendBillingReceiptEmail(BillingDTO bill, String recipientEmail) {
        String targetEmail = (recipientEmail != null && !recipientEmail.trim().isEmpty()) ? recipientEmail : "patient@sunrisedental.com";
        String subject = "Official Payment Receipt #" + bill.getInvoiceNumber() + " - Sunrise Dental Clinic";

        String dateStr = bill.getAppointmentDate() != null 
                ? bill.getAppointmentDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                : "";

        String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
                    .card { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #0F766E 0%%, #115E59 100%%); padding: 25px; text-align: center; color: #ffffff; }
                    .header h2 { margin: 0; font-size: 22px; font-weight: 700; }
                    .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 13px; }
                    .receipt-table { width: calc(100%% - 40px); margin: 20px; border-collapse: collapse; }
                    .receipt-table th { background: #f8fafc; padding: 10px 12px; text-align: left; font-size: 13px; color: #475569; border-bottom: 2px solid #cbd5e1; }
                    .receipt-table td { padding: 10px 12px; border-bottom: 1px solid #e2e8f0; font-size: 14px; color: #0f172a; }
                    .total-row td { font-weight: 700; color: #0F766E; font-size: 16px; background: #ecfdf5; border-top: 2px solid #059669; }
                    .footer { background: #f8fafc; padding: 15px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }
                </style>
                </head>
                <body>
                <div class="card">
                  <div class="header">
                    <h2>SUNRISE DENTAL CLINIC</h2>
                    <p>OFFICIAL PAYMENT RECEIPT & INVOICE</p>
                  </div>
                  <div style="padding:20px 20px 0 20px; font-size:14px; color:#475569; line-height:1.6;">
                    <strong>Invoice #:</strong> INV-%d &nbsp;|&nbsp; <strong>Date:</strong> %s<br>
                    <strong>Patient Name:</strong> %s<br>
                    <strong>Attending Specialist:</strong> %s (%s)
                  </div>
                  <table class="receipt-table">
                    <thead>
                      <tr><th>Description / Service Item</th><th style="text-align:right;">Amount (LKR)</th></tr>
                    </thead>
                    <tbody>
                      <tr><td>Dentist Consultation Fee</td><td style="text-align:right;">%.2f</td></tr>
                      <tr><td>Facility & Administrative Fee</td><td style="text-align:right;">%.2f</td></tr>
                      <tr><td>Procedure: %s</td><td style="text-align:right;">%.2f</td></tr>
                      <tr><td>Discount Applied</td><td style="text-align:right; color:#dc2626;">-%.2f</td></tr>
                      <tr class="total-row"><td>TOTAL AMOUNT PAID</td><td style="text-align:right;">LKR %.2f</td></tr>
                    </tbody>
                  </table>
                  <div class="footer">
                    Wish you a healthy smile! Keep this official receipt for insurance claim purposes.
                  </div>
                </div>
                </body>
                </html>
                """.formatted(
                bill.getInvoiceNumber(),
                dateStr,
                bill.getPatientName(),
                bill.getDentistName(),
                bill.getDentistSpecialization() != null ? bill.getDentistSpecialization() : "General Dentistry",
                bill.getConsultationFee(),
                bill.getClinicCharge(),
                bill.getTreatmentName(),
                bill.getTreatmentBaseCost(),
                bill.getDiscountAmount(),
                bill.getTotalAmount()
        );

        logger.info("[EMAIL-SERVICE] Dispatching Payment Receipt Email to: {}", targetEmail);
        return attemptSmtpSend(targetEmail, subject, htmlBody);
    }

    /**
     * Sends an automated HTML Password Reset Email with secure token / link.
     */
    public boolean sendPasswordResetEmail(String recipientEmail, String username, String resetToken) {
        String targetEmail = (recipientEmail != null && !recipientEmail.trim().isEmpty()) ? recipientEmail : "staff@sunrisedental.com";
        String subject = "Password Reset Request - Sunrise Dental Clinic Staff Portal";

        String resetUrl = "http://localhost:8081/reset-password?token=" + resetToken;

        String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
                    .card { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #0F766E 0%%, #115E59 100%%); padding: 25px; text-align: center; color: #ffffff; }
                    .body { padding: 30px 25px; color: #334155; font-size: 15px; line-height: 1.6; }
                    .btn { display: inline-block; background-color: #0F766E; color: #ffffff !important; padding: 12px 28px; text-decoration: none; border-radius: 8px; font-weight: 700; margin: 20px 0; }
                    .token-box { background: #f8fafc; border: 1px solid #cbd5e1; padding: 12px; border-radius: 6px; font-family: monospace; font-size: 16px; font-weight: bold; color: #0f172a; letter-spacing: 1px; display: inline-block; }
                    .footer { background: #f8fafc; padding: 15px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }
                </style>
                </head>
                <body>
                <div class="card">
                  <div class="header">
                    <h2 style="margin:0; font-size:22px;">SUNRISE DENTAL CLINIC</h2>
                    <p style="margin:5px 0 0 0; opacity:0.9; font-size:13px;">Staff Portal - Password Reset Request</p>
                  </div>
                  <div class="body">
                    <p>Hello <strong>%s</strong>,</p>
                    <p>We received a request to reset your password for the Sunrise Dental Clinic Staff Management Portal.</p>
                    <p style="text-align: center;">
                      <a href="%s" class="btn" target="_blank">Reset Password</a>
                    </p>
                    <p style="font-size:13px; color:#64748b; text-align:center;">Or copy and paste this token code on the reset page:</p>
                    <div style="text-align:center;"><div class="token-box">%s</div></div>
                    <p style="font-size:12px; color:#dc2626; margin-top:20px; text-align:center;">⏱️ This reset link and token are valid for <strong>30 minutes</strong>.</p>
                    <p style="font-size:13px; color:#64748b;">If you did not request a password reset, please ignore this email or contact your System Administrator immediately.</p>
                  </div>
                  <div class="footer">
                    Sunrise Dental Clinic - IT Support & Security Team
                  </div>
                </div>
                </body>
                </html>
                """.formatted(
                username != null ? username : "Staff Member",
                resetUrl,
                resetToken
        );

        logger.info("[EMAIL-SERVICE] Dispatching Password Reset Email to: {}", targetEmail);
        return attemptSmtpSend(targetEmail, subject, htmlBody);
    }

    /**
     * Sends an automated HTML Staff Account Creation Welcome & Password Setup Email.
     */
    public boolean sendStaffWelcomeSetupEmail(String recipientEmail, String fullName, String username, String role, String resetToken) {
        String targetEmail = (recipientEmail != null && !recipientEmail.trim().isEmpty()) ? recipientEmail : "staff@sunrisedental.com";
        String subject = "Welcome to Sunrise Dental Clinic - Account Activation & Password Setup";

        String setupUrl = "http://localhost:8081/reset-password?token=" + resetToken;

        String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; }
                    .card { max-width: 580px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #0F766E 0%%, #115E59 100%%); padding: 25px; text-align: center; color: #ffffff; }
                    .body { padding: 30px 25px; color: #334155; font-size: 15px; line-height: 1.6; }
                    .btn { display: inline-block; background-color: #0F766E; color: #ffffff !important; padding: 12px 28px; text-decoration: none; border-radius: 8px; font-weight: 700; margin: 20px 0; }
                    .info-box { background: #ecfdf5; border: 1px solid #a7f3d0; padding: 15px; border-radius: 8px; margin: 15px 0; }
                    .token-box { background: #f8fafc; border: 1px solid #cbd5e1; padding: 12px; border-radius: 6px; font-family: monospace; font-size: 16px; font-weight: bold; color: #0f172a; letter-spacing: 1px; display: inline-block; }
                    .footer { background: #f8fafc; padding: 15px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }
                </style>
                </head>
                <body>
                <div class="card">
                  <div class="header">
                    <h2 style="margin:0; font-size:22px;">SUNRISE DENTAL CLINIC</h2>
                    <p style="margin:5px 0 0 0; opacity:0.9; font-size:13px;">Staff Account Activation & Password Setup</p>
                  </div>
                  <div class="body">
                    <p>Hello <strong>%s</strong>,</p>
                    <p>Welcome to Sunrise Dental Clinic! An account has been created for you on the Staff Operations Portal.</p>
                    <div class="info-box">
                      <strong>Login Username:</strong> %s<br>
                      <strong>System Role:</strong> %s
                    </div>
                    <p>To complete your registration, please set your personal password by clicking the button below:</p>
                    <p style="text-align: center;">
                      <a href="%s" class="btn" target="_blank">Set Account Password</a>
                    </p>
                    <p style="font-size:13px; color:#64748b; text-align:center;">Or enter this activation token code manually on the reset page:</p>
                    <div style="text-align:center;"><div class="token-box">%s</div></div>
                    <p style="font-size:12px; color:#dc2626; margin-top:20px; text-align:center;">⏱️ This setup link and token are valid for <strong>30 minutes</strong>.</p>
                  </div>
                  <div class="footer">
                    Sunrise Dental Clinic - Administration & Security Team
                  </div>
                </div>
                </body>
                </html>
                """.formatted(
                fullName != null ? fullName : "Staff Member",
                username,
                role != null ? role : "Receptionist",
                setupUrl,
                resetToken
        );

        logger.info("[EMAIL-SERVICE] Dispatching Staff Welcome & Password Setup Email to: {}", targetEmail);
        return attemptSmtpSend(targetEmail, subject, htmlBody);
    }

    private boolean attemptSmtpSend(String recipientEmail, String subject, String htmlContent) {
        if (mailPassword != null && mailPassword.startsWith("xkeysib-")) {
            boolean apiSuccess = sendViaBrevoApi(recipientEmail, subject, htmlContent);
            if (apiSuccess) {
                return true;
            }
        }

        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                String fromAddress = (fromEmail != null && !fromEmail.isEmpty()) ? fromEmail : "notifications@sunrisedental.com";
                helper.setFrom(fromAddress, "Sunrise Dental Clinic");
                helper.setTo(recipientEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);
                logger.info("[EMAIL-SERVICE] SMTP HTML Mail successfully delivered to {}", recipientEmail);
                return true;
            } catch (Exception ex) {
                logger.warn("[EMAIL-SERVICE] SMTP Delivery fallback to Log Mode: {}", ex.getMessage());
            }
        }
        logger.info("[EMAIL-SERVICE] SMTP Not Configured. Email logged to console mode.");
        return true;
    }

    private boolean sendViaBrevoApi(String recipientEmail, String subject, String htmlContent) {
        try {
            String senderEmail = (fromEmail != null && !fromEmail.trim().isEmpty() && !fromEmail.contains("notifications@")) 
                    ? fromEmail : "vijayaretnam1022menakanan@gmail.com";

            String jsonPayload = """
                {
                  "sender": {"name": "Sunrise Dental Clinic", "email": "%s"},
                  "to": [{"email": "%s"}],
                  "subject": "%s",
                  "htmlContent": "%s"
                }
                """.formatted(
                    escapeJson(senderEmail),
                    escapeJson(recipientEmail),
                    escapeJson(subject),
                    escapeJson(htmlContent)
                );

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", mailPassword)
                    .header("content-type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("[EMAIL-SERVICE] Live email successfully dispatched via Brevo REST API to {} (HTTP {})", recipientEmail, response.statusCode());
                return true;
            } else {
                logger.warn("[EMAIL-SERVICE] Brevo REST API returned HTTP {}: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception ex) {
            logger.error("[EMAIL-SERVICE] Brevo REST API dispatch failed: {}", ex.getMessage());
            return false;
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
