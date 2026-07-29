# Use Case Specification: Sunrise Dental Clinic System

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-use-case.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A - 20 Marks) |
| **System Boundary** | Sunrise Dental Clinic Management System |
| **Author** | Development Team |

---

## 2. Real System Actors & Role Hierarchy (2 Human Roles)

In standard UML 2.5 and real-world system design, **Actors represent the real human roles who use the software system**. For Sunrise Dental Clinic, staff members belong to one of 2 distinct human roles:

```
┌──────────────────────────────────────┐
│             Receptionist             │  <- Front-desk staff member
└──────────────────▲───────────────────┘
                   │ inherits (--|>)
┌──────────────────┴───────────────────┐
│            Administrator             │  <- Clinic Manager (All Receptionist privileges + Admin capabilities)
└──────────────────────────────────────┘
```

### 2.1 `Receptionist` (Primary Operational Actor)
- **Real-World Role:** Front-desk staff member at Sunrise Dental Clinic.
- **Responsibilities:** Greets patients, registers demographic profiles, searches available dentist slots, books appointment visits, and calculates/prints patient invoices.
- **Associated Use Cases:** `UC-1: Authenticate User`, `UC-2: Register & Search Patient`, `UC-4: Search Available Slots`, `UC-5: Book Appointment`, `UC-6: Calculate & Issue Bill`.

### 2.2 `Administrator` (Primary Managerial Actor)
- **Real-World Role:** Clinic Manager / System Administrator.
- **Responsibilities:** Manages dentist shift schedules and slot duration parameters ($5, 15, 30$ mins), configures procedure treatment costs, oversees clinic staff accounts, and views financial revenue analytics.
- **Inheritance:** Inherits all operational privileges from `Receptionist` (`Admin --|> Recept`).
- **Associated Use Cases:** All Use Cases (`UC-1` through `UC-7`).

---

## 3. UML Relationship Notations & Definitions (`<<include>>`, `<<extend>>`, `Generalization`)

To satisfy Task A assessment criteria for UML fluency and design rationale, the table and section below define the exact UML relationship stereotypes used in the diagram:

| Relationship Stereotype | Notation Symbol | Standard UML Definition | Application Rationale in Dental System |
|---|---|---|---|
| **`<<include>>`** | Dotted Arrow `..>` | **Mandatory Inclusion:** The base use case **must always** execute the included sub-process to complete its execution. | `UC-5: Book Appointment` **always includes** `UC-5a: Validate Patient Overlap`. Booking cannot occur without checking overlap. |
| **`<<extend>>`** | Dotted Arrow `..>` | **Optional Extension:** The extending use case conditionally adds behavior to the base use case at an extension point. | `UC-5: Book Appointment` **is extended by** `UC-4: Search Available Slots` when selecting a time slot. |
| **`Generalization`** | Solid Arrow `--|>` | **Inheritance:** The child element inherits all behavior, attributes, and use case associations of the parent. | `Administrator --|> Receptionist`. Admin inherits all Receptionist privileges without redundant lines. |

### 3.1 Detailed Rationale for `<<include>>` Relationships
- **`UC-5 (Book Appointment) --<<include>>--> UC-5a (Validate Patient Overlap)`**:
  - *Rationale:* Dynamic overlap prevention is an unconditional business requirement. Every booking attempt must execute the 3-point time check formula before saving the record.
- **`UC-6 (Calculate & Issue Bill) --<<include>>--> UC-6a (Compute Treatment Fee)`**:
  - *Rationale:* Invoicing requires calculating procedure base cost + consultation fee. Computing treatment fee is mandatory for bill generation.

### 3.2 Detailed Rationale for `<<extend>>` Relationships
- **`UC-5 (Book Appointment) <--<<extend>>-- UC-4 (Search Available Slots)`**:
  - *Rationale:* Searching slots can happen independently (e.g. receptionist checking schedule availability). However, when a receptionist decides to book a specific slot, the search flow extends into the appointment booking process.

---

## 4. Detailed Use Case Descriptions

