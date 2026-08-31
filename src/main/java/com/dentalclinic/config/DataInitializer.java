package com.dentalclinic.config;

import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.DentistSchedule;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.entity.User;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.DentistScheduleRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import com.dentalclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Startup Data Initializer Runner with Sample Dentists, Treatments, Schedules, and Appointments.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final DentistScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, 
                           PatientRepository patientRepository,
                           DentistRepository dentistRepository,
                           TreatmentTypeRepository treatmentTypeRepository,
                           DentistScheduleRepository scheduleRepository,
                           AppointmentRepository appointmentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("[DEBUG-INIT] Executing DataInitializer on Database Startup...");

        // Ensure Admin user exists with valid BCrypt hash for "admin123" if missing
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFullName("System Administrator");
            admin.setRole("Admin");
            admin.setEmail("admin@sunrisedental.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("[DEBUG-INIT] Created default Admin user ('admin').");
        } else {
            System.out.println("[DEBUG-INIT] Admin user already exists. Preserving existing password/credentials.");
        }

        // Ensure Receptionist user exists with valid BCrypt hash for "recept123" if missing
        if (userRepository.findByUsername("receptionist").isEmpty()) {
            User receptionist = new User();
            receptionist.setUsername("receptionist");
            receptionist.setFullName("Nimali Perera (Receptionist)");
            receptionist.setRole("Receptionist");
            receptionist.setEmail("receptionist@sunrisedental.com");
            receptionist.setPassword(passwordEncoder.encode("recept123"));
            receptionist.setIsActive(true);
            userRepository.save(receptionist);
            System.out.println("[DEBUG-INIT] Created default Receptionist user ('receptionist').");
        } else {
            System.out.println("[DEBUG-INIT] Receptionist user already exists. Preserving existing password/credentials.");
        }

        // Seed Sample Dentists
        Dentist d1, d2, d3, d4;
        if (dentistRepository.count() < 4) {
            d1 = getOrCreateDentist("Dr. Sarah Chen", "General Dentistry & Orthodontics", "0771112233", new BigDecimal("1500.00"), "BDS (Colombo), M.Sc (London)");
            d2 = getOrCreateDentist("Dr. Nimal Perera", "Oral Surgery & Restorative", "0772223344", new BigDecimal("2000.00"), "BDS (Peradeniya), FDSRCS (UK)");
            d3 = getOrCreateDentist("Dr. Dilini Jayasinghe", "Pediatric & Cosmetic Dentistry", "0773334455", new BigDecimal("1800.00"), "BDS (Colombo), Dip. in Pedodontics");
            d4 = getOrCreateDentist("Dr. Kasun Bandara", "Endodontics & Implantology", "0774445566", new BigDecimal("2500.00"), "BDS (Peradeniya), MS (Implantology)");
        } else {
            java.util.List<Dentist> dentists = dentistRepository.findAll();
            d1 = dentists.get(0);
            d2 = dentists.get(1);
            d3 = dentists.size() > 2 ? dentists.get(2) : d1;
            d4 = dentists.size() > 3 ? dentists.get(3) : d2;
        }

        // Seed Sample Treatment Catalog
        TreatmentType tt1, tt2, tt3, tt4, tt5, tt6;
        if (treatmentTypeRepository.count() < 6) {
            tt1 = treatmentTypeRepository.save(new TreatmentType("General Consultation & Cleaning", "Comprehensive oral checkup & scaling", new BigDecimal("1500.00")));
            tt2 = treatmentTypeRepository.save(new TreatmentType("Tooth Extraction", "Surgical / Non-surgical extraction", new BigDecimal("2500.00")));
            tt3 = treatmentTypeRepository.save(new TreatmentType("Composite Filling", "Tooth-colored composite restoration", new BigDecimal("3000.00")));
            tt4 = treatmentTypeRepository.save(new TreatmentType("Root Canal Therapy", "Endodontic therapy per canal", new BigDecimal("8000.00")));
            tt5 = treatmentTypeRepository.save(new TreatmentType("Dental Crown & Bridge", "Ceramic/Porcelain crown placement", new BigDecimal("15000.00")));
            tt6 = treatmentTypeRepository.save(new TreatmentType("Teeth Whitening & Polishing", "Laser enamel whitening and stain removal", new BigDecimal("6000.00")));
        } else {
            java.util.List<TreatmentType> ttList = treatmentTypeRepository.findAll();
            tt1 = ttList.get(0);
            tt2 = ttList.size() > 1 ? ttList.get(1) : tt1;
            tt3 = ttList.size() > 2 ? ttList.get(2) : tt1;
            tt4 = ttList.size() > 3 ? ttList.get(3) : tt1;
            tt5 = ttList.size() > 4 ? ttList.get(4) : tt1;
            tt6 = ttList.size() > 5 ? ttList.get(5) : tt1;
        }

        // Seed Sample Patients (10 Patients registered across past dates)
        LocalDate today = LocalDate.now();
        Patient p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;
        if (patientRepository.count() < 10) {
            p1 = createPatientIfMissing("John Doe", "0771234567", "johndoe@gmail.com", "123 Galle Road, Colombo 03", "199012345678", LocalDate.of(1990, 5, 15), "M", today.minusDays(30));
            p2 = createPatientIfMissing("Anusha Silva", "0779876543", "anusha.silva@gmail.com", "45 Kandy Road, Kiribathgoda", "198856789012", LocalDate.of(1988, 8, 22), "F", today.minusDays(25));
            p3 = createPatientIfMissing("Kamal Fernando", "0712345678", "kamal.f@gmail.com", "88 Main Street, Negombo", "199523456789", LocalDate.of(1995, 3, 10), "M", today.minusDays(20));
            p4 = createPatientIfMissing("Ruwan Jayawardena", "0773456789", "ruwan.j@gmail.com", "12 School Lane, Nugegoda", "199234567890", LocalDate.of(1992, 11, 4), "M", today.minusDays(15));
            p5 = createPatientIfMissing("Priyani Gunasekara", "0784567890", "priyani.g@yahoo.com", "77 Temple Road, Maharagama", "198545678901", LocalDate.of(1985, 7, 18), "F", today.minusDays(12));
            p6 = createPatientIfMissing("Sahan De Silva", "0765678901", "sahan.ds@gmail.com", "20 Beach Road, Mount Lavinia", "199856789012", LocalDate.of(1998, 2, 28), "M", today.minusDays(10));
            p7 = createPatientIfMissing("Nimmi Fonseka", "0776789012", "nimmi.f@outlook.com", "55 Station Road, Dehiwala", "199367890123", LocalDate.of(1993, 9, 14), "F", today.minusDays(7));
            p8 = createPatientIfMissing("Mahesh Rathnayake", "0717890123", "mahesh.r@gmail.com", "99 Highlevel Road, Pannipitiya", "198978901234", LocalDate.of(1989, 1, 5), "M", today.minusDays(5));
            p9 = createPatientIfMissing("Thilini Wickramasinghe", "0758901234", "thilini.w@gmail.com", "14 Park Street, Colombo 02", "199689012345", LocalDate.of(1996, 6, 21), "F", today.minusDays(2));
            p10 = createPatientIfMissing("Dinesh Cooray", "0779012345", "dinesh.c@gmail.com", "30 Baseline Road, Dematagoda", "199190123456", LocalDate.of(1991, 12, 30), "M", today.minusDays(1));
        } else {
            java.util.List<Patient> pList = patientRepository.findAll();
            p1 = pList.get(0); p2 = pList.get(1); p3 = pList.get(2);
            p4 = pList.size() > 3 ? pList.get(3) : p1;
            p5 = pList.size() > 4 ? pList.get(4) : p2;
            p6 = pList.size() > 5 ? pList.get(5) : p3;
            p7 = pList.size() > 6 ? pList.get(6) : p1;
            p8 = pList.size() > 7 ? pList.get(7) : p2;
            p9 = pList.size() > 8 ? pList.get(8) : p3;
            p10 = pList.size() > 9 ? pList.get(9) : p1;
        }

        // Seed Doctor Shift Schedules across multiple dates (Past 7 days, Today, and Tomorrow)
        LocalDate dayMinus7 = today.minusDays(7);
        LocalDate dayMinus3 = today.minusDays(3);
        LocalDate dayMinus1 = today.minusDays(1);
        LocalDate dayPlus1 = today.plusDays(1);

        DentistSchedule sPast1 = getOrCreateSchedule(d1, dayMinus7, LocalTime.of(9, 0), LocalTime.of(13, 0));
        DentistSchedule sPast2 = getOrCreateSchedule(d2, dayMinus7, LocalTime.of(14, 0), LocalTime.of(18, 0));
        DentistSchedule sPast3 = getOrCreateSchedule(d3, dayMinus3, LocalTime.of(9, 0), LocalTime.of(13, 0));
        DentistSchedule sPast4 = getOrCreateSchedule(d4, dayMinus1, LocalTime.of(10, 0), LocalTime.of(14, 0));

        DentistSchedule sToday1 = getOrCreateSchedule(d1, today, LocalTime.of(9, 0), LocalTime.of(13, 0));
        DentistSchedule sToday2 = getOrCreateSchedule(d2, today, LocalTime.of(9, 0), LocalTime.of(13, 0));
        DentistSchedule sToday3 = getOrCreateSchedule(d3, today, LocalTime.of(14, 0), LocalTime.of(18, 0));
        DentistSchedule sToday4 = getOrCreateSchedule(d4, today, LocalTime.of(14, 0), LocalTime.of(18, 0));

        DentistSchedule sNext1 = getOrCreateSchedule(d1, dayPlus1, LocalTime.of(9, 0), LocalTime.of(13, 0));
        DentistSchedule sNext2 = getOrCreateSchedule(d2, dayPlus1, LocalTime.of(14, 0), LocalTime.of(18, 0));

        // Seed Sample Appointments (18 Appointments across past, current, and future dates)
        if (appointmentRepository.count() < 12) {
            // Past Completed Appointments (Financial & Performance Analytics)
            createAppointmentIfMissing(p1, d1, tt1, sPast1, 1, dayMinus7, LocalTime.of(9, 0), LocalTime.of(9, 30), "COMPLETED", "Initial scaling and hygiene check completed.");
            createAppointmentIfMissing(p2, d2, tt4, sPast2, 1, dayMinus7, LocalTime.of(14, 0), LocalTime.of(14, 30), "COMPLETED", "Root canal stage 1 initiated.");
            createAppointmentIfMissing(p3, d3, tt3, sPast3, 1, dayMinus3, LocalTime.of(9, 0), LocalTime.of(9, 30), "COMPLETED", "Composite filling applied to upper molar.");
            createAppointmentIfMissing(p4, d4, tt5, sPast4, 1, dayMinus1, LocalTime.of(10, 0), LocalTime.of(10, 30), "COMPLETED", "Porcelain crown preparation completed.");
            createAppointmentIfMissing(p5, d1, tt2, sPast1, 2, dayMinus7, LocalTime.of(9, 30), LocalTime.of(10, 0), "COMPLETED", "Lower premolar non-surgical extraction.");
            createAppointmentIfMissing(p6, d3, tt6, sPast3, 2, dayMinus3, LocalTime.of(9, 30), LocalTime.of(10, 0), "COMPLETED", "Enamel whitening treatment finished.");

            // Today's Appointments
            createAppointmentIfMissing(p1, d1, tt1, sToday1, 1, today, LocalTime.of(9, 0), LocalTime.of(9, 30), "COMPLETED", "Follow-up hygiene checkup & polishing.");
            createAppointmentIfMissing(p7, d2, tt4, sToday2, 1, today, LocalTime.of(9, 0), LocalTime.of(9, 30), "COMPLETED", "Root canal stage 2 filing & obturation.");
            createAppointmentIfMissing(p8, d1, tt3, sToday1, 2, today, LocalTime.of(9, 30), LocalTime.of(10, 0), "CONFIRMED", "Restoration of premolar cavity.");
            createAppointmentIfMissing(p9, d2, tt2, sToday2, 2, today, LocalTime.of(9, 30), LocalTime.of(10, 0), "CONFIRMED", "Extraction of wisdom tooth.");
            createAppointmentIfMissing(p10, d3, tt1, sToday3, 1, today, LocalTime.of(14, 0), LocalTime.of(14, 30), "BOOKED", "Routine consultation booked.");
            createAppointmentIfMissing(p3, d4, tt5, sToday4, 1, today, LocalTime.of(14, 0), LocalTime.of(14, 30), "BOOKED", "Crown fitting session.");

            // Tomorrow / Future Appointments
            createAppointmentIfMissing(p4, d1, tt6, sNext1, 1, dayPlus1, LocalTime.of(9, 0), LocalTime.of(9, 30), "CONFIRMED", "Laser whitening session scheduled.");
            createAppointmentIfMissing(p5, d2, tt3, sNext2, 1, dayPlus1, LocalTime.of(14, 0), LocalTime.of(14, 30), "BOOKED", "Tooth restoration appointment.");
            createAppointmentIfMissing(p6, d1, tt1, sNext1, 2, dayPlus1, LocalTime.of(9, 30), LocalTime.of(10, 0), "BOOKED", "Routine checkup booked online.");
            createAppointmentIfMissing(p2, d2, tt4, sNext2, 2, dayPlus1, LocalTime.of(14, 30), LocalTime.of(15, 0), "CONFIRMED", "Final root canal sealing.");
        }
        
        System.out.println("[DEBUG-INIT] Dentists Seeded: " + dentistRepository.count());
        System.out.println("[DEBUG-INIT] Treatments Seeded: " + treatmentTypeRepository.count());
        System.out.println("[DEBUG-INIT] Patients Seeded: " + patientRepository.count());
        System.out.println("[DEBUG-INIT] Schedules Seeded: " + scheduleRepository.count());
        System.out.println("[DEBUG-INIT] Appointments Seeded: " + appointmentRepository.count());
        System.out.println("[DEBUG-INIT] Total Users in DB: " + userRepository.count());
        System.out.println("=================================================");
    }

    private Dentist getOrCreateDentist(String name, String spec, String phone, BigDecimal fee, String qual) {
        return dentistRepository.findAll().stream()
                .filter(d -> d.getDentistName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    Dentist d = new Dentist(name, spec, phone, fee);
                    d.setQualifications(qual);
                    return dentistRepository.save(d);
                });
    }

    private Patient createPatientIfMissing(String name, String phone, String email, String address, String nic, LocalDate dob, String gender, LocalDate regDate) {
        return patientRepository.findByNic(nic).orElseGet(() -> {
            Patient p = new Patient(name, phone, email, address, nic);
            p.setDateOfBirth(dob);
            p.setGender(gender);
            p.setRegisteredDate(regDate.atStartOfDay());
            return patientRepository.save(p);
        });
    }

    private DentistSchedule getOrCreateSchedule(Dentist dentist, LocalDate date, LocalTime start, LocalTime end) {
        return scheduleRepository.findByDentist_DentistIdAndScheduleDateAndIsActiveTrue(dentist.getDentistId(), date)
                .stream().findFirst()
                .orElseGet(() -> scheduleRepository.save(new DentistSchedule(dentist, date, start, end, 30, 10)));
    }

    private void createAppointmentIfMissing(Patient patient, Dentist dentist, TreatmentType treatment, DentistSchedule schedule, int token, LocalDate date, LocalTime start, LocalTime end, String status, String notes) {
        java.util.List<Appointment> existing = appointmentRepository.findByDentist_DentistIdAndAppointmentDateAndStatusNot(dentist.getDentistId(), date, "CANCELLED");
        boolean exists = existing.stream().anyMatch(a -> a.getPatient().getPatientId().equals(patient.getPatientId()) && a.getStartTime().equals(start));
        if (!exists) {
            Appointment apt = new Appointment(patient, dentist, treatment, schedule, null, token, date, start, end, status);
            apt.setNotes(notes);
            appointmentRepository.save(apt);
        }
    }
}

