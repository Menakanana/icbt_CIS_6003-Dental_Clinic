# Sunrise Dental Clinic - Database Design Document & Data Dictionary

## Overview
This document specifies the relational database schema, data dictionary, primary/foreign key relationships, and integrity constraints for **Sunrise Dental Clinic Management System** (`SunriseDentalClinic`).

---

## 1. Entity-Relationship Data Dictionary

### 1.1 `Users` Table (Staff Authentication)
Stores system users (Administrators and Receptionists) with salted BCrypt password hashes.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `UserID` | `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique user identifier |
| `Username` | `VARCHAR(50)` | `UNIQUE, NOT NULL` | Staff login username |
| `Password` | `VARCHAR(255)` | `NOT NULL` | BCrypt password hash string ($2a$12$...) |
| `Email` | `VARCHAR(100)` | `NULL` | User email address |
| `FullName` | `VARCHAR(100)` | `NOT NULL` | Full name of staff member |
| `Role` | `VARCHAR(50)` | `NOT NULL DEFAULT 'Receptionist'` | Role (`Admin` or `Receptionist`) |
| `IsActive` | `BIT` | `NOT NULL DEFAULT 1` | Account status flag |
| `CreatedDate` | `DATETIME2` | `DEFAULT GETDATE()` | Account creation timestamp |
| `LastLogin` | `DATETIME2` | `NULL` | Last successful login timestamp |
| `CreatedBy` | `INT` | `NULL` | Admin UserID who created this account |

---

### 1.2 `Patients` Table
Stores patient demographic profiles.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `PatientID` | `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique patient identifier |
| `PatientName` | `VARCHAR(100)` | `NOT NULL` | Full name of patient |
| `Address` | `VARCHAR(255)` | `NULL` | Residential address |
| `ContactNumber` | `VARCHAR(15)` | `NOT NULL` | Primary mobile number (e.g. 0771234567) |
| `Email` | `VARCHAR(100)` | `NULL` | Patient email address |
| `DateOfBirth` | `DATE` | `NULL` | Date of birth |
| `Gender` | `CHAR(1)` | `NULL` | Gender (`M`/`F`) |
| `NIC` | `VARCHAR(20)` | `NULL` | National Identity Card number |
| `RegisteredDate`| `DATETIME2` | `DEFAULT GETDATE()` | Registration timestamp |
| `IsActive` | `BIT` | `NOT NULL DEFAULT 1` | Active status flag |

---

### 1.3 `Dentists` Table
Stores dentist profiles and per-dentist consultation fees.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `DentistID` | `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique dentist identifier |
| `DentistName` | `VARCHAR(100)` | `NOT NULL` | Dentist full name with title |
| `Email` | `VARCHAR(100)` | `NULL` | Professional email |
| `ContactNumber` | `VARCHAR(15)` | `NULL` | Contact phone number |
| `Specialization` | `VARCHAR(100)` | `NULL` | Dental specialization |
| `LicenseNumber` | `VARCHAR(50)` | `NULL` | Dental Council license number |
| `Qualifications` | `VARCHAR(255)` | `NULL` | Degrees & certifications |
| `ConsultationFee`| `DECIMAL(10,2)`| `NOT NULL DEFAULT 1500.00`| Individual consultation rate (LKR) |
| `ExperienceYears`| `INT` | `NULL` | Years of clinical practice |
| `IsActive` | `BIT` | `NOT NULL DEFAULT 1` | Active status flag |

---

### 1.4 `TreatmentTypes` Table
Catalog of dental procedures and base treatment costs.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `TreatmentTypeID`| `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique treatment identifier |
| `TreatmentName` | `VARCHAR(100)` | `NOT NULL` | Name of procedure (e.g. Tooth Extraction) |
| `Description` | `VARCHAR(255)` | `NULL` | Description of treatment |
| `BaseCost` | `DECIMAL(10,2)`| `NOT NULL` | Base procedure cost (LKR) |
| `IsActive` | `BIT` | `NOT NULL DEFAULT 1` | Active status flag |

---

### 1.5 `DentistSchedule` Table
Defines dentist work shifts and custom slot duration parameters per date.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `ScheduleID` | `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique shift schedule ID |
| `DentistID` | `INT` | `FOREIGN KEY (Dentists)` | Associated dentist ID |
| `ScheduleDate` | `DATE` | `NOT NULL` | Shift date |
| `SessionStartTime`| `TIME` | `NOT NULL` | Shift start time (e.g. 14:00) |
| `SessionEndTime` | `TIME` | `NOT NULL` | Shift end time (e.g. 15:00) |
| `SlotDurationMinutes`| `INT` | `NOT NULL DEFAULT 30`| Slot length in minutes (5, 15, 30) |
| `MaxPatientsInSession`| `INT` | `NOT NULL` | Calculated max capacity |
| `IsActive` | `BIT` | `NOT NULL DEFAULT 1` | Active shift flag |

---

### 1.6 `DentistSessionSlots` Table
Generated time slots belonging to a dentist schedule.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `SlotID` | `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique slot identifier |
| `ScheduleID` | `INT` | `FOREIGN KEY (DentistSchedule)`| Parent schedule ID |
| `DentistID` | `INT` | `FOREIGN KEY (Dentists)` | Associated dentist ID |
| `SlotNumber` | `INT` | `NOT NULL` | Sequential slot number (1, 2, 3...) |
| `SlotStartTime` | `TIME` | `NOT NULL` | Slot start time (e.g. 14:00) |
| `SlotEndTime` | `TIME` | `NOT NULL` | Slot end time (e.g. 14:05) |
| `IsBooked` | `BIT` | `NOT NULL DEFAULT 0` | Availability flag (0=Available, 1=Booked) |

