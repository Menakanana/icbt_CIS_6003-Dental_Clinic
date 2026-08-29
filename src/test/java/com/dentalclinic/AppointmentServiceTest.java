package com.dentalclinic;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.DentistScheduleRepository;
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
import java.util.List;
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

    @Mock
    private DentistScheduleRepository scheduleRepository;

    private AppointmentService appointmentService;

    private Dentist sampleDentist;
    private Patient samplePatient;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        com.dentalclinic.service.SlotService slotService = new com.dentalclinic.service.SlotService(dentistRepository, scheduleRepository, appointmentRepository);
        appointmentService = new AppointmentService(appointmentRepository, patientRepository, dentistRepository, treatmentTypeRepository, null, slotService);

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
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
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

        when(patientRepository.findFirstByNicAndIsActiveTrue("199512345678")).thenReturn(Optional.empty());
        when(patientRepository.findFirstByPatientNameIgnoreCaseAndContactNumberAndIsActiveTrue("Kamal Gunaratne", "0779998877")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(newPatient);
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
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
    @DisplayName("Should reuse existing patient on quick add if NIC or Name+Phone already exists")
    void shouldReuseExistingPatientOnQuickAddDeduplication() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setQuickPatientName("John Doe");
        request.setQuickContactNumber("0771234567");
        request.setQuickNic("199012345678");
        request.setDentistId(1);
        request.setAppointmentDate(futureDate);
        request.setStartTime(LocalTime.of(11, 0));

        when(patientRepository.findFirstByNicAndIsActiveTrue("199012345678")).thenReturn(Optional.of(samplePatient));
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.countActiveAppointmentsForDentistOnDate(eq(1), eq(futureDate)))
                .thenReturn(0);

        Appointment mockSaved = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, futureDate, LocalTime.of(11, 0), LocalTime.of(11, 30), "BOOKED"
        );
        mockSaved.setAppointmentId(103);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockSaved);

        AppointmentTicketDTO ticket = appointmentService.bookAppointment(request);

        assertNotNull(ticket);
        assertEquals("John Doe", ticket.getPatientName());
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
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
                .thenReturn(Collections.singletonList(existingApp));

        assertThrows(IllegalStateException.class, () -> appointmentService.bookAppointment(request));
    }

    @Test
    @DisplayName("Should throw exception when booking with non-existent patient ID")
    void shouldThrowExceptionWhenPatientNotFound() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(999);
        request.setDentistId(1);

        when(patientRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(com.dentalclinic.exception.ResourceNotFoundException.class, () -> appointmentService.bookAppointment(request));
    }

    @Test
    @DisplayName("Should throw exception when booking with non-existent dentist ID")
    void shouldThrowExceptionWhenDentistNotFound() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setPatientId(10);
        request.setDentistId(999);

        when(patientRepository.findById(10)).thenReturn(Optional.of(samplePatient));
        when(dentistRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(com.dentalclinic.exception.ResourceNotFoundException.class, () -> appointmentService.bookAppointment(request));
    }

    @Test
    @DisplayName("Should return empty list when no appointments exist for today")
    void shouldGetTodayAppointments_EmptyList() {
        when(appointmentRepository.findByAppointmentDateAndStatusNotOrderByTokenNumberAsc(any(LocalDate.class), eq("CANCELLED"))).thenReturn(Collections.emptyList());

        var todayList = appointmentService.getTodayAppointments();
        assertNotNull(todayList);
        assertTrue(todayList.isEmpty());
    }

    @Test
    @DisplayName("Should successfully retrieve appointment by ID")
    void shouldGetAppointmentById_Success() {
        Appointment app = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, futureDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "CONFIRMED"
        );
        app.setAppointmentId(50);
        when(appointmentRepository.findById(50)).thenReturn(Optional.of(app));

        AppointmentTicketDTO ticket = appointmentService.getAppointmentById(50);
        assertNotNull(ticket);
        assertEquals(50, ticket.getAppointmentId());
    }

    @Test
    @DisplayName("Should successfully reschedule appointment to new date and time")
    void shouldRescheduleAppointment_Success() {
        Appointment app = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, futureDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "CONFIRMED"
        );
        app.setAppointmentId(50);

        LocalDate nextWeek = futureDate.plusDays(7);
        LocalTime newStart = LocalTime.of(14, 0);
        LocalTime newEnd = LocalTime.of(14, 30);

        when(appointmentRepository.findById(50)).thenReturn(Optional.of(app));
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(scheduleRepository.findByDentist_DentistIdAndScheduleDate(1, nextWeek)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(1, nextWeek, "CANCELLED")).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentTicketDTO rescheduled = appointmentService.rescheduleAppointment(50, nextWeek, LocalTime.of(9, 0), LocalTime.of(9, 30));

        assertNotNull(rescheduled);
        assertEquals(50, rescheduled.getAppointmentId());
        assertEquals(nextWeek, rescheduled.getAppointmentDate());
        assertEquals(LocalTime.of(9, 0), rescheduled.getStartTime());
        assertEquals(1, rescheduled.getTokenNumber());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when rescheduling a COMPLETED or CANCELLED appointment")
    void shouldRescheduleAppointment_StatusGuard_ThrowsException() {
        Appointment app = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, futureDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "COMPLETED"
        );
        app.setAppointmentId(50);

        when(appointmentRepository.findById(50)).thenReturn(Optional.of(app));

        assertThrows(IllegalStateException.class, () ->
                appointmentService.rescheduleAppointment(50, futureDate.plusDays(1), LocalTime.of(11, 0), null)
        );
    }

    @Test
    @DisplayName("Should throw IllegalStateException when rescheduling an unpaid past appointment")
    void shouldRescheduleUnpaidPastAppointment_ThrowsException() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        Appointment app = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, pastDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "BOOKED"
        );
        app.setAppointmentId(60);
        app.setPaidAmount(BigDecimal.ZERO);
        app.setPaymentStatus("UNPAID");

        when(appointmentRepository.findById(60)).thenReturn(Optional.of(app));

        assertThrows(IllegalStateException.class, () ->
                appointmentService.rescheduleAppointment(60, futureDate, LocalTime.of(9, 0), null)
        );
    }

    @Test
    @DisplayName("Should allow rescheduling a paid past appointment with Deposit Carry-Over")
    void shouldReschedulePaidPastAppointment_DepositCarryOver_Success() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        Appointment app = new Appointment(
                samplePatient, sampleDentist, null, null, null, 1, pastDate, LocalTime.of(10, 0), LocalTime.of(10, 30), "BOOKED"
        );
        app.setAppointmentId(70);
        app.setPaidAmount(new BigDecimal("2000.00"));
        app.setPaymentStatus("PAID_DEPOSIT");
        app.setPaymentMethod("Cash");

        LocalDate nextWeek = futureDate.plusDays(2);
        when(appointmentRepository.findById(70)).thenReturn(Optional.of(app));
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(scheduleRepository.findByDentist_DentistIdAndScheduleDate(1, nextWeek)).thenReturn(Collections.emptyList());
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(1, nextWeek, "CANCELLED")).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentTicketDTO ticket = appointmentService.rescheduleAppointment(70, nextWeek, LocalTime.of(9, 0), LocalTime.of(9, 30));

        assertNotNull(ticket);
        assertEquals(70, ticket.getAppointmentId());
        assertEquals(nextWeek, ticket.getAppointmentDate());
        assertEquals(new BigDecimal("2000.00"), ticket.getPaidAmount());
        assertEquals("PAID_DEPOSIT", ticket.getPaymentStatus());
    }
}
