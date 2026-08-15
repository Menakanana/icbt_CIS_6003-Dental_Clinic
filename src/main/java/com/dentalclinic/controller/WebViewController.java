package com.dentalclinic.controller;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.dto.PatientDTO;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.AuthService;
import com.dentalclinic.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Web View Controller for handling HTML/JSP page navigation.
 * 
 * Layer: Presentation Layer (Spring MVC)
 * Purpose: Renders login page and dashboard view templates (/WEB-INF/views/*.jsp).
 */
@Controller
public class WebViewController {

    private final AuthService authService;
    private final PatientService patientService;
    private final DentistRepository dentistRepository;
    private final AppointmentService appointmentService;

    @Autowired
    public WebViewController(AuthService authService,
                              PatientService patientService,
                              DentistRepository dentistRepository,
                              AppointmentService appointmentService) {
        this.authService = authService;
        this.patientService = patientService;
        this.dentistRepository = dentistRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequestDTO loginRequest, Model model) {
        try {
            AuthResponseDTO authResponse = authService.authenticate(loginRequest);
            model.addAttribute("user", authResponse);
            
            // Populate dashboard attributes
            populateDashboardModel(model);
            
            return "dashboard"; // Renders /WEB-INF/views/dashboard.jsp
        } catch (BadCredentialsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "login"; // Re-renders login.jsp with error alert
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        populateDashboardModel(model);
        return "dashboard";
    }

    @PostMapping("/booking/create")
    public String handleWebBooking(@ModelAttribute("bookingRequest") BookingRequestDTO bookingRequest, Model model) {
        try {
            appointmentService.bookAppointment(bookingRequest);
            model.addAttribute("successMessage", "Appointment booked successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        populateDashboardModel(model);
        return "dashboard";
    }

    @PostMapping("/patients/register")
    public String handleWebPatientRegistration(@ModelAttribute("patientDTO") PatientDTO patientDTO, Model model) {
        try {
            patientService.registerPatient(patientDTO);
            model.addAttribute("successMessage", "Patient registered successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        populateDashboardModel(model);
        return "dashboard";
    }

    private void populateDashboardModel(Model model) {
        model.addAttribute("patients", patientService.getAllActivePatients());
        model.addAttribute("dentists", dentistRepository.findByIsActiveTrue());
        model.addAttribute("todayAppointments", appointmentService.getTodayAppointments());
        model.addAttribute("patientDTO", new PatientDTO());
        model.addAttribute("bookingRequest", new BookingRequestDTO());
    }
}
