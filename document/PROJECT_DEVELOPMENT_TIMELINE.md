# Sunrise Dental Clinic - Feature-Driven Development Plan

## Project Details
- **Module:** CIS6003 - Advanced Programming (Cardiff Metropolitan University / ICBT)
- **Project Name:** Sunrise Dental Clinic Management System
- **Assignment Issue Date:** July 04, 2026
- **Project Start Date:** July 08, 2026
- **Development Completion Date:** August 24, 2026
- **Testing & Final Report Completion:** August 31, 2026
- **Submission Deadline:** September 05, 2026

---

## 1. Feature-Driven Sprint Breakdown

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                               FEATURE-DRIVEN ROADMAP                                    │
├───────────────────┬───────────────────┬───────────────────────┬─────────────────────────┤
│ Sprint 1 (W1-W2)  │ Sprint 2 (W3-W4)  │ Sprint 3 (W5-W6)      │ Sprint 4 (W7-W9)        │
│ Setup, DB Design  │ Task A UML Specs  │ Dentist Slot Engine   │ Billing, Reports, UI    │
│ & Architecture    │ & Auth/Patients   │ & Booking Prevention  │ & Final Testing         │
│ (Jul 08 - Jul 18) │ (Jul 19 - Aug 01) │ (Aug 02 - Aug 15)     │ (Aug 16 - Aug 31)       │
└───────────────────┴───────────────────┴───────────────────────┴─────────────────────────┘
```

---

## 2. Feature Sprint Schedule & Task Mapping

### Sprint 1: Project Setup, Requirements & Database Design (July 08 – July 18, 2026)
- **July 08, 2026:** Repository initialization, directory structure, and Git setup. *(Task D)*
- **July 13, 2026:** Requirements analysis and evaluation of appointment scheduling models. *(Task A & B)*
- **July 17, 2026:** Database schema design (`SunriseDentalClinicDB`) and DDL script (`DentalClinic_DB_Script.sql`). *(Task A & B)*

---

### Sprint 2: Task A UML Diagrams & Feature 1 (Auth & Patients) (July 19 – August 01, 2026)
*Task A Focus: UML Modeling (Use Case, Class & Sequence Diagrams, Design Rationale & Assumptions)*

- **July 21, 2026 (Task A Use Case & Class Diagrams):** 
  - **Task A Use Case Diagram:** Define actors (`Admin`, `Receptionist`) and core use cases (Patient Registration, Slot Booking, Bill Generation, Report Viewing).
  - **Task A Class Diagram:** Derive domain model classes, entity attributes, multi-tier relations, and JPA mappings.
- **July 26, 2026 (Task A Sequence Diagrams & Assumptions):** 
  - **Task A Sequence Diagrams:** Derive step-by-step sequence workflows for User Authentication, Slot Booking, and Billing logic.
  - **Task A Assumptions:** Document explicit design assumptions (per-dentist session slots, immutable bill snapshots, BCrypt hashing factor 12).
  - Staff User Entity, BCrypt Password Hashing (`PasswordService`), and Auth REST API (`POST /api/auth/login`).
- **August 01, 2026 (Feature 1 Complete):** 
  - Patient Entity, DTO Validation (`@Valid`), Patient REST API (`/api/patients`), Web UI screen, and Unit Tests (`PatientServiceTest`).

---

### Sprint 3: Feature 2 - Dentist Schedules, Slot Generation & Appointment Booking (August 02 – August 15, 2026)
*Complete Vertical Slice: Entity + Repository + Service + REST API + Overlap Prevention + Unit Tests*

- **August 03, 2026:** Dentist & Schedule Entities, dynamic slot generation engine (`SlotService`), and Slot REST API (`GET /api/slots/available`).
- **August 08, 2026:** Appointment Entity and 3-point time overlap validation (`AppointmentValidationService`) to prevent double-booking.
- **August 15, 2026:** Interactive slot booking UI screen, booking REST API (`POST /api/appointments/book`), and Unit Tests (`SlotServiceTest`, `AppointmentServiceTest`).

---

### Sprint 4: Feature 3 & 4 - Billing System, Reporting Engine & Final Testing (August 16 – August 31, 2026)
*Complete Vertical Slice: Entity + Billing Formula + Report Engine + UI + End-to-End Tests*

- **August 18, 2026:** Bill Entity, financial calculation service ($\text{Consultation} + \text{Base Cost} - \text{Discount}$), and Billing REST API (`POST /api/bills/generate`).
- **August 22, 2026:** Report generation service (`ReportService`) for Daily Schedules & Financial Revenue, plus Global Error Handler (`@RestControllerAdvice`).
- **August 26, 2026:** Billing and Reports UI dashboard, Swagger UI configuration (`/swagger-ui.html`), and CORS settings.
- **August 29, 2026:** Full automated test suite execution (JUnit 5 + Mockito + `MockMvc` REST Controller tests). *(Task C)*
- **August 31, 2026:** Final assignment report compilation, UML diagram formatting, and release tag `v1.0.0`. *(Task A–D)*

---

## 3. Assignment Task Alignment Matrix

| Assignment Task | Weighting | Covered Features & Deliverables | Target Sprint | Schedule |
|---|---|---|---|---|
| **Task A: System Design & UML** | 20 Marks (LO I) | Use Case Diagram, Class Diagram, Sequence Diagrams, Design Decisions & Assumptions | Sprint 1 & 2 | Jul 19 – Aug 01 |
| **Task B: Web Application** | 40 Marks (LO II) | Java REST Web API, Spring Data JPA ORM, 6 Design Patterns, Reports Engine, Web UI | Sprint 2, 3 & 4 | Aug 02 – Aug 26 |
| **Task C: Testing & Automation** | 20 Marks (LO II) | Test Plan, TDD Rationale, Feature Unit Tests (JUnit 5 + Mockito + MockMvc) | Sprint 2, 3 & 4 | Aug 27 – Aug 29 |
| **Task D: Version Control** | 20 Marks (LO III) | GitHub Repository, Commit History Workflow, Release Tagging `v1.0.0` | Sprint 1–4 | Jul 08 – Aug 31 |

---

## 4. Task A Detailed Specifications (UML Diagrams & Assumptions)

### 4.1 Task A Timeline (July 19 – August 01, 2026)
Task A deliverables are scheduled and completed across Sprint 1 and Sprint 2 leading up to system implementation.

### 4.2 Use Case Diagram Specifications
- **Actors:**
  1. **Receptionist:** Registers patients, views available slots, books appointments, generates bills, processes cash/card payments.
  2. **Administrator:** All Receptionist privileges + manages dentist schedules/slot parameters, configures treatment base costs, views financial revenue analytics.
- **Core Use Cases:**
  - `UC-1: Authenticate Staff User` (BCrypt password check)
  - `UC-2: Register & Search Patient Profile`
  - `UC-3: Define Dentist Shift & Generate Time Slots`
  - `UC-4: Search Available Dentist Slots`
  - `UC-5: Book Appointment` (Includes Overlap Validation)
  - `UC-6: Calculate Bill & Issue Invoice` (Consultation + Treatment - Discount)
  - `UC-7: Generate Clinic Reports` (Daily Schedule, Revenue, Patient History)

### 4.3 Class Diagram Specifications
- **Domain Entities & Relationships:**
  - `User` ($1 \rightarrow N$) `Appointment` (CreatedBy)
  - `Patient` ($1 \rightarrow N$) `Appointment`
  - `Dentist` ($1 \rightarrow N$) `DentistSchedule` ($1 \rightarrow N$) `DentistSessionSlot`
  - `Appointment` ($1 \rightarrow 1$) `Bill`
  - `TreatmentType` ($1 \rightarrow N$) `Appointment`
- **Multi-Tier Services:**
  - `AuthService`, `SlotService`, `AppointmentValidationService`, `BillingService`, `ReportService`.

### 4.4 Sequence Diagram Workflows
1. **User Authentication Sequence:** `Client` $\rightarrow$ `AuthApiController` $\rightarrow$ `AuthService` $\rightarrow$ `UserRepository` $\rightarrow$ `BCryptEncoder`.
2. **Appointment Booking & Overlap Validation Sequence:** `Client` $\rightarrow$ `AppointmentApiController` $\rightarrow$ `AppointmentValidationService` $\rightarrow$ `AppointmentRepository` $\rightarrow$ `SlotRepository`.
3. **Financial Billing Sequence:** `Client` $\rightarrow$ `BillingApiController` $\rightarrow$ `BillingService` $\rightarrow$ `DentistRepository` $\rightarrow$ `TreatmentTypeRepository` $\rightarrow$ `BillRepository`.

### 4.5 Task A Key System Assumptions
1. **Per-Dentist Session Slot Assumption:** Each dentist shift is divided into discrete fixed slots ($5, 15, 30$ min duration) based on dentist specialization, guaranteeing zero over-booking.
2. **Patient Overlap Prevention Assumption:** A patient cannot hold two active appointments whose time windows overlap: $(S_1 < E_2) \land (E_1 > S_2)$ on the same date.
3. **Immutable Bill Snapshot Assumption:** Fees for consultation and base treatment procedures are snapshotted into `Bills` table at invoicing time, remaining unaffected by future rate changes.
4. **Security & Hashing Assumption:** All staff credentials are stored using salted BCrypt hashing (factor 12) for OWASP & GDPR compliance.
