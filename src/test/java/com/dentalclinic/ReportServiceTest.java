package com.dentalclinic;

import com.dentalclinic.dto.AdminReportDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import com.dentalclinic.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private TreatmentTypeRepository treatmentTypeRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private ReportService reportService;

    private Dentist dentist1;
    private Dentist dentist2;
    private Patient patient1;
    private Patient patient2;
    private TreatmentType treatment1;
    private TreatmentType treatment2;
    private Appointment appt1;
    private Appointment appt2;

    @BeforeEach
    public void setUp() {
        dentist1 = new Dentist("Dr. Nimal Fernando", "Orthodontics", "0711111111", new BigDecimal("2500.00"));
        dentist1.setDentistId(1);

        dentist2 = new Dentist("Dr. Sunethra Perera", "Pediatric Dentistry", "0722222222", new BigDecimal("3000.00"));
        dentist2.setDentistId(2);

        patient1 = new Patient("Kavindi Silva", "0771234567", "kavindi@example.com", "Colombo", "199512345678");
        patient1.setPatientId(101);
        patient1.setRegisteredDate(LocalDateTime.now().minusDays(10));
        patient1.setDateOfBirth(LocalDate.now().minusYears(28));
        patient1.setGender("Female");

        patient2 = new Patient("Ruwan Perera", "0788888888", "ruwan@example.com", "Kandy", "198088888888");
        patient2.setPatientId(102);
        patient2.setRegisteredDate(LocalDateTime.now().minusDays(5));
        patient2.setDateOfBirth(LocalDate.now().minusYears(45));
        patient2.setGender("Male");

        treatment1 = new TreatmentType("Tooth Extraction", "Surgical tooth removal", new BigDecimal("4500.00"));
        treatment1.setTreatmentTypeId(10);

        treatment2 = new TreatmentType("Teeth Whitening", "Laser cosmetic whitening", new BigDecimal("12000.00"));
        treatment2.setTreatmentTypeId(11);

        appt1 = new Appointment(patient1, dentist1, treatment1, null, null, 1, LocalDate.now(), LocalTime.of(9, 30), LocalTime.of(10, 0), "COMPLETED");
        appt1.setAppointmentId(1);
        appt1.setPaidAmount(new BigDecimal("7500.00"));
        appt1.setPaymentMethod("Cash");
        appt1.setPaymentStatus("FULL_PAID");

        appt2 = new Appointment(patient2, dentist2, treatment2, null, null, 2, LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(14, 30), "BOOKED");
        appt2.setAppointmentId(2);
        appt2.setPaidAmount(BigDecimal.ZERO);
        appt2.setPaymentMethod("Credit/Debit Card");
        appt2.setPaymentStatus("UNPAID");
    }

    @Test
    @DisplayName("Unit Test: generateReport Doctor Demand Suite Returns Aggregated Metrics")
    public void testGenerateDoctorDemandReport() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2));
        when(dentistRepository.findAll()).thenReturn(Arrays.asList(dentist1, dentist2));

        AdminReportDTO report = reportService.generateReport("DOCTOR_DEMAND", LocalDate.now().minusDays(7), LocalDate.now());

        assertNotNull(report);
        assertEquals("DOCTOR_DEMAND", report.getReportType());
        assertEquals(4, report.getKpiCards().size());
        assertEquals(9, report.getTableHeaders().size());
        assertEquals(2, report.getTableRows().size());
    }

    @Test
    @DisplayName("Unit Test: generateReport Procedure Usage Suite Aggregates Treatment Volume & Revenue")
    public void testGenerateProcedureUsageReport() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2));
        when(treatmentTypeRepository.findAll()).thenReturn(Arrays.asList(treatment1, treatment2));

        AdminReportDTO report = reportService.generateReport("PROCEDURE_USAGE", LocalDate.now().minusDays(7), LocalDate.now());

        assertNotNull(report);
        assertEquals("PROCEDURE_USAGE", report.getReportType());
        assertEquals(4, report.getKpiCards().size());
        assertEquals(7, report.getTableHeaders().size());
        assertEquals(2, report.getTableRows().size());
    }

    @Test
    @DisplayName("Unit Test: generateReport Financial Summary Breakdown by Payment Method")
    public void testGenerateFinancialSummaryReport() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2));

        AdminReportDTO report = reportService.generateReport("FINANCIAL_SUMMARY", LocalDate.now().minusDays(7), LocalDate.now());

        assertNotNull(report);
        assertEquals("FINANCIAL_SUMMARY", report.getReportType());
        assertFalse(report.getTableRows().isEmpty());
    }

    @Test
    @DisplayName("Unit Test: generateReport Patient Demographics & Age Distribution")
    public void testGeneratePatientDemographicsReport() {
        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient1, patient2));
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2));

        AdminReportDTO report = reportService.generateReport("PATIENT_DEMOGRAPHICS", LocalDate.now().minusDays(30), LocalDate.now());

        assertNotNull(report);
        assertEquals("PATIENT_DEMOGRAPHICS", report.getReportType());
        assertEquals(2, report.getTableRows().size());
    }

    @Test
    @DisplayName("Unit Test: generateReport Cancellation & Reschedule Audit")
    public void testGenerateCancellationAuditReport() {
        Appointment cancelledAppt = new Appointment(patient1, dentist1, treatment1, null, null, 3, LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(11, 30), "CANCELLED");
        cancelledAppt.setAppointmentId(3);
        cancelledAppt.setNotes("Patient requested cancellation due to travel");

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, cancelledAppt));

        AdminReportDTO report = reportService.generateReport("CANCELLATION_AUDIT", LocalDate.now().minusDays(7), LocalDate.now());

        assertNotNull(report);
        assertEquals("CANCELLATION_AUDIT", report.getReportType());
        assertEquals(3, report.getTableRows().size());
    }

    @Test
    @DisplayName("Unit Test: generateReport Session Utilization Breakdown")
    public void testGenerateSessionUtilizationReport() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2));

        AdminReportDTO report = reportService.generateReport("SESSION_UTILIZATION", LocalDate.now().minusDays(7), LocalDate.now());

        assertNotNull(report);
        assertEquals("SESSION_UTILIZATION", report.getReportType());
        assertEquals(3, report.getTableRows().size()); // Morning, Afternoon, Evening
    }

    @Test
    @DisplayName("Unit Test: generateReport Outstanding Dues Roster")
    public void testGenerateOutstandingDuesReport() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2));

        AdminReportDTO report = reportService.generateReport("OUTSTANDING_DUES", LocalDate.now().minusDays(7), LocalDate.now());

        assertNotNull(report);
        assertEquals("OUTSTANDING_DUES", report.getReportType());
        assertEquals(1, report.getTableRows().size()); // appt2 is UNPAID with due balance
    }
}
