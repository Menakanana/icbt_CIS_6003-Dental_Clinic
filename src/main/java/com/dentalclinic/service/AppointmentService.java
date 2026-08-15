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

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DentistRepository dentistRepository,
            TreatmentTypeRepository treatmentTypeRepository,
            @Autowired(required = false) EmailNotificationService emailNotificationService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.emailNotificationService = emailNotificationService;
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

        // Step 1: Resolve Patient (Existing or Inline Quick-Add)
        Patient patient;
        if (request.getPatientId() != null) {
            patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient not found with ID: " + request.getPatientId()));
        } else if (request.getQuickPatientName() != null && !request.getQuickPatientName().trim().isEmpty()) {
            // 1-step Inline Quick-Add Patient registration
            patient = new Patient(
                    request.getQuickPatientName().trim(),
                    request.getQuickContactNumber() != null ? request.getQuickContactNumber().trim() : "0770000000",
                    null,
                    "Clinic Walk-in / Phone Registration",
                    request.getQuickNic() != null ? request.getQuickNic().trim() : null);
            patient = patientRepository.save(patient);
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

        // Step 4: Validate Overlap
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime() != null ? request.getEndTime() : startTime.plusMinutes(30);

        List<Appointment> overlaps = appointmentRepository.findOverlappingAppointments(
                dentist.getDentistId(), request.getAppointmentDate(), startTime, endTime);

        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Selected time slot is no longer available. Please select another slot.");
        }

        // Step 5: Calculate Token Number
        Integer nextToken = appointmentRepository.countActiveAppointmentsForDentistOnDate(dentist.getDentistId(),
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

        Appointment saved = appointmentRepository.save(appointment);

        // Step 7: Construct AppointmentTicketDTO Response
        String timeRange = startTime.format(TIME_FORMATTER) + " - " + endTime.format(TIME_FORMATTER);

        AppointmentTicketDTO ticket = new AppointmentTicketDTO(
                saved.getAppointmentId(),
                saved.getAppointmentId() + 1000,
                saved.getTokenNumber(),
                patient.getPatientId(),
                patient.getPatientName(),
                patient.getContactNumber(),
                patient.getNic(),
                dentist.getDentistId(),
                dentist.getDentistName(),
                dentist.getSpecialization(),
                saved.getAppointmentDate(),
                startTime,
                endTime,
                timeRange,
                saved.getStatus(),
                dentist.getConsultationFee());

        if (emailNotificationService != null) {
            try {
                emailNotificationService.sendBookingConfirmationEmail(ticket, patient.getEmail());
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
        return appointmentRepository.findByAppointmentDateAndStatusNot(LocalDate.now(), "CANCELLED")
                .stream()
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
     * Retrieves full chronological visit history for a specific patient.
     */
    public List<AppointmentTicketDTO> getPatientVisitHistory(Integer patientId) {
        return appointmentRepository.findByPatient_PatientIdOrderByAppointmentDateDesc(patientId)
                .stream()
                .map(this::mapToTicketDTO)
                .collect(Collectors.toList());
    }

    private AppointmentTicketDTO mapToTicketDTO(Appointment app) {
        String timeRange = app.getStartTime().format(TIME_FORMATTER) + " - " + app.getEndTime().format(TIME_FORMATTER);
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
        
        dto.setTreatmentName(app.getTreatmentType() != null ? app.getTreatmentType().getTreatmentName() : "General Dental Consultation");
        dto.setTreatmentBaseCost(app.getTreatmentType() != null ? app.getTreatmentType().getBaseCost() : java.math.BigDecimal.ZERO);
        dto.setNotes(app.getNotes());
        return dto;
    }
}
