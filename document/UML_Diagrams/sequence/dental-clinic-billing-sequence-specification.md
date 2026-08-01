# Sequence Specification: Financial Bill Generation & Invoicing

## 1. Document Control & Overview

| Field | Detail |
|---|---|
| **Diagram Name** | `dental-clinic-billing-sequence.puml` |
| **Module / Task** | CIS6003 - Advanced Programming (Task A - 20 Marks) |
| **Workflow** | Financial Calculation & Immutable Bill Snapshot Generation |

---

## 2. Lifelines & Participants

- **`Receptionist`**: Human actor generating invoice.
- **`BillingApiController`**: REST Web API controller handling `POST /api/bills/generate`.
- **`BillingService`**: Service performing billing math and snapshot creation.
- **`AppointmentRepository`**: Spring Data JPA repository managing `Appointment` entity lookups.
- **`BillRepository`**: Spring Data JPA repository saving immutable `Bill` entities.

---

## 3. Message Sequence & Financial Logic

1. **HTTP Request:** Receptionist posts `BillRequestDTO` (appointmentNumber, discountAmount).
2. **Appointment Lookup:** Service calls `AppointmentRepository.findById(appointmentNumber)`.
   - **`alt` Branch 1 (Not Found):** Throws `IllegalArgumentException`. Returns `404 Not Found`.
3. **Financial Math & Snapshot Creation:**
   - Fetches snapshot values: `consultationFee` (from Dentist) and `baseCost` (from TreatmentType).
   - Computes: $\text{Total Cost} = \text{Consultation Fee} + \text{Base Cost}$.
   - Computes: $\text{Final Amount} = \text{Total Cost} - \text{Discount Amount}$.
   - Saves immutable `Bill` record in `BillRepository`.
   - Returns `200 OK` with printable `BillResponseDTO`.
