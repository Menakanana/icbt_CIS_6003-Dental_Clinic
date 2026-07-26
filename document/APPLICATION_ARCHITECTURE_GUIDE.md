# Sunrise Dental Clinic - Master Application & Architecture Guide

## Overview
This document provides the master technical specification for the **Sunrise Dental Clinic Management System**. Built on **Spring Boot, Spring Data JPA / Hibernate (ORM)**, all core business logic, dynamic slot generation algorithms, patient overlap validations, financial bill calculations, DTO input validations, RBAC security, Global Exception Handling, Report Generation, Swagger UI OpenAPI, and CORS configurations reside in clean, testable Java Application Services exposed via **RESTful Web API endpoints (JSON)**.

---

## 1. 3-Tier Layered REST Web API Architecture

```
┌─────────────────────────────────────────────────────────┐
│               WEB APPLICATION FRONTEND                  │
│       (HTML5 / CSS3 / Vanilla JavaScript UI)            │
└───────────────────────────┬─────────────────────────────┘
                            │ HTTP Requests / JSON Payloads + Bearer Token
┌───────────────────────────▼─────────────────────────────┐
│           SECURITY & REST API CONTROLLER LAYER          │
│  - SecurityConfig       (RBAC: Admin / Receptionist)    │
│  - WebCorsConfig        (Cross-Origin Resource Sharing) │
│  - OpenApiConfig        (Swagger UI API Documentation)  │
│  - GlobalExceptionHandler (@RestControllerAdvice)        │
│  - AuthController       POST /api/auth/login            │
│  - PatientController    POST /api/patients (@Valid)     │
│  - SlotController       GET  /api/slots/available       │
│  - AppointmentController POST /api/appointments/book    │
│  - BillingController    POST /api/bills/generate        │
│  - ReportController     GET  /api/reports/*             │
└───────────────────────────┬─────────────────────────────┘
                            │ Calls Service Interfaces
┌───────────────────────────▼─────────────────────────────┐
│                 SERVICE / BUSINESS LAYER                │
│  - AuthService (BCrypt Password Hashing & JWT Token)    │
│  - SlotService (Dynamic Per-Dentist Slot Generation)     │
│  - AppointmentValidationService (Overlap Detection)     │
│  - BillingService (Financial Calculations & Snapshots)  │
│  - ReportService (Schedule, Revenue, & History Reports) │
└───────────────────────────┬─────────────────────────────┘
                            │ Consumes Spring Data JPA Repositories
┌───────────────────────────▼─────────────────────────────┐
│          SPRING DATA JPA / HIBERNATE (ORM LAYER)        │
│  - UserRepository, PatientRepository, DentistRepository │
│  - ScheduleRepository, SlotRepository                   │
│  - AppointmentRepository, BillRepository                │
└───────────────────────────┬─────────────────────────────┘
                            │ Hibernate ORM / Dialect
┌───────────────────────────▼─────────────────────────────┐
│               RELATIONAL DATABASE STORAGE               │
│            (SQL Server / H2 / MySQL DB)                 │
└─────────────────────────────────────────────────────────┘
```

---

## 2. Design Justification & Technical Trade-offs

To satisfy academic rubric requirements for **Critical Reflection & Design Justification** (Task A & Task B), the table below highlights why alternative design choices were evaluated and superseded during the design phase:

| Architecture Considered | Technical Limitations / Trade-offs | Justification for Selected 3-Tier REST API Architecture |
|---|---|---|
| **Free-Form Time Booking** *(Unstructured appointment times)* | High risk of double-booking, irregular time gaps, and complex runtime validation. | **Per-Dentist Dynamic Slot System:** Pre-defined shift slots ($5, 15, 30$ min) guarantee zero double-booking and optimal schedule capacity. |
| **Database-Centric Stored Procedures** *(Heavy T-SQL procedural logic)* | T-SQL vendor lock-in, hard to version-control in Git, difficult to unit test without database rollbacks or specialized T-SQL test frameworks. | **Code-First Service Layer:** Procedural logic moves to Java services (`SlotService`, `AppointmentValidationService`), allowing fast in-memory unit testing and clean versioning. |
| **Monolithic Desktop Client** *(Swing / JavaFX GUI coupled with DB)* | Lacks distributed architecture, difficult to access over web protocols, hard to integrate with web API clients. | **Decoupled REST Web API:** Java backend exposes JSON web services accessible by web browsers, mobile web, or third-party clinic tools. |

