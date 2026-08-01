# Sequence Specification: Appointment Booking & Overlap Prevention

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-booking-sequence.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A - 20 Marks) |
| **Workflow** | Patient Appointment Booking & 3-Point Overlap Validation |

---

## 2. Lifelines & Participants

- **`Receptionist`**: Primary human actor initiating booking request.
- **`AppointmentApiController`**: REST Web API controller handling HTTP `POST /api/appointments/book`.
- **`AppointmentValidationService`**: Service containing overlap detection & slot booking rules.
- **`SlotRepository`**: Spring Data JPA repository managing `DentistSessionSlot` entities.
- **`AppointmentRepository`**: Spring Data JPA repository managing `Appointment` entities.

---

## 3. Message Sequence & Conditional Logic

1. **HTTP Request:** Receptionist sends `POST /api/appointments/book` with `BookingRequestDTO`.
2. **Slot Availability Verification:** Service queries `SlotRepository.findById(slotId)`.
   - **`alt` Branch 1 (Slot Unavailable):** If `isBooked == true`, service throws `IllegalStateException`. Controller returns `409 Conflict`.
3. **Patient Overlap Prevention Check:** If slot is free, Service queries `AppointmentRepository.findPatientOverlaps(...)`.
   - **`alt` Branch 2 (Patient Double-Booked):** If overlap formula $(S_1 < E_2) \land (E_1 > S_2)$ evaluates to true, service throws `IllegalStateException`. Controller returns `409 Conflict`.
   - **`else` Branch 3 (Success Path):** Slot marked booked, new `Appointment` entity saved, returns `201 Created`.

---

## 4. Exceptional Flows & Error Codes

- **HTTP `400 Bad Request`**: Returned if DTO `@Valid` annotations fail (e.g. past appointment date).
- **HTTP `409 Conflict`**: Returned if slot is already booked OR patient has overlapping booking.
