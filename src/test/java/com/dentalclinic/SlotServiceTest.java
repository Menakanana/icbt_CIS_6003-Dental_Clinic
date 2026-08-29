package com.dentalclinic;

import com.dentalclinic.dto.SlotDTO;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.DentistSchedule;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.DentistScheduleRepository;
import com.dentalclinic.service.SlotService;
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
 * Unit Test Suite for SlotService dynamic slot generation and overlap calculations.
 */
@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private DentistScheduleRepository scheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private SlotService slotService;

    private Dentist sampleDentist;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        sampleDentist = new Dentist("Dr. Sarah Chen", "Orthodontist", "0771112233", new BigDecimal("1500.00"));
        sampleDentist.setDentistId(1);
        futureDate = LocalDate.now().plusDays(5);
    }

    @Test
    @DisplayName("Should generate 8 dynamic 30-minute slots for shift 09:00 AM to 01:00 PM")
    void shouldGenerateDefaultSlots() {
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(scheduleRepository.findByDentist_DentistIdAndScheduleDate(eq(1), eq(futureDate)))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
                .thenReturn(Collections.emptyList());

        List<SlotDTO> slots = slotService.generateAvailableSlots(1, futureDate);

        assertNotNull(slots);
        assertEquals(8, slots.size()); // (13:00 - 09:00) / 30 mins = 8 slots
        assertTrue(slots.get(0).getIsAvailable());
        assertEquals("09:00 AM", slots.get(0).getDisplayTime());
        assertEquals(1, slots.get(0).getTokenNumber());
    }

    @Test
    @DisplayName("Should respect custom DentistSchedule shift hours and slot duration")
    void shouldRespectCustomDentistSchedule() {
        DentistSchedule customSchedule = new DentistSchedule(
                sampleDentist, futureDate, LocalTime.of(14, 0), LocalTime.of(16, 0), 15, 8
        );

        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(scheduleRepository.findByDentist_DentistIdAndScheduleDate(eq(1), eq(futureDate)))
                .thenReturn(List.of(customSchedule));
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
                .thenReturn(Collections.emptyList());

        List<SlotDTO> slots = slotService.generateAvailableSlots(1, futureDate);

        assertNotNull(slots);
        assertEquals(8, slots.size()); // 2 hours / 15 mins = 8 slots
        assertEquals("02:00 PM", slots.get(0).getDisplayTime());
    }

    @Test
    @DisplayName("Should generate sequential tokens across multiple session blocks while excluding breaks")
    void shouldGenerateMultiSessionSlotsWithBreaks() {
        DentistSchedule session1 = new DentistSchedule(
                sampleDentist, futureDate, LocalTime.of(9, 0), LocalTime.of(11, 0), 20, 6
        );
        DentistSchedule session2 = new DentistSchedule(
                sampleDentist, futureDate, LocalTime.of(14, 0), LocalTime.of(16, 0), 20, 6
        );

        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(scheduleRepository.findByDentist_DentistIdAndScheduleDate(eq(1), eq(futureDate)))
                .thenReturn(List.of(session1, session2));
        when(appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(eq(1), eq(futureDate), eq("CANCELLED")))
                .thenReturn(Collections.emptyList());

        List<SlotDTO> slots = slotService.generateAvailableSlots(1, futureDate);

        assertNotNull(slots);
        assertEquals(12, slots.size()); // 6 tokens in morning session + 6 tokens in evening session = 12 total
        assertEquals(1, slots.get(0).getTokenNumber());
        assertEquals("09:00 AM", slots.get(0).getDisplayTime());
        assertEquals(7, slots.get(6).getTokenNumber());
        assertEquals("02:00 PM", slots.get(6).getDisplayTime());
    }

    @Test
    @DisplayName("Should return empty list of slots when Doctor is explicitly Off Duty")
    void shouldReturnEmptySlotsWhenDoctorIsOffDuty() {
        DentistSchedule offDutySchedule = new DentistSchedule(
                sampleDentist, futureDate, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, 10
        );
        offDutySchedule.setIsActive(false);

        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(scheduleRepository.findByDentist_DentistIdAndScheduleDate(eq(1), eq(futureDate)))
                .thenReturn(List.of(offDutySchedule));

        List<SlotDTO> slots = slotService.generateAvailableSlots(1, futureDate);

        assertNotNull(slots);
        assertTrue(slots.isEmpty());
    }
}
