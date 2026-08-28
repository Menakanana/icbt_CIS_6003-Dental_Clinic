package com.dentalclinic;

import com.dentalclinic.config.DataInitializer;
import com.dentalclinic.entity.*;
import com.dentalclinic.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private TreatmentTypeRepository treatmentTypeRepository;

    @Mock
    private DentistScheduleRepository scheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    public void setUp() {
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$dummyBCryptHash");
        when(dentistRepository.count()).thenReturn(4L);
        when(treatmentTypeRepository.count()).thenReturn(6L);
        when(patientRepository.count()).thenReturn(10L);
        when(appointmentRepository.count()).thenReturn(12L);

        Dentist d1 = new Dentist("Dr. Sarah Chen", "General", "0771112233", new BigDecimal("1500.00"));
        Dentist d2 = new Dentist("Dr. Nimal Perera", "Oral", "0772223344", new BigDecimal("2000.00"));
        d1.setDentistId(1);
        d2.setDentistId(2);
        when(dentistRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        TreatmentType tt1 = new TreatmentType("General Consultation & Cleaning", "Checkup", new BigDecimal("1500.00"));
        TreatmentType tt2 = new TreatmentType("Tooth Extraction", "Extraction", new BigDecimal("2500.00"));
        tt1.setTreatmentTypeId(1);
        tt2.setTreatmentTypeId(2);
        when(treatmentTypeRepository.findAll()).thenReturn(Arrays.asList(tt1, tt2));

        Patient p1 = new Patient("John Doe", "0771234567", "john@gmail.com", "Address", "199012345678");
        Patient p2 = new Patient("Anusha Silva", "0779876543", "anusha@gmail.com", "Address", "198856789012");
        Patient p3 = new Patient("Kamal Fernando", "0712345678", "kamal@gmail.com", "Address", "199523456789");
        p1.setPatientId(1);
        p2.setPatientId(2);
        p3.setPatientId(3);
        when(patientRepository.findAll()).thenReturn(Arrays.asList(p1, p2, p3));

        DentistSchedule dummySchedule = new DentistSchedule(d1, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(13, 0), 30, 10);
        lenient().when(scheduleRepository.findByDentist_DentistIdAndScheduleDateAndIsActiveTrue(any(), any()))
                .thenReturn(Collections.singletonList(dummySchedule));
    }

    @Test
    @DisplayName("DataInitializer creates admin and receptionist if missing in database")
    public void testRun_SeedsMissingUsers() throws Exception {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("receptionist")).thenReturn(Optional.empty());

        dataInitializer.run();

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("DataInitializer preserves existing user credentials and does NOT overwrite reset passwords")
    public void testRun_PreservesExistingUserPasswords() throws Exception {
        User existingAdmin = new User("admin", "$2a$12$userResetPasswordHash", "System Admin", "Admin", "admin@sunrisedental.com");
        User existingReceptionist = new User("receptionist", "$2a$12$receptResetPasswordHash", "Nimali", "Receptionist", "recept@sunrisedental.com");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(existingAdmin));
        when(userRepository.findByUsername("receptionist")).thenReturn(Optional.of(existingReceptionist));

        dataInitializer.run();

        // Verify save was NOT called for users, preserving their reset password hashes
        verify(userRepository, never()).save(any(User.class));
        assertEquals("$2a$12$userResetPasswordHash", existingAdmin.getPassword());
        assertEquals("$2a$12$receptResetPasswordHash", existingReceptionist.getPassword());
    }
}
