package com.dentalclinic;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import com.dentalclinic.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Unit Test Suite for AppointmentService booking execution and 1-step quick-add patient registration.
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private TreatmentTypeRepository treatmentTypeRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Dentist sampleDentist;
    private Patient samplePatient;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        sampleDentist = new Dentist("Dr. Sarah Chen", "Orthodontist", "0771112233", new BigDecimal("1500.00"));
        sampleDentist.setDentistId(1);

        samplePatient = new Patient("John Doe", "0771234567", "john@gmail.com", "Colombo", "199012345678");
        samplePatient.setPatientId(10);

        futureDate = LocalDate.now().plusDays(2);
    }

    @Test
    @DisplayName("Should successfully book appointment for existing patient")
    void shouldBookAppointmentForExistingPatient() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(10);
        request.setDentistId(1);
        request.setAppointmentDate(futureDate);
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(10, 30));

        when(patientRepository.findById(10)).thenReturn(Optional.of(samplePatient));
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(appointmentRepository.findOverlappingAppointments(eq(1), eq(futureDate), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.countActiveAppointmentsForDentistOnDate(eq(1), eq(futureDate)))
                .thenReturn(0);

        Appointment mockSaved = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, futureDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "BOOKED"
        );
        mockSaved.setAppointmentId(101);

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockSaved);

        AppointmentTicketDTO ticket = appointmentService.bookAppointment(request);

        assertNotNull(ticket);
        assertEquals(101, ticket.getAppointmentId());
        assertEquals(1, ticket.getTokenNumber());
        assertEquals("John Doe", ticket.getPatientName());
        assertEquals("Dr. Sarah Chen", ticket.getDentistName());
    }

    @Test
    @DisplayName("Should register new patient via 1-step quick add and book appointment")
    void shouldQuickAddPatientAndBookAppointment() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setQuickPatientName("Kamal Gunaratne");
        request.setQuickContactNumber("0779998877");
        request.setQuickNic("199512345678");
        request.setDentistId(1);
        request.setAppointmentDate(futureDate);
        request.setStartTime(LocalTime.of(11, 0));

        Patient newPatient = new Patient("Kamal Gunaratne", "0779998877", null, "Clinic Walk-in", "199512345678");
        newPatient.setPatientId(20);

        when(patientRepository.save(any(Patient.class))).thenReturn(newPatient);
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(appointmentRepository.findOverlappingAppointments(eq(1), eq(futureDate), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.countActiveAppointmentsForDentistOnDate(eq(1), eq(futureDate)))
                .thenReturn(1);

        Appointment mockSaved = new Appointment(
                newPatient, sampleDentist, null, null, null, 2, futureDate, LocalTime.of(11, 0), LocalTime.of(11, 30), "BOOKED"
        );
        mockSaved.setAppointmentId(102);

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockSaved);

        AppointmentTicketDTO ticket = appointmentService.bookAppointment(request);

        assertNotNull(ticket);
        assertEquals(2, ticket.getTokenNumber());
        assertEquals("Kamal Gunaratne", ticket.getPatientName());
    }

    @Test
    @DisplayName("Should throw exception when booking time slot overlaps with existing appointment")
    void shouldThrowExceptionWhenTimeSlotOverlaps() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(10);
        request.setDentistId(1);
        request.setAppointmentDate(futureDate);
        request.setStartTime(LocalTime.of(10, 0));

        when(patientRepository.findById(10)).thenReturn(Optional.of(samplePatient));
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        
        Appointment existingApp = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, futureDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "BOOKED"
        );
        when(appointmentRepository.findOverlappingAppointments(eq(1), eq(futureDate), any(), any()))
                .thenReturn(Collections.singletonList(existingApp));

        assertThrows(IllegalStateException.class, () -> appointmentService.bookAppointment(request));
    }
}
