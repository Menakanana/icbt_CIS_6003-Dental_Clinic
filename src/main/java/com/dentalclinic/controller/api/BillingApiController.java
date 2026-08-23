package com.dentalclinic.controller.api;

import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.service.BillingService;
import com.dentalclinic.service.EmailNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Patient Billing & Invoice Web Services.
 * 
 * Layer: Presentation / API Controller Layer
 * Base Path: /api/billing
 */
@RestController
@RequestMapping({"/api/billing", "/api/bills"})
@CrossOrigin(origins = "*")
public class BillingApiController {

    private final BillingService billingService;
    private final EmailNotificationService emailNotificationService;

    public BillingApiController(BillingService billingService, EmailNotificationService emailNotificationService) {
        this.billingService = billingService;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * GET /api/billing/calculate/{appointmentId}?discount=500.00&stage=FINAL_SETTLED&paymentMethod=Cash
     */
    @GetMapping("/calculate/{appointmentId}")
    public ResponseEntity<BillingDTO> calculateBill(
            @PathVariable("appointmentId") Integer appointmentId,
            @RequestParam(name = "discount", required = false, defaultValue = "0") BigDecimal discount,
            @RequestParam(name = "stage", required = false, defaultValue = "FINAL_SETTLED") String stage,
            @RequestParam(name = "treatmentTypeId", required = false) Integer treatmentTypeId,
            @RequestParam(name = "paymentMethod", required = false, defaultValue = "Cash") String paymentMethod) {

        BillingDTO bill = billingService.calculateBill(appointmentId, discount, stage, treatmentTypeId, paymentMethod);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<BillingDTO> getAppointmentBillingDetails(@PathVariable("appointmentId") Integer appointmentId) {
        BillingDTO bill = billingService.calculateMultiProcedureBill(appointmentId, java.util.List.of(), BigDecimal.ZERO, "FINAL_SETTLED", "Cash", false);
        return ResponseEntity.ok(bill);
    }

    public static class SettleRequest {
        public Integer appointmentId;
        public java.util.List<Integer> treatmentTypeIds;
        public BigDecimal discount;
        public String paymentMethod;
    }

    @PostMapping("/settle")
    public ResponseEntity<BillingDTO> settleBill(@RequestBody SettleRequest req) {
        if (req == null || req.appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID is required for bill settlement.");
        }
        BillingDTO bill = billingService.calculateMultiProcedureBill(
                req.appointmentId,
                req.treatmentTypeIds != null ? req.treatmentTypeIds : java.util.List.of(),
                req.discount != null ? req.discount : BigDecimal.ZERO,
                "FINAL_SETTLED",
                req.paymentMethod != null ? req.paymentMethod : "Cash",
                true);
        return ResponseEntity.ok(bill);
    }

    /**
     * POST /api/billing/send-email/{appointmentId}
     * Dispatches payment receipt email to patient.
     */
    @PostMapping("/send-email/{appointmentId}")
    public ResponseEntity<Map<String, Object>> sendReceiptEmail(
            @PathVariable("appointmentId") Integer appointmentId,
            @RequestParam(name = "email", required = false) String customEmail) {

        Map<String, Object> response = new HashMap<>();
        try {
            BillingDTO bill = billingService.calculateBill(appointmentId, BigDecimal.ZERO);
            String recipient = (customEmail != null && !customEmail.isBlank()) ? customEmail : null;
            boolean sent = emailNotificationService.sendBillingReceiptEmail(bill, recipient);

            response.put("success", sent);
            response.put("message", sent ? "Payment receipt email dispatched successfully." : "Receipt generated in log mode.");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