---

### 1.7 `Appointments` Table
Links Patient, Dentist, Treatment, and Slot for a scheduled visit.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `AppointmentNumber`| `INT` | `PRIMARY KEY IDENTITY(1000,1)`| Unique appointment number |
| `PatientID` | `INT` | `FOREIGN KEY (Patients)` | Patient ID |
| `DentistID` | `INT` | `FOREIGN KEY (Dentists)` | Dentist ID |
| `TreatmentTypeID`| `INT` | `FOREIGN KEY (TreatmentTypes)`| Selected treatment ID |
| `ScheduleID` | `INT` | `FOREIGN KEY (DentistSchedule)`| Dentist schedule ID |
| `SlotID` | `INT` | `FOREIGN KEY (DentistSessionSlots)`| Assigned slot ID |
| `TokenNumber` | `INT` | `NOT NULL` | Sequential shift token number (#1, #2, #3...) |
| `AppointmentDate` | `DATE` | `NOT NULL` | Appointment date |
| `Status` | `VARCHAR(20)` | `NOT NULL DEFAULT 'Scheduled'`| Status (`Scheduled`, `Completed`, `Cancelled`) |
| `Notes` | `VARCHAR(255)`| `NULL` | Booking notes |
| `CreatedBy` | `INT` | `NOT NULL` | UserID who booked appointment |

> **Unique Constraint:** `UQ_Dentist_Slot UNIQUE (DentistID, AppointmentDate, SlotID)` prevents duplicate dentist bookings.

---

### 1.8 `Bills` Table
Stores immutable financial snapshots of issued bills.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `BillID` | `INT` | `PRIMARY KEY IDENTITY(1000,1)`| Unique bill identifier |
| `AppointmentNumber`| `INT` | `UNIQUE, FOREIGN KEY (Appointments)`| Associated appointment |
| `PatientID` | `INT` | `FOREIGN KEY (Patients)` | Patient ID |
| `DentistID` | `INT` | `FOREIGN KEY (Dentists)` | Dentist ID |
| `ConsultationFee`| `DECIMAL(10,2)`| `NOT NULL` | Fee snapshot at billing time |
| `TreatmentCost` | `DECIMAL(10,2)`| `NOT NULL` | Base cost snapshot at billing time |
| `TotalCost` | `DECIMAL(10,2)`| `NOT NULL` | `ConsultationFee + TreatmentCost` |
| `DiscountAmount` | `DECIMAL(10,2)`| `DEFAULT 0.00` | Discount applied (LKR) |
| `FinalAmount` | `DECIMAL(10,2)`| `NOT NULL` | `TotalCost - DiscountAmount` |
| `PaymentStatus` | `VARCHAR(20)`| `DEFAULT 'Pending'` | Status (`Pending`, `Paid`) |
| `PaymentMethod` | `VARCHAR(50)`| `NULL` | Payment method (`Cash`, `Card`) |
| `PaymentDate` | `DATETIME2` | `NULL` | Timestamp of payment |
| `BillDate` | `DATETIME2` | `DEFAULT GETDATE()` | Invoice creation timestamp |
| `IssuedBy` | `INT` | `NOT NULL` | UserID who issued bill |

---

### 1.9 `password_reset_tokens` Table (Security & Authentication)
Stores time-bound, secure single-use tokens for password reset verification.

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `token_id` | `BIGINT` | `PRIMARY KEY IDENTITY(1,1)` | Unique token record identifier |
| `token` | `VARCHAR(100)` | `UNIQUE, NOT NULL` | Generated security token string (`RST-...`) |
| `userid` | `INT` | `FOREIGN KEY (Users), NOT NULL` | Target user account identifier |
| `expiry_date` | `DATETIME2` | `NOT NULL` | Expiration timestamp (30 min duration) |
| `is_used` | `BIT` | `NOT NULL DEFAULT 0` | Single-use consumption flag (0=Valid, 1=Used) |
| `created_date` | `DATETIME2` | `NOT NULL DEFAULT GETDATE()` | Token issuance timestamp |

---

### 1.10 `clinic_settings` Table (System-wide Configuration)
Stores key-value system configuration parameters (e.g. clinic charge, clinic name, address, phone).

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `settingid` | `INT` | `PRIMARY KEY IDENTITY(1,1)` | Unique setting identifier |
| `setting_key` | `VARCHAR(50)` | `UNIQUE, NOT NULL` | Configuration key (e.g. `clinic_charge`) |
| `setting_value` | `VARCHAR(255)` | `NOT NULL` | Configuration value (e.g. `500.00`) |
| `description` | `VARCHAR(255)` | `NULL` | Description of configuration setting |

---

## 2. Foreign Key Relational Diagram

```
[Users] (CreatedBy) ──────────┐
   │                          ▼
   ├──► [password_reset_tokens]  [Patients] ───────────► [Appointments] ◄────────── [TreatmentTypes]
   │                                                         │    ▲
   │                                                         │    │
[Dentists] ──► [DentistSchedule] ────────────────────────────┼──► [DentistSessionSlots]
    │                                                        │
    └────────────────────────────────────────────────────────┼───────────┐
                                                             ▼           ▼
[clinic_settings]                                       [Appointments] ──► [Bills]
```

---

**Specification Version:** 4.1 (Pure Database Specification - Updated with Security & Settings Tables)  
**Status:** Official Data Dictionary for `SunriseDentalClinic`
