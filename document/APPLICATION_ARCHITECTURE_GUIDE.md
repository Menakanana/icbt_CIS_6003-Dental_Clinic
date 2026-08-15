# Sunrise Dental Clinic - Master Application & Architecture Guide

## Overview
This document specifies the master technical architecture for the **Sunrise Dental Clinic Management System**. Built on **Spring Boot, Spring Data JPA / Hibernate (ORM)**, the application features a **Hybrid Architecture** supporting both **Server-Side Rendered JSP Views** (for traditional browser UI rendering) and **Decoupled RESTful Web APIs** (for JSON integration & automated testing).

---

## 1. 3-Tier Architecture (JSP Views & REST Web API Dual-Layer)

```
┌─────────────────────────────────────────────────────────┐
│               PRESENTATION LAYER (VIEW / UI)            │
│  - JSP Views (/WEB-INF/views/*.jsp)                    │
│    • login.jsp (Staff Sign In Interface)                │
│    • dashboard.jsp (Staff Operations Portal)            │
│    • booking.jsp (Appointment Slot Booking View)        │
│    • billing.jsp (Invoice Generation & Payment View)    │
│  - Web Browser Client (HTTP / Form Submissions / JSON)  │
└───────────────────────────┬─────────────────────────────┘
                            │ HTTP Requests / Form Data / JSON Payloads
┌───────────────────────────▼─────────────────────────────┐
│           SECURITY & CONTROLLER LAYER                   │
│  - SecurityConfig       (BCrypt $2a$12$ Password Encoder)│
│  - WebViewController    (Routes GET/POST to JSP Views)  │
│  - AuthApiController       POST /api/auth/login (JSON)  │
│  - PatientApiController    POST /api/patients (@Valid)  │
│  - SlotApiController       GET  /api/slots/available    │
│  - AppointmentApiController POST /api/appointments/book │
│  - BillingApiController    POST /api/bills/generate     │
│  - GlobalExceptionHandler (@RestControllerAdvice)        │
└───────────────────────────┬─────────────────────────────┘
                            │ Calls Service Interfaces
┌───────────────────────────▼─────────────────────────────┐
│                 SERVICE / BUSINESS LAYER                │
│  - AuthService (BCrypt Password Verification)           │
│  - SlotService (Dynamic Per-Dentist Slot Generation)    │
│  - AppointmentValidationService (Overlap Detection)    │
│  - BillingService (Financial Calculations & Snapshots) │
│  - ReportService (Schedule, Revenue, & History Reports)│
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
│            (H2 In-Memory / MS SQL Server DB)            │
└─────────────────────────────────────────────────────────┘
```

---

## 2. Design Justification & Technical Trade-offs

To satisfy academic rubric requirements for **Critical Reflection & Design Justification** (Task A & Task B), the table below highlights why alternative design choices were evaluated and superseded:

| Architecture Considered | Technical Limitations / Trade-offs | Justification for Selected Hybrid Architecture |
|---|---|---|
| **Pure REST API Only** | Requires modern JavaScript framework (React/Angular) runtime on client; syllabus requires server-side views. | **Hybrid JSP + REST Architecture:** Supports traditional JSP form submissions for course requirements while providing JSON REST APIs for automated testing. |
| **Free-Form Time Booking** *(Unstructured appointment times)* | High risk of double-booking, irregular time gaps, and complex runtime validation. | **Per-Dentist Dynamic Slot System:** Pre-defined shift slots ($5, 15, 30$ min) guarantee zero double-booking and optimal schedule capacity. |
| **Database-Centric Stored Procedures** *(Heavy T-SQL procedural logic)* | T-SQL vendor lock-in, hard to version-control in Git, difficult to unit test without database rollbacks. | **Code-First Service Layer:** Procedural logic moves to Java services (`SlotService`, `AppointmentValidationService`), allowing fast in-memory unit testing. |

---

## 3. Design Patterns Implemented (Task B Requirement)

| Design Pattern | Pattern Category | Java Class / Interface | Application Role & Purpose |
|---|---|---|---|
| **Repository Pattern** | Enterprise | `AppointmentRepository`, `PatientRepository` | Hides JPA/SQL queries behind entity interfaces. |
| **Service Layer Pattern** | Enterprise | `SlotService`, `AppointmentValidationService` | Encapsulates complex business rules & validations. |
| **DTO Pattern** | Structural | `BookingRequestDTO`, `PatientDTO`, `BillResponseDTO` | Decouples HTTP JSON payloads from DB entities. |
| **Controller Pattern** | Architectural | `WebViewController`, `AuthApiController` | Handles HTTP routing & JSP view rendering. |
| **Singleton Pattern** | Creational | `@Service`, `@Repository`, `@Bean` Spring Beans | Ensures a single shared memory instance for services. |
| **Factory Pattern** | Creational | `SlotService`, `BillingService` | Encapsulates dynamic slot & bill snapshot creation. |

---

## 4. Hybrid Time-Slot + Token Ticket Engine Architecture

To optimize front-desk reception speed and eliminate double-booking in dental procedures, the system implements a **Hybrid Time-Slot + Token Ticket Engine** (`SlotService` and `AppointmentValidationService`).

### A. Appointment Ticket Structure
Every booking generates a combined **Appointment Ticket**:
- 🕒 **Estimated Arrival Time Window** (e.g. `04:30 PM - 05:00 PM`): Informs the patient when to arrive to prevent waiting room overcrowding.
- 🎟️ **Queue Token Number** (e.g. `Token #3`): Establishes sequential order for the dentist within their shift window.

### B. Mathematical Overlap Prevention Condition
Before confirming any booking, `AppointmentValidationService` evaluates the generated slot range $(\text{slotStart}, \text{slotEnd})$ against existing non-cancelled database appointments $(\text{appStart}, \text{appEnd})$:

$$\text{Overlap} = (\text{slotStart} < \text{appEnd}) \quad \land \quad (\text{slotEnd} > \text{appStart})$$

If $\text{Overlap} = \text{true}$, the slot is flagged `isAvailable = false` and rendered as a greyed-out disabled button in the JSP view.

---

## 5. Dual Patient Management Workflow Specification

The presentation layer supports two complementary patient workflows to accommodate fast phone/walk-in bookings as well as long-term record maintenance:

1. **Inline Quick-Add (1-Step Flow during Booking):**
   - Allows receptionists to register new patients directly inside the booking interface with minimal fields (Name, Sri Lankan NIC, Contact Number).
   - Automatically saves the patient to SQL Server and assigns the `PatientID` to the new appointment in a single atomic transaction.
2. **Dedicated Patient Directory & Profile Manager (2-Step Flow):**
   - Provides a searchable directory of all registered patients.
   - Allows updating patient profiles later (Address, Email, Date of Birth, Gender, Emergency Contact).
   - Displays historical dental visits, past invoices, and upcoming booked slots.

---

## 6. Active Reference Specifications

- 📂 [`document/UML_Diagrams/`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/UML_Diagrams) – Complete UML Diagram Suite (Use Case, Class, Sequence, ER, Activity Diagrams).
- 📄 [`document/DATABASE_DESIGN_DOCUMENT.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/DATABASE_DESIGN_DOCUMENT.md) – Relational Data Dictionary & Schema Specification.
- 📄 [`document/PROJECT_DEVELOPMENT_TIMELINE.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/PROJECT_DEVELOPMENT_TIMELINE.md) – Feature-Driven Development Plan & Sprint Roadmap.
