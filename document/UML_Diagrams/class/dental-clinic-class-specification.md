# Class Specification: Sunrise Dental Clinic System

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-class.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A - 20 Marks) |
| **Domain Package** | `com.dentalclinic.entity` & `com.dentalclinic.service` |

---

## 2. Package Architecture & Layer Separation (Why 2 Packages?)

In standard enterprise software engineering and 3-Tier Layered Architecture, classes are organized into distinct UML Packages to enforce **Separation of Concerns**:

```
┌─────────────────────────────────────────────────────────┐
│               com.dentalclinic.service                  │  <- Business Logic Layer
│   (AuthService, SlotService, AppointmentValidationService, BillingService)
└───────────────────────────┬─────────────────────────────┘
                            │ Dependency (..>)
┌───────────────────────────▼─────────────────────────────┐
│               com.dentalclinic.entity                   │  <- Data Model Layer
│   (User, Patient, Dentist, TreatmentType, Appointment, Bill, etc.)
└─────────────────────────────────────────────────────────┘
```

1. **`com.dentalclinic.entity` Package (Data Models):**
   - Contains the 8 core JPA entity classes representing database tables, attributes, and data relationships.
   - Strictly encapsulates state (private fields `-`) and data accessors (public getters/setters `+`).
2. **`com.dentalclinic.service` Package (Business Services):**
   - Contains service interfaces defining operational workflows and business algorithms (e.g. `authenticate()`, `generateSlotsForSchedule()`, `validateAndBook()`, `generateBill()`).
   - Uses Dependency (`..>`) relationships to consume entity instances without corrupting entity data structures.

---

## 3. Class Definitions & Member Details

### 3.1 Entity Classes (`com.dentalclinic.entity`)

#### `User`
- **Purpose:** Represents system staff members and BCrypt login credentials.
- **Attributes:** `- Integer userId`, `- String username`, `- String password`, `- String fullName`, `- String role`, `- Boolean isActive`.
- **Methods:** `+ getUserId() : Integer`, `+ getUsername() : String`, `+ getPassword() : String`, `+ getRole() : String`.

#### `Patient`
- **Purpose:** Stores demographic profiles of clinic patients.
- **Attributes:** `- Integer patientId`, `- String patientName`, `- String address`, `- String contactNumber`, `- String nic`.
- **Methods:** `+ getPatientId() : Integer`, `+ getPatientName() : String`, `+ getContactNumber() : String`.

#### `Dentist`
- **Purpose:** Stores dentist professional profile and consultation rate.
- **Attributes:** `- Integer dentistId`, `- String dentistName`, `- String specialization`, `- BigDecimal consultationFee`.
- **Methods:** `+ getDentistId() : Integer`, `+ getConsultationFee() : BigDecimal`.

#### `TreatmentType`
- **Purpose:** Catalog of available dental procedures and base cost rates.
- **Attributes:** `- Integer treatmentTypeId`, `- String treatmentName`, `- BigDecimal baseCost`.
- **Methods:** `+ getTreatmentTypeId() : Integer`, `+ getBaseCost() : BigDecimal`.

#### `DentistSchedule`
- **Purpose:** Defines shift work dates and slot duration parameters.
- **Attributes:** `- Integer scheduleId`, `- LocalDate scheduleDate`, `- LocalTime sessionStartTime`, `- LocalTime sessionEndTime`, `- Integer slotDurationMinutes`.
- **Methods:** `+ getScheduleId() : Integer`.

#### `DentistSessionSlot`
- **Purpose:** Time slots generated for a dentist's shift.
- **Attributes:** `- Integer slotId`, `- Integer slotNumber`, `- LocalTime slotStartTime`, `- LocalTime slotEndTime`, `- Boolean isBooked`.
- **Methods:** `+ getSlotId() : Integer`, `+ getIsBooked() : Boolean`, `+ markAsBooked() : void`.

#### `Appointment`
- **Purpose:** Links Patient, Dentist, Treatment, and Slot for a scheduled visit.
- **Attributes:** `- Integer appointmentNumber`, `- LocalDate appointmentDate`, `- String status`, `- String notes`.
- **Methods:** `+ getAppointmentNumber() : Integer`, `+ getStatus() : String`.

#### `Bill`
- **Purpose:** Immutable financial snapshot invoice record.
- **Attributes:** `- Integer billId`, `- BigDecimal consultationFee`, `- BigDecimal treatmentCost`, `- BigDecimal totalCost`, `- BigDecimal discountAmount`, `- BigDecimal finalAmount`, `- String paymentStatus`.
- **Methods:** `+ getBillId() : Integer`, `+ getFinalAmount() : BigDecimal`.

---

### 3.2 Service Interfaces (`com.dentalclinic.service`)

#### `AuthService`
- **Purpose:** Handles user credential verification and authentication token generation.
- **Methods:** `+ authenticate(username: String, rawPassword: String) : String`.

#### `SlotService`
- **Purpose:** Handles dynamic per-dentist time slot generation and availability searching.
- **Methods:** `+ generateSlotsForSchedule(schedule: DentistSchedule) : List<DentistSessionSlot>`, `+ getAvailableSlots(dentistId: Integer, date: LocalDate) : List<DentistSessionSlot>`.

#### `AppointmentValidationService`
- **Purpose:** Executes 3-point time-overlap validation and books appointment.
- **Methods:** `+ validateAndBook(dto: BookingRequestDTO) : Appointment`.

#### `BillingService`
- **Purpose:** Calculates financial total ($\text{Consultation} + \text{Base Cost} - \text{Discount}$) and saves immutable invoice snapshot.
- **Methods:** `+ generateBill(appointmentNumber: Integer, discount: BigDecimal) : Bill`.

---

## 4. Relationships & Multiplicities

1. **`User "1" -- "0..*" Appointment` (Association - `creates >`):**
   - One staff user can record or create many appointment bookings (`0..*`).
2. **`Patient "1" -- "0..*" Appointment` (Association - `books >`):**
   - One patient can have zero or many appointment records (`0..*`).
3. **`Dentist "1" -- "0..*" DentistSchedule` (Association - `owns >`):**
   - One dentist owns zero or many shift schedule records (`0..*`).
4. **`DentistSchedule "1" *-- "1..*" DentistSessionSlot` (Composition):**
   - Strong lifecycle ownership. Slots cannot exist without a parent schedule. When a schedule is deleted, all child slots are deleted.
5. **`Dentist "1" -- "0..*" Appointment` (Association - `attends >`):**
   - One dentist can attend many patient appointments (`0..*`).
6. **`TreatmentType "1" -- "0..*" Appointment` (Association - `applies to >`):**
   - One procedure treatment type can apply to many appointment visits (`0..*`).
7. **`Appointment "1" -- "0..1" Bill` (One-to-One Association - `generates >`):**
   - Each appointment generates at most one financial bill snapshot (`0..1`).
8. **`Service ..> Entity` (Dependency):**
   - `AppointmentValidationService ..> Appointment` (creates appointment)
   - `SlotService ..> DentistSessionSlot` (generates slots)
   - `BillingService ..> Bill` (generates bill snapshot)

---

## 5. Design Decisions & OOP Principles

- **Encapsulation:** All class attributes use private `-` access modifiers to prevent direct external mutation.
- **Precision Datatypes:** `BigDecimal` is strictly used for monetary fields (`consultationFee`, `baseCost`, `finalAmount`) to prevent floating-point rounding errors.
- **Repository Abstraction:** Entity classes map to relational tables via Spring Data JPA `@Entity` annotations.
