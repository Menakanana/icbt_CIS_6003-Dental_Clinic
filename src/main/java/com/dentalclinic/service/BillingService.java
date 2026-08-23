package com.dentalclinic.service;

import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling treatment cost calculation, multi-procedure billing, discount application, and
 * 2-stage receipt generation.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class BillingService {

    private final AppointmentRepository appointmentRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final EmailNotificationService emailNotificationService;
    private final ClinicSettingService clinicSettingService;

    public static final BigDecimal DEFAULT_CLINIC_CHARGE = new BigDecimal("500.00");

    @Autowired
    public BillingService(AppointmentRepository appointmentRepository,
                          TreatmentTypeRepository treatmentTypeRepository,
                          @Autowired(required = false) EmailNotificationService emailNotificationService,
                          @Autowired(required = false) ClinicSettingService clinicSettingService) {
        this.appointmentRepository = appointmentRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.emailNotificationService = emailNotificationService;
        this.clinicSettingService = clinicSettingService;
    }

    /**
     * Calculates itemized bill for an appointment (Backward Compatible).
     */
    public BillingDTO calculateBill(Integer appointmentId, BigDecimal discountAmount) {
        return calculateBill(appointmentId, discountAmount, "FINAL_SETTLED", null, "Cash");
    }

    /**
     * Calculates itemized bill with 2-stage support (INITIAL_DEPOSIT vs FINAL_SETTLED), treatment selection, and payment method.
     */
    public BillingDTO calculateBill(Integer appointmentId, BigDecimal discountAmount, String billStage, Integer treatmentTypeId, String paymentMethod) {
        List<Integer> treatmentIds = (treatmentTypeId != null) ? List.of(treatmentTypeId) : List.of();
        return calculateMultiProcedureBill(appointmentId, treatmentIds, discountAmount, billStage, paymentMethod, false);
    }

    /**
     * Calculates or settles a multi-procedure bill for an appointment.
     */
    @Transactional
    public BillingDTO calculateMultiProcedureBill(Integer appointmentId, List<Integer> treatmentTypeIds, BigDecimal discountAmount, String billStage, String paymentMethod, boolean markAsPaid) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        BigDecimal consultationFee = appointment.getDentist() != null
                && appointment.getDentist().getConsultationFee() != null
                        ? appointment.getDentist().getConsultationFee()
                        : BigDecimal.ZERO;

        BigDecimal clinicCharge = (clinicSettingService != null) ? clinicSettingService.getClinicCharge() : DEFAULT_CLINIC_CHARGE;

        boolean isInitial = "INITIAL_DEPOSIT".equalsIgnoreCase(billStage);

        BigDecimal previousPaid = appointment.getPaidAmount() != null ? appointment.getPaidAmount() : BigDecimal.ZERO;

        BigDecimal treatmentCostSum = BigDecimal.ZERO;
        List<BillingDTO.TreatmentItemDTO> treatmentItems = new ArrayList<>();
        String mainTreatmentName = "General Dental Consultation & Procedure";

        if (!isInitial && treatmentTypeIds != null && !treatmentTypeIds.isEmpty()) {
            for (Integer tId : treatmentTypeIds) {
                if (tId != null) {
                    treatmentTypeRepository.findById(tId).ifPresent(tt -> {
                        BigDecimal cost = tt.getBaseCost() != null ? tt.getBaseCost() : BigDecimal.ZERO;
                        treatmentItems.add(new BillingDTO.TreatmentItemDTO(tt.getTreatmentName(), cost));
                    });
                }
            }
        }

        // Fallback to appointment's saved treatment type if no list passed
        if (!isInitial && treatmentItems.isEmpty() && appointment.getTreatmentType() != null) {
            BigDecimal cost = appointment.getTreatmentType().getBaseCost() != null ? appointment.getTreatmentType().getBaseCost() : BigDecimal.ZERO;
            treatmentItems.add(new BillingDTO.TreatmentItemDTO(appointment.getTreatmentType().getTreatmentName(), cost));
        }

        for (BillingDTO.TreatmentItemDTO item : treatmentItems) {
            treatmentCostSum = treatmentCostSum.add(item.getCost());
        }
        if (!treatmentItems.isEmpty()) {
            mainTreatmentName = treatmentItems.get(0).getName() + (treatmentItems.size() > 1 ? " (+" + (treatmentItems.size() - 1) + " more)" : "");
        }

        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative.");
        }

        BigDecimal discount = (!isInitial && discountAmount != null) ? discountAmount : BigDecimal.ZERO;

        BigDecimal grossTotal = isInitial ? consultationFee.add(clinicCharge) : consultationFee.add(clinicCharge).add(treatmentCostSum);
        BigDecimal netDue = isInitial ? grossTotal : grossTotal.subtract(previousPaid).subtract(discount);
        if (netDue.compareTo(BigDecimal.ZERO) < 0) {
            netDue = BigDecimal.ZERO;
        }

        BillingDTO bill = new BillingDTO(
                appointmentId + 5000,
                appointment.getAppointmentId(),
                appointment.getTokenNumber(),
                appointment.getPatient().getPatientName(),
                appointment.getPatient().getContactNumber(),
                appointment.getDentist().getDentistName(),
                appointment.getDentist().getSpecialization(),
                mainTreatmentName,
                appointment.getAppointmentDate(),
                consultationFee,
                clinicCharge,
                treatmentCostSum,
                discount,
                grossTotal);

        bill.setBillStage(isInitial ? "INITIAL_DEPOSIT" : "FINAL_SETTLED");
        bill.setPaymentMethod(paymentMethod != null && !paymentMethod.trim().isEmpty() ? paymentMethod : "Cash");
        bill.setMedicalHistory(appointment.getPatient() != null ? appointment.getPatient().getMedicalHistory() : null);
        bill.setPreviousPaidAmount(previousPaid);
        bill.setNetBalanceDue(netDue);
        bill.setTreatmentItems(treatmentItems);

        if (markAsPaid && !isInitial) {
            appointment.setPaidAmount(previousPaid.add(netDue));
            appointment.setPaymentStatus("FULL_PAID");
            appointment.setStatus("COMPLETED");
            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                appointment.setPaymentMethod(paymentMethod.trim());
            }
            appointmentRepository.save(appointment);
        }

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
