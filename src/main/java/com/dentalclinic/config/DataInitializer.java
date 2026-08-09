package com.dentalclinic.config;

import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.User;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Startup Data Initializer Runner with Debug Logging.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, 
                           PatientRepository patientRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("[DEBUG-INIT] Executing DataInitializer on Database Startup...");

        // Ensure Admin user exists with valid BCrypt hash for "admin123" AND "Admin@123"
        User admin = userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setFullName("System Administrator");
            u.setRole("Admin");
            u.setEmail("admin@sunrisedental.com");
            return u;
        });
        
        String adminHash = passwordEncoder.encode("admin123");
        admin.setPassword(adminHash);
        admin.setIsActive(true);
        userRepository.save(admin);
        System.out.println("[DEBUG-INIT] Reset 'admin' password to 'admin123'. Hash: " + adminHash);

        // Ensure Receptionist user exists with valid BCrypt hash for "recept123"
        User receptionist = userRepository.findByUsername("receptionist").orElseGet(() -> {
            User u = new User();
            u.setUsername("receptionist");
            u.setFullName("Sarah Jenkins (Receptionist)");
            u.setRole("Receptionist");
            u.setEmail("receptionist@sunrisedental.com");
            return u;
        });
        
        String receptHash = passwordEncoder.encode("recept123");
        receptionist.setPassword(receptHash);
        receptionist.setIsActive(true);
        userRepository.save(receptionist);
        System.out.println("[DEBUG-INIT] Reset 'receptionist' password to 'recept123'. Hash: " + receptHash);

        // Seed initial sample patient record if database has no patients
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
        
        System.out.println("[DEBUG-INIT] Total Users in DB: " + userRepository.count());
        System.out.println("=================================================");
    }
}
