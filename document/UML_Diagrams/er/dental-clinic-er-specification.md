# Entity-Relationship (ER) Diagram Specification

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-er.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A & Database Design) |
| **Database Name** | `SunriseDentalClinic` |
| **Notation Standard** | Crow's Foot ER Notation (UML / Relational Data Model) |

---

## 2. Entities & Key Constraints

1. **`Users`**: Stores system staff accounts (`Admin`, `Receptionist`) with salted BCrypt password hashes.
   - **Primary Key:** `UserID`
   - **Foreign Keys:** `CreatedBy` $\rightarrow$ `Users(UserID)`
2. **`Patients`**: Stores patient demographic profiles.
   - **Primary Key:** `PatientID`
3. **`Dentists`**: Stores dentist professional profiles and individual consultation rates.
   - **Primary Key:** `DentistID`
4. **`TreatmentTypes`**: Catalog of dental procedures and base treatment costs.
   - **Primary Key:** `TreatmentTypeID`
5. **`DentistSchedule`**: Shift schedules defining date, start/end times, and slot duration parameters per dentist.
   - **Primary Key:** `ScheduleID`
   - **Foreign Keys:** `DentistID` $\rightarrow$ `Dentists(DentistID)`
6. **`DentistSessionSlots`**: Time slots generated from a dentist's shift schedule.
   - **Primary Key:** `SlotID`
   - **Foreign Keys:** `ScheduleID` $\rightarrow$ `DentistSchedule(ScheduleID)`, `DentistID` $\rightarrow$ `Dentists(DentistID)`
7. **`Appointments`**: Core transactional table linking Patient, Dentist, Treatment, and Slot.
   - **Primary Key:** `AppointmentNumber`
   - **Foreign Keys:** `PatientID`, `DentistID`, `TreatmentTypeID`, `ScheduleID`, `SlotID`, `CreatedBy`
8. **`Bills`**: Immutable financial snapshot generated upon completing/billing an appointment.
   - **Primary Key:** `BillID`
   - **Foreign Keys:** `AppointmentNumber` (UNIQUE), `PatientID`, `DentistID`, `IssuedBy`
9. **`password_reset_tokens`**: Stores secure single-use tokens for password reset verification.
   - **Primary Key:** `token_id`
   - **Foreign Keys:** `userid` $\rightarrow$ `Users(UserID)`
10. **`clinic_settings`**: Stores key-value system configuration parameters (e.g., clinic charge).
   - **Primary Key:** `settingid`

---

## 3. Relational Cardinalities

* `Dentists` **`||--o{`** `DentistSchedule`: One Dentist has zero or many Shift Schedules.
* `DentistSchedule` **`||--|{`** `DentistSessionSlots`: One Shift Schedule contains one or many Session Slots.
* `Patients` **`||--o{`** `Appointments`: One Patient can book zero or many Appointments.
* `DentistSessionSlots` **`||--||`** `Appointments`: One Session Slot is reserved for exactly one Appointment.
* `Appointments` **`||--||`** `Bills`: One Appointment generates exactly one Bill invoice.
* `Users` **`||--o{`** `password_reset_tokens`: One User can request zero or many Password Reset Tokens.
