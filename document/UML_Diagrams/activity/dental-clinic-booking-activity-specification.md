# Activity Specification: Dynamic Booking & Overlap Prevention Logic

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-booking-activity.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A & Task B) |
| **Workflow** | Activity Flowchart for Appointment Validation & Booking |

---

## 2. Swimlanes & Layer Responsibilities

1. **`Presentation Tier (Web UI)`**: Captures receptionist input form data and renders success/error feedback.
2. **`Application Tier (REST Web API)`**: Executes DTO validation, slot availability checking, overlap detection math, and entity persistence.

---

## 3. Plain-Language Overlap Decision Logic

In the **Patient Overlap Decision Diamond**, the system evaluates whether the requested appointment time window overlaps with any existing booking for the same patient on the target date.

$$\text{Overlap Condition: (Existing Start Time < New End Time) AND (Existing End Time > New Start Time)}$$

### 3.1 Numerical Example (Overlap Conflict Detected)
- **Existing Appt 1:** Start = `14:00`, End = `14:30`.
- **New Attempt Appt 2:** Start = `14:15`, End = `14:45`.
- **Calculation:** `(14:00 < 14:45) AND (14:30 > 14:15) => TRUE`.
- **System Action:** Branch to `Return 409 Conflict` (Patient Double-Booked).

### 3.2 Numerical Example (Back-to-Back Allowed)
- **Existing Appt 1:** Start = `14:00`, End = `14:30`.
- **New Attempt Appt 2:** Start = `14:30`, End = `15:00`.
- **Calculation:** `(14:30 > 14:30) => FALSE`.
- **System Action:** Branch to `Mark Slot Booked` $\rightarrow$ `Save Appointment` $\rightarrow$ `Return 201 Created`.

---

## 4. Activity Nodes & Decision Logic

- **Initial Node `(*)`**: Starts upon Receptionist submitting booking request.
- **DTO Validation Decision Diamond**: Validates `@NotNull`, `@Positive`, and `@FutureOrPresent` annotations.
  - *No Branch:* Generates `400 Bad Request`.
  - *Yes Branch:* Moves to slot availability check.
- **Slot Availability Decision Diamond**: Checks `isBooked` flag on `DentistSessionSlot`.
  - *No Branch:* Generates `409 Conflict` (Slot Unavailable).
  - *Yes Branch:* Moves to 3-point patient time-overlap check.
- **Patient Overlap Decision Diamond**: Evaluates `(Existing Start < New End AND Existing End > New Start)` on `AppointmentRepository`.
  - *Yes Branch (Overlap Exists):* Generates `409 Conflict` (Patient Double-Booked).
  - *No Branch (Success Path):* Marks slot booked, saves appointment, returns `201 Created`.
- **Final Nodes `(*)`**: Terminates workflow cleanly in both success and error paths.
