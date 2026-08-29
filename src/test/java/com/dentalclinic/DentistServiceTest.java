package com.dentalclinic;

import com.dentalclinic.dto.DentistDTO;
import com.dentalclinic.dto.DentistScheduleDTO;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.DentistSchedule;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.DentistScheduleRepository;
import com.dentalclinic.service.DentistService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DentistServiceTest {

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private DentistScheduleRepository scheduleRepository;

    @InjectMocks
    private DentistService dentistService;

    private Dentist sampleDentist;

    @BeforeEach
    public void setUp() {
        sampleDentist = new Dentist("Dr. Sarah Chen", "Orthodontics", "0771112233", new BigDecimal("1500.00"));
        sampleDentist.setDentistId(1);
    }

    @Test
    @DisplayName("Unit Test: saveDentist Creates New Doctor Profile")
    public void testSaveDentist() {
        when(dentistRepository.existsByDentistNameIgnoreCaseAndIsActiveTrue("Dr. Sarah Chen")).thenReturn(false);
        when(dentistRepository.save(any(Dentist.class))).thenReturn(sampleDentist);

        DentistDTO inputDTO = new DentistDTO(null, "Dr. Sarah Chen", "Orthodontics", "0771112233", new BigDecimal("1500.00"));
        DentistDTO savedDTO = dentistService.saveDentist(inputDTO);

        assertNotNull(savedDTO);
        assertEquals(1, savedDTO.getDentistId());
        assertEquals("Dr. Sarah Chen", savedDTO.getDentistName());
        assertEquals(new BigDecimal("1500.00"), savedDTO.getConsultationFee());
    }

    @Test
    @DisplayName("Unit Test: saveDentist Rejects Duplicate Doctor Name")
    public void testSaveDentistDuplicateRejection() {
        when(dentistRepository.existsByDentistNameIgnoreCaseAndIsActiveTrue("Dr. Sarah Chen")).thenReturn(true);

        DentistDTO inputDTO = new DentistDTO(null, "Dr. Sarah Chen", "Orthodontics", "0771112233", new BigDecimal("1500.00"));

        assertThrows(IllegalArgumentException.class, () -> {
            dentistService.saveDentist(inputDTO);
        });
    }

    @Test
    @DisplayName("Unit Test: saveSchedule Configures Daily Doctor Shift")
    public void testSaveSchedule() {
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));

        DentistSchedule schedule = new DentistSchedule(
                sampleDentist,
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                30,
                15
        );
        schedule.setScheduleId(10);

        when(scheduleRepository.findFirstByDentist_DentistIdAndScheduleDateAndIsActiveTrue(1, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(scheduleRepository.save(any(DentistSchedule.class))).thenReturn(schedule);

        DentistScheduleDTO inputDTO = new DentistScheduleDTO(1, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), 30);
        inputDTO.setMaxPatientsInSession(16);
        DentistScheduleDTO result = dentistService.saveSchedule(inputDTO);

        assertNotNull(result);
        assertEquals(10, result.getScheduleId());
        assertEquals("Dr. Sarah Chen", result.getDentistName());
        assertEquals(30, result.getSlotDurationMinutes());
    }

    @Test
    @DisplayName("Unit Test: saveSchedule Past Date Throws IllegalArgumentException")
    public void testSaveSchedule_PastDate_ThrowsException() {
        DentistScheduleDTO inputDTO = new DentistScheduleDTO(1, LocalDate.now().minusDays(1), LocalTime.of(9, 0), LocalTime.of(17, 0), 30);
        assertThrows(IllegalArgumentException.class, () -> dentistService.saveSchedule(inputDTO));
    }

    @Test
    @DisplayName("Unit Test: saveSchedule Non-existent Dentist Throws Exception")
    public void testSaveSchedule_InvalidDentist_ThrowsException() {
        when(dentistRepository.findById(999)).thenReturn(Optional.empty());

        DentistScheduleDTO inputDTO = new DentistScheduleDTO(999, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), 30);
        assertThrows(com.dentalclinic.exception.ResourceNotFoundException.class, () -> dentistService.saveSchedule(inputDTO));
    }

    @Test
    @DisplayName("Unit Test: saveSchedule End Time Before Start Time Throws Exception")
    public void testSaveSchedule_EndTimeBeforeStartTime_ThrowsException() {
        DentistScheduleDTO inputDTO = new DentistScheduleDTO(1, LocalDate.now(), LocalTime.of(17, 0), LocalTime.of(9, 0), 30);
        assertThrows(IllegalArgumentException.class, () -> dentistService.saveSchedule(inputDTO));
    }

    @Test
    @DisplayName("Unit Test: deleteDentist Soft Deletes Doctor Profile")
    public void testDeleteDentist() {
        when(dentistRepository.findById(1)).thenReturn(Optional.of(sampleDentist));
        when(dentistRepository.save(any(Dentist.class))).thenAnswer(i -> i.getArgument(0));

        dentistService.deleteDentist(1);

        assertFalse(sampleDentist.getIsActive());
    }

    @Test
    @DisplayName("Unit Test: deleteDentist Invalid ID Throws ResourceNotFoundException")
    public void testDeleteDentist_InvalidId_ThrowsException() {
        when(dentistRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(com.dentalclinic.exception.ResourceNotFoundException.class, () -> dentistService.deleteDentist(999));
    }
}
