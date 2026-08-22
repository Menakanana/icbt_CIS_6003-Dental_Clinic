package com.dentalclinic.controller.api;

import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST Controller for Patient Billing & Invoice Web Services.
 * 
 * Layer: Presentation / API Controller Layer
 * Base Path: /api/billing
 */
@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingApiController {

    private final BillingService billingService;

    public BillingApiController(BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * GET /api/billing/calculate/{appointmentId}?discount=500.00
     */
    @GetMapping("/calculate/{appointmentId}")
    public ResponseEntity<BillingDTO> calculateBill(
            @PathVariable("appointmentId") Integer appointmentId,
            @RequestParam(name = "discount", required = false, defaultValue = "0") BigDecimal discount) {

        BillingDTO bill = billingService.calculateBill(appointmentId, discount);
        return ResponseEntity.ok(bill);
    }
}