### `UC-1: Authenticate User`
- **Primary Actor:** Receptionist, Administrator.
- **Description:** Verifies username and salted BCrypt password hash.

### `UC-2: Register & Search Patient Profile`
- **Primary Actor:** Receptionist.
- **Description:** Registers new patient demographic profile (Name, Address, Contact Number, NIC) or searches existing records.

### `UC-3: Manage Dentist Schedule & Slots`
- **Primary Actor:** Administrator.
- **Description:** Configures dentist shift dates, working start/end times, and slot duration parameters ($5, 15, 30$ minutes). Automatically triggers `SlotService` dynamic slot generation.

### `UC-4: Search Available Dentist Slots`
- **Primary Actor:** Receptionist.
- **Description:** Filters unbooked session slots per dentist and target date.

### `UC-5: Book Appointment`
- **Primary Actor:** Receptionist.
- **Description:** Reserves an unbooked session slot for a patient.
- **Relationships:**
  - **`<<include>> UC-5a: Validate Patient Overlap`**: MANDATORY check preventing patient double-booking.
  - **`<<extend>> UC-4: Search Available Slots`**: Extends slot search during selection.

### `UC-6: Calculate & Issue Bill`
- **Primary Actor:** Receptionist.
- **Description:** Generates immutable financial invoice snapshot.
- **Formula:** $\text{Final Amount} = \text{Consultation Fee} + \text{Treatment Base Cost} - \text{Discount}$.
- **Relationships:**
  - **`<<include>> UC-6a: Compute Treatment Fee`**: MANDATORY fee computation.

### `UC-7: View Financial & Clinic Reports`
- **Primary Actor:** Administrator.
- **Description:** Accesses Daily Schedule Roster, Financial Revenue Reports, and Patient History.

---

## 5. Plain-Language Explanation of Patient Overlap Check

In `UC-5a: Validate Patient Overlap`, two appointment time windows on the same date overlap if and only if:

$$\text{(Existing Start Time < New End Time) AND (Existing End Time > New Start Time)}$$

### 5.1 Concrete Overlap Example (Blocked - HTTP 409 Conflict)
- **Existing Patient Booking (Appt 1):** Start = `14:00`, End = `14:30`.
- **New Booking Attempt (Appt 2):** Start = `14:15`, End = `14:45`.
- **Validation Check:**
  - Is `Existing Start (14:00) < New End (14:45)`? $\rightarrow \checkmark \text{ (TRUE)}$
  - Is `Existing End (14:30) > New Start (14:15)`? $\rightarrow \checkmark \text{ (TRUE)}$
- **Result:** Both TRUE $\Rightarrow$ **OVERLAP DETECTED!** Booking is rejected with `409 Conflict`.

### 5.2 Concrete Non-Overlap Example (Back-to-Back - Allowed)
- **Existing Patient Booking (Appt 1):** Start = `14:00`, End = `14:30`.
- **New Booking Attempt (Appt 2):** Start = `14:30`, End = `15:00`.
- **Validation Check:**
  - Is `Existing End (14:30) > New Start (14:30)`? $\rightarrow \text{FALSE}$.
- **Result:** Condition fails $\Rightarrow$ **NO OVERLAP!** Back-to-back booking is permitted.

---

## 6. Key Design Decisions & Assumptions

1. **2 Real System Actors:** The Use Case diagram explicitly models the **2 real human roles** (`Receptionist` and `Administrator`) working at Sunrise Dental Clinic, mapping 1-to-1 with database roles (`Users.Role`) and Spring Security permissions (`SecurityConfig`).
2. **Actor Generalization (`Admin --|> Recept`):** Administrator inherits Receptionist capabilities so the manager can perform front-desk bookings or print invoices when needed without duplicating associations.
3. **Mandatory Overlap Prevention (`<<include>> UC-5a`):** Overlap validation is strictly included in `UC-5` to enforce zero double-booking at the service layer.
4. **Immutable Snapshot Invoicing (`<<include>> UC-6a`):** Fees are frozen at billing creation time so subsequent price list changes do not affect past financial records.
