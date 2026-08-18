package com.dentalclinic.service;

import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service handling treatment cost calculation, discount application, and
 * receipt generation.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class BillingService {

    private final AppointmentRepository appointmentRepository;
    private final EmailNotificationService emailNotificationService;
    private final ClinicSettingService clinicSettingService;

    public static final BigDecimal DEFAULT_CLINIC_CHARGE = new BigDecimal("500.00");

    @Autowired
    public BillingService(AppointmentRepository appointmentRepository,
                          @Autowired(required = false) EmailNotificationService emailNotificationService,
                          @Autowired(required = false) ClinicSettingService clinicSettingService) {
        this.appointmentRepository = appointmentRepository;
        this.emailNotificationService = emailNotificationService;
        this.clinicSettingService = clinicSettingService;
    }

    /**
     * Calculates itemized bill for an appointment.
     * Total = Consultation Fee + Clinic Charge + Treatment Cost - Discount
     */
    public BillingDTO calculateBill(Integer appointmentId, BigDecimal discountAmount) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        BigDecimal consultationFee = appointment.getDentist() != null
                && appointment.getDentist().getConsultationFee() != null
                        ? appointment.getDentist().getConsultationFee()
                        : BigDecimal.ZERO;

        BigDecimal treatmentCost = appointment.getTreatmentType() != null
                && appointment.getTreatmentType().getBaseCost() != null
                        ? appointment.getTreatmentType().getBaseCost()
                        : BigDecimal.ZERO;

        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative.");
        }

        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal clinicCharge = (clinicSettingService != null) ? clinicSettingService.getClinicCharge() : DEFAULT_CLINIC_CHARGE;

        BigDecimal total = consultationFee.add(clinicCharge).add(treatmentCost).subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        String treatmentName = appointment.getTreatmentType() != null
                ? appointment.getTreatmentType().getTreatmentName()
                : "General Dental Consultation";

        BillingDTO bill = new BillingDTO(
                appointmentId + 5000, // Generated Invoice Number
                appointment.getAppointmentId(),
                appointment.getTokenNumber(),
                appointment.getPatient().getPatientName(),
                appointment.getPatient().getContactNumber(),
                appointment.getDentist().getDentistName(),
                appointment.getDentist().getSpecialization(),
                treatmentName,
                appointment.getAppointmentDate(),
                consultationFee,
                clinicCharge,
                treatmentCost,
                discount,
                total);

        if (emailNotificationService != null) {
            try {
                emailNotificationService.sendBillingReceiptEmail(bill, appointment.getPatient().getEmail());
            } catch (Exception ex) {
                // Non-blocking log
            }
        }

        return bill;
    }
}
