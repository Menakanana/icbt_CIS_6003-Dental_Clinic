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

## 4. Active Reference Specifications

- 📂 [`document/UML_Diagrams/`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/UML_Diagrams) – Complete Mermaid.js UML Diagram Suite (Use Case, Class, Sequence, ER, Activity Diagrams).
- 📄 [`document/DATABASE_DESIGN_DOCUMENT.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/DATABASE_DESIGN_DOCUMENT.md) – Relational Data Dictionary & Schema Specification.
- 📄 [`document/PROJECT_DEVELOPMENT_TIMELINE.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/PROJECT_DEVELOPMENT_TIMELINE.md) – Feature-Driven Development Plan & Sprint Roadmap.
- 📄 [`document/Assignment_CIS6003_Advanced_Programming.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/Assignment_CIS6003_Advanced_Programming.md) – Official assignment brief.
