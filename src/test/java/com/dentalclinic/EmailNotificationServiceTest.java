package com.dentalclinic;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    private AppointmentTicketDTO ticket;
    private BillingDTO bill;

    @BeforeEach
    public void setUp() {
        ticket = new AppointmentTicketDTO(
                1,
                1001,
                1,
                101,
                "John Doe",
                "0771234567",
                "199012345678",
                1,
                "Dr. Sarah Chen",
                "Orthodontics",
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                "09:00 AM - 09:30 AM",
                "BOOKED",
                new BigDecimal("1500.00")
        );

        bill = new BillingDTO(
                5001,
                1,
                1,
                "John Doe",
                "0771234567",
                "Dr. Sarah Chen",
                "Orthodontics",
                "General Dental Consultation",
                LocalDate.now(),
                new BigDecimal("1500.00"),
                new BigDecimal("500.00"),
                new BigDecimal("2500.00"),
                new BigDecimal("500.00"),
                new BigDecimal("4000.00")
        );
    }

    @Test
    @DisplayName("Unit Test: Send Appointment Booking Confirmation Email Log Mode")
    public void testSendBookingConfirmationEmail() {
        boolean result = emailNotificationService.sendBookingConfirmationEmail(ticket, "johndoe@gmail.com");
        assertTrue(result);
    }

    @Test
    @DisplayName("Unit Test: Send Payment Receipt Email Log Mode")
    public void testSendBillingReceiptEmail() {
        boolean result = emailNotificationService.sendBillingReceiptEmail(bill, "johndoe@gmail.com");
        assertTrue(result);
    }
}
