package com.dentalclinic.config;

import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.entity.User;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import com.dentalclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Startup Data Initializer Runner with Sample Dentists & Treatments.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, 
                           PatientRepository patientRepository,
                           DentistRepository dentistRepository,
                           TreatmentTypeRepository treatmentTypeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("[DEBUG-INIT] Executing DataInitializer on Database Startup...");

        // Ensure Admin user exists with valid BCrypt hash for "admin123"
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setFullName("System Administrator");
            u.setRole("Admin");
            u.setEmail("admin@sunrisedental.com");
            return u;
        });
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setIsActive(true);
        userRepository.save(admin);

        // Ensure Receptionist user exists with valid BCrypt hash for "recept123"
        User receptionist = userRepository.findByUsername("receptionist").orElseGet(() -> {
            User u = new User();
            u.setUsername("receptionist");
            u.setFullName("Nimali Perera (Receptionist)");
            u.setRole("Receptionist");
            u.setEmail("receptionist@sunrisedental.com");
            return u;
        });
        receptionist.setPassword(passwordEncoder.encode("recept123"));
        receptionist.setIsActive(true);
        userRepository.save(receptionist);

        // Seed Sample Dentists if empty
        if (dentistRepository.count() == 0) {
            Dentist d1 = new Dentist("Dr. Sarah Chen", "General Dentistry & Orthodontics", "0771112233", new BigDecimal("1500.00"));
            d1.setQualifications("BDS (Colombo), M.Sc (London)");
            dentistRepository.save(d1);

            Dentist d2 = new Dentist("Dr. Nimal Perera", "Oral Surgery & Restorative", "0772223344", new BigDecimal("2000.00"));
            d2.setQualifications("BDS (Peradeniya), FDSRCS (UK)");
            dentistRepository.save(d2);
        }

        // Seed Sample Treatment Catalog if empty
        if (treatmentTypeRepository.count() == 0) {
            treatmentTypeRepository.save(new TreatmentType("General Consultation & Cleaning", "Comprehensive oral checkup & scaling", new BigDecimal("1500.00")));
            treatmentTypeRepository.save(new TreatmentType("Tooth Extraction", "Surgical / Non-surgical extraction", new BigDecimal("2500.00")));
            treatmentTypeRepository.save(new TreatmentType("Composite Filling", "Tooth-colored composite restoration", new BigDecimal("3000.00")));
            treatmentTypeRepository.save(new TreatmentType("Root Canal Therapy", "Endodontic therapy per canal", new BigDecimal("8000.00")));
        }

        // Seed Sample Patient if empty
        if (patientRepository.count() == 0) {
            Patient samplePatient = new Patient(
                    "John Doe",
                    "0771234567",
                    "johndoe@gmail.com",
                    "123 Galle Road, Colombo 03",
                    "199012345678"
            );
            samplePatient.setDateOfBirth(LocalDate.of(1990, 5, 15));
            samplePatient.setGender("M");
            patientRepository.save(samplePatient);
        }
        
        System.out.println("[DEBUG-INIT] Dentists Seeded: " + dentistRepository.count());
        System.out.println("[DEBUG-INIT] Treatments Seeded: " + treatmentTypeRepository.count());
        System.out.println("[DEBUG-INIT] Total Users in DB: " + userRepository.count());
        System.out.println("=================================================");
    }
}