---

## 3. Design Patterns Implemented (Task B Requirement)

| Design Pattern | Pattern Category | Java Class / Interface | Application Role & Purpose |
|---|---|---|---|
| **Repository Pattern** | Enterprise | `AppointmentRepository`, `PatientRepository` | Hides JPA/SQL queries behind entity interfaces. |
| **Service Layer Pattern** | Enterprise | `SlotService`, `AppointmentValidationService` | Encapsulates complex business rules & validations. |
| **DTO Pattern** | Structural | `BookingRequestDTO`, `PatientDTO`, `BillResponseDTO` | Decouples HTTP JSON payloads from DB entities. |
| **Controller Pattern** | Architectural | `AppointmentApiController`, `AuthApiController` | Handles HTTP routing & request payload validations. |
| **Singleton Pattern** | Creational | `@Service`, `@Repository`, `@Bean` Spring Beans | Ensures a single shared memory instance for services. |
| **Factory Pattern** | Creational | `SlotService`, `BillingService` | Encapsulates dynamic slot & bill snapshot creation. |

---

## 4. Global Exception Handling & Error Standard

All uncaught exceptions and validation failures are intercepted by `@RestControllerAdvice` to produce consistent JSON error payloads with standard HTTP status codes (`400 Bad Request`, `404 Not Found`, `409 Conflict`, `500 Internal Error`).

### 4.1 Global Exception Handler (`GlobalExceptionHandler.java`)
```java
package com.dentalclinic.exception;

import com.dentalclinic.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed for input request", errors));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleBookingConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
}
```

---

## 5. Report Generation Engine (Task B Mandate)

The **`ReportService`** provides three specialized analytics reports:

1. **Daily Dentist Schedule Report (`GET /api/reports/daily-schedule?date=...`)**: Full patient roster per dentist for a date.
2. **Financial Revenue & Billing Report (`GET /api/reports/revenue?startDate=...&endDate=...`)**: Revenue breakdown comparing Dentist Consultation Fees vs. Treatment Base Costs.
3. **Patient Dental History Report (`GET /api/reports/patient-history/{patientId}`)**: Historical record of all past appointments, treatments, and bill payments for a patient.

### 5.1 Report Service Implementation (`ReportService.java`)
```java
package com.dentalclinic.service;

import com.dentalclinic.dto.FinancialRevenueReportDTO;
import com.dentalclinic.entity.Bill;
import com.dentalclinic.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final BillRepository billRepository;

    public ReportService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public FinancialRevenueReportDTO generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Bill> bills = billRepository.findByBillDateBetweenAndPaymentStatus(start, end, "Paid");

        BigDecimal totalConsultationFee = bills.stream()
                .map(Bill::getConsultationFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTreatmentCost = bills.stream()
                .map(Bill::getTreatmentCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = bills.stream()
                .map(Bill::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        FinancialRevenueReportDTO report = new FinancialRevenueReportDTO();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTotalPaidBills(bills.size());
        report.setTotalConsultationRevenue(totalConsultationFee);
        report.setTotalTreatmentRevenue(totalTreatmentCost);
        report.setTotalGrossRevenue(totalRevenue);

        return report;
    }
}
```

---

## 6. DTO Input Validation Standard (`jakarta.validation`)

All Request DTOs pass through automatic Bean Validation (`@Valid`) before reaching service logic.

