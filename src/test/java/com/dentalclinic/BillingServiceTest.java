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

    @Mock
    private com.dentalclinic.repository.TreatmentTypeRepository treatmentTypeRepository;

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
        // 2500 (consult) + 500 (clinic charge) + 5000 (treatment) = 8000.00 Gross Total
        assertEquals(new BigDecimal("8000.00"), bill.getTotalAmount());
        // 8000 (gross) - 0 (prev paid) - 500 (discount) = 7500.00 Net Balance Due
        assertEquals(new BigDecimal("7500.00"), bill.getNetBalanceDue());
    }

    @Test
    @DisplayName("Unit Test: calculateBill Throws ResourceNotFoundException for Invalid Appointment ID")
    public void testCalculateBillNotFound() {
        when(appointmentRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            billingService.calculateBill(999, BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Unit Test: calculateBill Throws IllegalArgumentException for Negative Discount")
    public void testCalculateBill_NegativeDiscount_ThrowsException() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        assertThrows(IllegalArgumentException.class, () -> {
            billingService.calculateBill(1, new BigDecimal("-100.00"));
        });
    }

    @Test
    @DisplayName("Unit Test: calculateBill Zero Discount Returns Full Total Amount")
    public void testCalculateBill_ZeroDiscount_ReturnsFullAmount() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        BillingDTO bill = billingService.calculateBill(1, BigDecimal.ZERO);

        assertNotNull(bill);
        assertEquals(0, new BigDecimal("0.00").compareTo(bill.getDiscountAmount()));
        // 2500 + 500 + 5000 - 0 = 8000.00
        assertEquals(0, new BigDecimal("8000.00").compareTo(bill.getTotalAmount()));
    }

    @Test
    @DisplayName("Unit Test: calculateBill Excessive Discount Capped at Zero (No Negative Bill)")
    public void testCalculateBill_ExcessiveDiscount_CappedAtZero() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        BigDecimal excessiveDiscount = new BigDecimal("20000.00");
        BillingDTO bill = billingService.calculateBill(1, excessiveDiscount);

        assertNotNull(bill);
        assertTrue(bill.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Unit Test: Verify Invoice Number is Generated Cleanly")
    public void testCalculateBill_InvoiceNumberGenerated() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        BillingDTO bill = billingService.calculateBill(1, BigDecimal.ZERO);

        assertNotNull(bill.getInvoiceNumber());
        assertTrue(bill.getInvoiceNumber() > 0);
    }

    @Test
    @DisplayName("Unit Test: calculateBill Stage 1 Initial Deposit Returns Consultation Fee + Facility Charge Only")
    public void testCalculateBill_InitialDepositStage_ReturnsConsultationAndClinicChargeOnly() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        BillingDTO bill = billingService.calculateBill(1, BigDecimal.ZERO, "INITIAL_DEPOSIT", null, "Credit/Debit Card");

        assertNotNull(bill);
        assertEquals("INITIAL_DEPOSIT", bill.getBillStage());
        assertEquals("Credit/Debit Card", bill.getPaymentMethod());
        assertEquals(new BigDecimal("2500.00"), bill.getConsultationFee());
        assertEquals(new BigDecimal("500.00"), bill.getClinicCharge());
        assertEquals(BigDecimal.ZERO, bill.getTreatmentBaseCost());
        // 2500 (consult) + 500 (clinic charge) = 3000.00
        assertEquals(new BigDecimal("3000.00"), bill.getTotalAmount());
    }

    @Test
    @DisplayName("Unit Test: calculateMultiProcedureBill Deducts Previous Paid Deposit and Marks Completed upon Settlement")
    public void testCalculateMultiProcedureBill_SettlementSuccess() {
        sampleAppointment.setPaidAmount(new BigDecimal("1500.00"));
        sampleAppointment.setPaymentStatus("PAID_DEPOSIT");
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(sampleAppointment));

        TreatmentType proc1 = new TreatmentType("Scaling & Polishing", "Deep cleaning", new BigDecimal("3500.00"));
        proc1.setTreatmentTypeId(10);
        TreatmentType proc2 = new TreatmentType("Fluoride Treatment", "Enamel protection", new BigDecimal("1500.00"));
        proc2.setTreatmentTypeId(11);

        when(treatmentTypeRepository.findById(10)).thenReturn(Optional.of(proc1));
        when(treatmentTypeRepository.findById(11)).thenReturn(Optional.of(proc2));

        BillingDTO bill = billingService.calculateMultiProcedureBill(
                1,
                java.util.List.of(10, 11),
                new BigDecimal("500.00"),
                "FINAL_SETTLED",
                "Credit/Debit Card",
                true
        );

        assertNotNull(bill);
        assertEquals(2, bill.getTreatmentItems().size());
        assertEquals(new BigDecimal("1500.00"), bill.getPreviousPaidAmount());
        // Gross: 2500 (consult) + 500 (facility) + 3500 (proc1) + 1500 (proc2) = 8000.00
        assertEquals(new BigDecimal("8000.00"), bill.getTotalAmount());
        // Net due: 8000 - 1500 (prev paid) - 500 (discount) = 6000.00
        assertEquals(new BigDecimal("6000.00"), bill.getNetBalanceDue());
        assertEquals("FULL_PAID", sampleAppointment.getPaymentStatus());
        assertEquals("COMPLETED", sampleAppointment.getStatus());
    }
}
