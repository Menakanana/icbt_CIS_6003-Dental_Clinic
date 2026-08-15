package com.dentalclinic.service;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business Service handling appointment booking, 1-step inline patient
 * registration, and overlap validation.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final EmailNotificationService emailNotificationService;
    private final SlotService slotService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentTypeRepository treatmentTypeRepository,
            @Autowired(required = false) EmailNotificationService emailNotificationService,
            @Autowired(required = false) SlotService slotService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.emailNotificationService = emailNotificationService;
        this.slotService = slotService;
    }

    /**
     * Books an appointment. Supports both Existing Patient selection AND 1-step
     * Inline Quick-Add Patient registration.
     */
    @Transactional
    public AppointmentTicketDTO bookAppointment(BookingRequestDTO request) {
        if (request.getAppointmentDate() != null && request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book an appointment for a past date.");
        }

        // Step 1: Resolve Patient (Existing or Inline Quick-Add with Deduplication)
        Patient patient;
        if (request.getPatientId() != null) {
            patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient not found with ID: " + request.getPatientId()));
        } else if (request.getQuickPatientName() != null && !request.getQuickPatientName().trim().isEmpty()) {
            String quickName = request.getQuickPatientName().trim();
            String quickPhone = request.getQuickContactNumber() != null ? request.getQuickContactNumber().trim() : "0770000000";
            String quickNic = request.getQuickNic() != null ? request.getQuickNic().trim() : null;
            String quickEmail = request.getQuickEmail() != null ? request.getQuickEmail().trim() : null;

            // Check if matching patient already exists in DB to prevent duplicate patient records
            Optional<Patient> existingPatient = Optional.empty();
            if (quickNic != null && !quickNic.isEmpty()) {
                existingPatient = patientRepository.findFirstByNicAndIsActiveTrue(quickNic);
            }
            if (existingPatient.isEmpty() && !quickName.isEmpty() && !quickPhone.isEmpty()) {
                existingPatient = patientRepository.findFirstByPatientNameIgnoreCaseAndContactNumberAndIsActiveTrue(quickName, quickPhone);
            }

            if (existingPatient.isPresent()) {
                patient = existingPatient.get();
                if ((patient.getEmail() == null || patient.getEmail().isBlank()) && quickEmail != null && !quickEmail.isBlank()) {
                    patient.setEmail(quickEmail);
                    patient = patientRepository.save(patient);
                }
            } else {
                patient = new Patient(quickName, quickPhone, quickEmail, "Clinic Walk-in / Phone Registration", quickNic);
                patient = patientRepository.save(patient);
            }
        } else {
            throw new IllegalArgumentException(
                    "Either select an existing patient or provide quick-add patient details.");
        }


        // Step 2: Resolve Dentist
        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        // Step 3: Resolve Treatment Type (Default to Checkup if not provided)
        TreatmentType treatmentType = null;
        if (request.getTreatmentTypeId() != null) {
            treatmentType = treatmentTypeRepository.findById(request.getTreatmentTypeId()).orElse(null);
        }

        // Step 4: Validate Overlap (In-memory evaluation for 100% SQL Server / DB Compatibility)
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime() != null ? request.getEndTime() : startTime.plusMinutes(30);

        List<Appointment> activeAppointments = appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(
                dentist.getDentistId(), request.getAppointmentDate(), "CANCELLED");

        boolean hasOverlap = activeAppointments.stream()
                .anyMatch(a -> (startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime())));

        if (hasOverlap) {
            throw new IllegalStateException("Selected time slot is no longer available. Please select another slot.");
        }

        // Step 5: Resolve Token Number — honour the slot the user clicked on the frontend.
        // The slot grid assigns tokens by time-position (1, 2, 3 …); always trust that value.
        // Fall back to COUNT + 1 only when the token is not supplied (e.g. direct API calls).
        Integer nextToken = (request.getTokenNumber() != null && request.getTokenNumber() > 0)
                ? request.getTokenNumber()
                : appointmentRepository.countActiveAppointmentsForDentistOnDate(dentist.getDentistId(),
                        request.getAppointmentDate()) + 1;

        // Step 6: Create & Save Appointment
        Appointment appointment = new Appointment(
                patient,
                dentist,
                treatmentType,
                null,
                null,
                nextToken,
                request.getAppointmentDate(),
                startTime,
                endTime,
                "BOOKED");
        appointment.setNotes(request.getNotes());
        if (treatmentType != null) {
            appointment.getTreatmentProcedures().add(treatmentType);
        }

        boolean payNow = !"PAY_LATER".equalsIgnoreCase(request.getInitialPaymentOption());
        java.math.BigDecimal fee = dentist.getConsultationFee() != null ? dentist.getConsultationFee() : java.math.BigDecimal.ZERO;
        java.math.BigDecimal clinicFee = new java.math.BigDecimal("500.00");
        java.math.BigDecimal deposit = fee.add(clinicFee);

        if (payNow) {
            appointment.setPaymentStatus("PAID_DEPOSIT");
            appointment.setPaidAmount(deposit);
        } else {
            appointment.setPaymentStatus("UNPAID");
            appointment.setPaidAmount(java.math.BigDecimal.ZERO);
        }
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().trim().isEmpty()) {
            appointment.setPaymentMethod(request.getPaymentMethod().trim());
        }

        Appointment saved = appointmentRepository.save(appointment);

        // Step 7: Construct AppointmentTicketDTO Response
        AppointmentTicketDTO ticket = mapToTicketDTO(saved);

        if (emailNotificationService != null) {
            try {
                if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
                    emailNotificationService.sendBookingConfirmationEmail(ticket, patient.getEmail());
                }
            } catch (Exception ex) {
                // Non-blocking log
            }
        }

        return ticket;
    }

    /**
     * Retrieves all active scheduled appointments for today.
     */
    public List<AppointmentTicketDTO> getTodayAppointments() {
        return appointmentRepository.findByAppointmentDateAndStatusNotOrderByTokenNumberAsc(LocalDate.now(), "CANCELLED")
                .stream()
                .map(this::mapToTicketDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all active scheduled appointments for a specific date.
     */
    public List<AppointmentTicketDTO> getAppointmentsByDate(LocalDate date) {
        if (date == null) date = LocalDate.now();
        return appointmentRepository.findByAppointmentDateAndStatusNotOrderByTokenNumberAsc(date, "CANCELLED")
                .stream()
                .map(this::mapToTicketDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all non-cancelled appointments across all dates for administrative roster and billing grid pagination.
     */
    public List<AppointmentTicketDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .sorted((a1, a2) -> {
                    if (a1.getAppointmentDate() == null || a2.getAppointmentDate() == null) return 0;
                    int dateComp = a2.getAppointmentDate().compareTo(a1.getAppointmentDate());
                    if (dateComp != 0) return dateComp;
                    return Integer.compare(a1.getTokenNumber(), a2.getTokenNumber());
                })
                .map(this::mapToTicketDTO)
                .collect(Collectors.toList());
    }

    /**
     * Searches for a single appointment by ID.
     */
    public AppointmentTicketDTO getAppointmentById(Integer appointmentId) {
        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));
        return mapToTicketDTO(app);
    }

    /**
     * Searches appointments by patient name, contact number, NIC, or appointment ID.
     */
    public List<AppointmentTicketDTO> searchAppointments(String query) {
        return searchAppointments(query, null);
    }

    /**
     * Searches appointments by patient name, contact number, NIC, or appointment ID, optionally filtered by date.
     */
    public List<AppointmentTicketDTO> searchAppointments(String query, LocalDate date) {
        boolean hasQuery = (query != null && !query.isBlank());
        List<Appointment> list = new java.util.ArrayList<>();

        if (hasQuery) {
            String trimmed = query.trim();
            list = new java.util.ArrayList<>(appointmentRepository.searchAppointments(trimmed));

            // Clean prefixes like "APT-", "APT ", "APT#", "#" (case-insensitive)
            String cleanDigits = trimmed.replaceAll("(?i)^APT[-#\\s]*", "").replaceAll("^#", "").trim();

            if (list.isEmpty() && !cleanDigits.equals(trimmed) && cleanDigits.matches("\\d+")) {
                list = new java.util.ArrayList<>(appointmentRepository.searchAppointments(cleanDigits));
            }
            if (list.isEmpty() && cleanDigits.matches("\\d+")) {
                try {
                    Integer id = Integer.parseInt(cleanDigits);
                    appointmentRepository.findById(id).ifPresent(list::add);
                } catch (NumberFormatException ignored) {}
            }

            if (date != null) {
                list = list.stream()
                        .filter(a -> date.equals(a.getAppointmentDate()))
                        .collect(Collectors.toList());
            }
        } else if (date != null) {
            list = appointmentRepository.findByAppointmentDateAndStatusNot(date, "CANCELLED");
        }

        return list.stream()
                .map(this::mapToTicketDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves full chronological visit history for a specific patient.
     */
    public List<AppointmentTicketDTO> getPatientVisitHistory(Integer patientId) {
        return appointmentRepository.findByPatient_PatientIdOrderByAppointmentDateDescStartTimeDesc(patientId)
                .stream()
                .map(this::mapToTicketDTO)
                .collect(Collectors.toList());
    }

    /**
     * Reschedules an existing appointment to a new date, dentist, and time slot.
     */
    @Transactional
    public AppointmentTicketDTO rescheduleAppointment(Integer appointmentId, Integer newDentistId, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime, Integer tokenNumber) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        String currentStatus = appointment.getStatus() != null ? appointment.getStatus().toUpperCase() : "BOOKED";
        if ("COMPLETED".equals(currentStatus) || "CLOSED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new IllegalStateException("Cannot reschedule appointment APT-#" + appointmentId + " with status '" + currentStatus + "'. Finished consultations and cancelled tickets are closed.");
        }

        boolean isPastAppointment = appointment.getAppointmentDate() != null && appointment.getAppointmentDate().isBefore(LocalDate.now());
        boolean hasPaidDeposit = (appointment.getPaidAmount() != null && appointment.getPaidAmount().compareTo(java.math.BigDecimal.ZERO) > 0)
                || "PAID_DEPOSIT".equalsIgnoreCase(appointment.getPaymentStatus()) 
                || "FULL_PAID".equalsIgnoreCase(appointment.getPaymentStatus());

        if (isPastAppointment && !hasPaidDeposit) {
            throw new IllegalStateException("Cannot reschedule an unpaid missed appointment from a past date (" + appointment.getAppointmentDate() + "). Please create a new appointment.");
        }

        if (newDate == null || newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot reschedule to a past date.");
        }

        Dentist targetDentist;
        if (newDentistId != null && !newDentistId.equals(appointment.getDentist().getDentistId())) {
            targetDentist = dentistRepository.findById(newDentistId)
                    .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + newDentistId));
        } else {
            targetDentist = appointment.getDentist();
        }

        if (Boolean.FALSE.equals(targetDentist.getIsActive())) {
            throw new IllegalStateException("Doctor profile for '" + targetDentist.getDentistName() + "' is inactive.");
        }

        List<com.dentalclinic.dto.SlotDTO> availableSlots = slotService != null ?
                slotService.generateAvailableSlots(targetDentist.getDentistId(), newDate) : List.of();

        if (availableSlots.isEmpty()) {
            throw new IllegalStateException("Doctor " + targetDentist.getDentistName() + " is off-duty or has no available shift on " + newDate + ".");
        }

        com.dentalclinic.dto.SlotDTO matchedSlot = null;
        if (tokenNumber != null) {
            matchedSlot = availableSlots.stream()
                    .filter(s -> s.getSlotId().equals(tokenNumber) || s.getTokenNumber().equals(tokenNumber))
                    .findFirst().orElse(null);
        }
        if (matchedSlot == null && newStartTime != null) {
            matchedSlot = availableSlots.stream()
                    .filter(s -> s.getStartTime().equals(newStartTime))
                    .findFirst().orElse(null);
        }

        LocalTime startTime;
        LocalTime endTime;
        Integer assignedToken;

        if (matchedSlot != null) {
            boolean isSameApptSlot = newDate.equals(appointment.getAppointmentDate()) &&
                    targetDentist.getDentistId().equals(appointment.getDentist().getDentistId()) &&
                    matchedSlot.getStartTime().equals(appointment.getStartTime());

            if (!matchedSlot.isAvailable() && !isSameApptSlot) {
                throw new IllegalStateException("Selected time slot (" + matchedSlot.getDisplayTime() + ") is no longer available.");
            }

            startTime = matchedSlot.getStartTime();
            endTime = matchedSlot.getEndTime();
            assignedToken = matchedSlot.getTokenNumber();
        } else {
            startTime = newStartTime != null ? newStartTime : LocalTime.of(9, 0);
            endTime = newEndTime != null ? newEndTime : startTime.plusMinutes(30);

            List<Appointment> activeApps = appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(
                    targetDentist.getDentistId(), newDate, "CANCELLED");
            boolean hasOverlap = activeApps.stream()
                    .filter(a -> !a.getAppointmentId().equals(appointmentId))
                    .anyMatch(a -> (startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime())));

            if (hasOverlap) {
                throw new IllegalStateException("Selected time slot on " + newDate + " is no longer available. Please pick another slot.");
            }
            assignedToken = tokenNumber != null ? tokenNumber : (appointmentRepository.countActiveAppointmentsForDentistOnDate(targetDentist.getDentistId(), newDate) + 1);
        }

        appointment.setDentist(targetDentist);
        appointment.setAppointmentDate(newDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setTokenNumber(assignedToken);
        appointment.setStatus("CONFIRMED");

        Appointment saved = appointmentRepository.save(appointment);
        AppointmentTicketDTO ticket = mapToTicketDTO(saved);

        if (emailNotificationService != null) {
            try {
                emailNotificationService.sendBookingConfirmationEmail(ticket, ticket.getPatientEmail());
            } catch (Exception ex) {
                // Log warning without failing transaction
            }
        }

        return ticket;
    }

    public AppointmentTicketDTO rescheduleAppointment(Integer appointmentId, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime) {
        return rescheduleAppointment(appointmentId, null, newDate, newStartTime, newEndTime, null);
    }

    private AppointmentTicketDTO mapToTicketDTO(Appointment app) {
        String timeRange = app.getStartTime().format(TIME_FORMATTER);
        AppointmentTicketDTO dto = new AppointmentTicketDTO(
                app.getAppointmentId(),
                app.getAppointmentId() + 1000,
                app.getTokenNumber(),
                app.getPatient().getPatientId(),
                app.getPatient().getPatientName(),
                app.getPatient().getContactNumber(),
                app.getPatient().getNic(),
                app.getDentist().getDentistId(),
                app.getDentist().getDentistName(),
                app.getDentist().getSpecialization(),
                app.getAppointmentDate(),
                app.getStartTime(),
                app.getEndTime(),
                timeRange,
                app.getStatus(),
                app.getDentist().getConsultationFee());
        
        dto.setPatientEmail(app.getPatient() != null ? app.getPatient().getEmail() : null);
        dto.setTreatmentName(app.getTreatmentType() != null ? app.getTreatmentType().getTreatmentName() : "General Dental Consultation");
        dto.setTreatmentBaseCost(app.getTreatmentType() != null ? app.getTreatmentType().getBaseCost() : java.math.BigDecimal.ZERO);
        dto.setNotes(app.getNotes());
        dto.setMedicalHistory(app.getPatient() != null ? app.getPatient().getMedicalHistory() : null);
        dto.setRelationship(app.getPatient() != null ? app.getPatient().getRelationship() : "Self");
        dto.setPaymentStatus(app.getPaymentStatus() != null ? app.getPaymentStatus() : "UNPAID");
        dto.setPaidAmount(app.getPaidAmount() != null ? app.getPaidAmount() : java.math.BigDecimal.ZERO);
        dto.setPaymentMethod(app.getPaymentMethod() != null ? app.getPaymentMethod() : "Cash");

        String sessionWindow;
        if (app.getSchedule() != null && app.getSchedule().getSessionStartTime() != null && app.getSchedule().getSessionEndTime() != null) {
            sessionWindow = app.getSchedule().getSessionStartTime().format(TIME_FORMATTER) + " - " + app.getSchedule().getSessionEndTime().format(TIME_FORMATTER);
        } else {
            sessionWindow = app.getStartTime().format(TIME_FORMATTER) + " - " + app.getEndTime().format(TIME_FORMATTER);
        }
        dto.setSessionTimeWindow(sessionWindow);
        return dto;
    }
}