### 6.1 Booking Request DTO (`BookingRequestDTO.java`)
```java
package com.dentalclinic.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    @NotNull(message = "Patient ID is required.")
    @Positive(message = "Invalid Patient ID.")
    private Integer patientId;

    @NotNull(message = "Dentist ID is required.")
    @Positive(message = "Invalid Dentist ID.")
    private Integer dentistId;

    @NotNull(message = "Treatment Type ID is required.")
    @Positive(message = "Invalid Treatment Type ID.")
    private Integer treatmentTypeId;

    @NotNull(message = "Schedule ID is required.")
    @Positive(message = "Invalid Schedule ID.")
    private Integer scheduleId;

    @NotNull(message = "Slot ID is required.")
    @Positive(message = "Invalid Slot ID.")
    private Integer slotId;

    @NotNull(message = "Appointment Date is required.")
    @FutureOrPresent(message = "Appointment date cannot be in the past.")
    private LocalDate appointmentDate;

    @Size(max = 255, message = "Notes cannot exceed 255 characters.")
    private String notes;
}
```

---

## 7. Role-Based Access Control (RBAC) & Security

### 7.1 Security Configuration (`SecurityConfig.java`)
```java
package com.dentalclinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/patients/**", "/api/slots/**", "/api/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                .requestMatchers(HttpMethod.POST, "/api/patients", "/api/appointments/book", "/api/bills/generate").hasAnyRole("ADMIN", "RECEPTIONIST")
                .requestMatchers("/api/schedules/**", "/api/reports/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```

---

## 8. Interactive API Documentation (Swagger / OpenAPI UI)

- **Swagger UI URL:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Docs:** `http://localhost:8080/v3/api-docs`

---

## 9. CORS (Cross-Origin Resource Sharing) Configuration

### 9.1 Web CORS Configuration (`WebCorsConfig.java`)
```java
package com.dentalclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## 10. Spring Data JPA Repositories (Pure Entity Queries - NO Raw SQL)

### 10.1 Appointment Repository (`AppointmentRepository.java`)
```java
package com.dentalclinic.repository;

import com.dentalclinic.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // Derived Entity Query: Find patient appointments by patient entity ID, date, and statuses (No raw SQL!)
    List<Appointment> findByPatientPatientIdAndAppointmentDateAndStatusIn(
        Integer patientId, LocalDate appointmentDate, List<String> statuses
    );

    // Derived Entity Query: Find dentist appointments by dentist entity ID and date (No raw SQL!)
    List<Appointment> findByDentistDentistIdAndAppointmentDate(
        Integer dentistId, LocalDate appointmentDate
    );
}
```

---

## 11. REST Web API Endpoints Specification

| Method | Endpoint | Description | Request Body / Query | Response |
|---|---|---|---|---|
| **POST** | `/api/auth/login` | Authenticate staff user | `LoginRequest` | `AuthResponse` |
| **GET** | `/api/patients` | Search / list patients | Query: `search` | `List<PatientDTO>` |
| **POST** | `/api/patients` | Register new patient | `PatientDTO` (`@Valid`) | `PatientDTO` |
| **GET** | `/api/slots/available` | Get available dentist slots | Query: `dentistId`, `date` | `List<SlotDTO>` |
| **POST** | `/api/appointments/book` | Book new appointment | `BookingRequestDTO` (`@Valid`) | `BookingResponseDTO` |
| **POST** | `/api/bills/generate` | Calculate & generate bill | `BillRequestDTO` (`@Valid`) | `BillResponseDTO` |
| **GET** | `/api/reports/revenue` | Financial revenue report | Query: `startDate`, `endDate` | `FinancialRevenueReportDTO` |
| **GET** | `/api/reports/daily-schedule` | Daily schedule report | Query: `date` | `DailyScheduleReportDTO` |

---

## 12. Active Reference Specifications

- 📄 [`document/DATABASE_DESIGN_DOCUMENT.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/DATABASE_DESIGN_DOCUMENT.md) – Relational Data Dictionary & Schema Specification.
- 📄 [`document/Assignment_CIS6003_Advanced_Programming.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/Assignment_CIS6003_Advanced_Programming.md) – Official assignment brief.
