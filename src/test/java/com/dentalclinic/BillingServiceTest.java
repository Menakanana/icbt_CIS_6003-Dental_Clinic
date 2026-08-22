package com.dentalclinic;

import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.service.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private BillingService billingService;

    private Appointment sampleAppointment;

    @BeforeEach
    public void setUp() {
        Patient patient = new Patient("Amara Perera", "0771234567", "amara@example.com", "Colombo", "199012345678");
        patient.setPatientId(101);

        Dentist dentist = new Dentist("Dr. Nimal Fernando", "Orthodontics", "0711111111", new BigDecimal("2500.00"));
        dentist.setDentistId(1);

        TreatmentType treatment = new TreatmentType("Teeth Whitening", "Professional whitening treatment", new BigDecimal("5000.00"));
        treatment.setTreatmentTypeId(2);

        sampleAppointment = new Appointment(
                patient,
                dentist,
                treatment,
                null,
                null,
                1,
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                "BOOKED"
        );
        sampleAppointment.setAppointmentId(1);
    }

    @Test
    @DisplayName("Unit Test: calculateBill Returns Accurate Fee Sum (Consultation + Treatment - Discount)")
    public void testCalculateBillSuccess() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        BigDecimal discount = new BigDecimal("500.00");
        BillingDTO bill = billingService.calculateBill(1, discount);

        assertNotNull(bill);
        assertEquals(1, bill.getAppointmentId());
        assertEquals("Amara Perera", bill.getPatientName());
        assertEquals("Dr. Nimal Fernando", bill.getDentistName());
        assertEquals("Teeth Whitening", bill.getTreatmentName());
        assertEquals(new BigDecimal("2500.00"), bill.getConsultationFee());
        assertEquals(new BigDecimal("500.00"), bill.getClinicCharge());
        assertEquals(new BigDecimal("5000.00"), bill.getTreatmentBaseCost());
        assertEquals(new BigDecimal("500.00"), bill.getDiscountAmount());
        // 2500 (consult) + 500 (clinic charge) + 5000 (treatment) - 500 (discount) = 7500.00
        assertEquals(new BigDecimal("7500.00"), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Unit Test: calculateBill Throws ResourceNotFoundException for Invalid Appointment ID")
    public void testCalculateBillNotFound() {
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            billingService.calculateBill(999, BigDecimal.ZERO);
        });
    }
}
