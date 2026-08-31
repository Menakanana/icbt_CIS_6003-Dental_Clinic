package com.dentalclinic.controller;

import com.dentalclinic.dto.AppointmentTicketDTO;
import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.BookingRequestDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.dto.PatientDTO;
import com.dentalclinic.dto.BillingDTO;
import com.dentalclinic.dto.DentistDTO;
import com.dentalclinic.dto.DentistScheduleDTO;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.service.AppointmentService;
import com.dentalclinic.service.AuthService;
import com.dentalclinic.service.BillingService;
import com.dentalclinic.service.ClinicSettingService;
import com.dentalclinic.service.DentistService;
import com.dentalclinic.service.PatientService;
import com.dentalclinic.service.TreatmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Web View Controller for handling HTML/JSP page navigation.
 * 
 * Layer: Presentation Layer (Spring MVC)
 * Purpose: Renders login page, dashboard, and billing receipt templates (/WEB-INF/views/*.jsp).
 */
@Controller
public class WebViewController {

    private final AuthService authService;
    private final PatientService patientService;
    private final DentistRepository dentistRepository;
    private final com.dentalclinic.repository.UserRepository userRepository;
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final DentistService dentistService;
    private final ClinicSettingService clinicSettingService;
    private final TreatmentTypeService treatmentTypeService;

    @Autowired
    public WebViewController(AuthService authService,
                              PatientService patientService,
                              DentistRepository dentistRepository,
                              com.dentalclinic.repository.UserRepository userRepository,
                              AppointmentService appointmentService,
                              BillingService billingService,
                              DentistService dentistService,
                              @Autowired(required = false) ClinicSettingService clinicSettingService,
                              @Autowired(required = false) TreatmentTypeService treatmentTypeService) {
        this.authService = authService;
        this.patientService = patientService;
        this.dentistRepository = dentistRepository;
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.billingService = billingService;
        this.dentistService = dentistService;
        this.clinicSettingService = clinicSettingService;
        this.treatmentTypeService = treatmentTypeService;
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

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam(name = "token", required = false) String token, Model model) {
        boolean isValid = authService.validatePasswordResetToken(token);
        model.addAttribute("token", token);
        model.addAttribute("isValidToken", isValid);
        if (!isValid) {
            model.addAttribute("errorMessage", "The password reset token is invalid, already used, or expired. Please request a new link.");
        }
        return "reset-password"; // Renders /WEB-INF/views/reset-password.jsp
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequestDTO loginRequest, jakarta.servlet.http.HttpSession session, Model model) {
        try {
            AuthResponseDTO authResponse = authService.authenticate(loginRequest);
            if (session != null) {
                session.setAttribute("user", authResponse);
            }
            model.addAttribute("user", authResponse);
            
            // Populate dashboard attributes
            populateDashboardModel(session, model);
            
            return "dashboard"; // Renders /WEB-INF/views/dashboard.jsp
        } catch (BadCredentialsException ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
            return "login"; // Re-renders login.jsp with error alert
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(jakarta.servlet.http.HttpSession session, Model model) {
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @PostMapping("/booking/create")
    public String handleWebBooking(@ModelAttribute("bookingRequest") BookingRequestDTO bookingRequest, jakarta.servlet.http.HttpSession session, Model model) {
        try {
            com.dentalclinic.dto.AppointmentTicketDTO ticket = appointmentService.bookAppointment(bookingRequest);
            return "redirect:/booking/ticket/" + ticket.getAppointmentId();
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
            populateDashboardModel(session, model);
            return "dashboard";
        }
    }

    @PostMapping("/patients/register")
    public String handleWebPatientRegistration(@ModelAttribute("patientDTO") PatientDTO patientDTO, jakarta.servlet.http.HttpSession session, Model model) {
        try {
            patientService.registerPatient(patientDTO);
            model.addAttribute("successMessage", "Patient registered successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @GetMapping("/booking/ticket/{appointmentId}")
    public String showAppointmentTicket(@PathVariable("appointmentId") Integer appointmentId, Model model) {
        AppointmentTicketDTO ticket = appointmentService.getAppointmentById(appointmentId);
        model.addAttribute("ticket", ticket);
        return "ticket"; // Renders /WEB-INF/views/ticket.jsp
    }

    @org.springframework.web.bind.annotation.RequestMapping(value = "/billing/receipt/{appointmentId}", method = {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.POST})
    public String showBillingReceipt(@PathVariable("appointmentId") Integer appointmentId,
                                     @RequestParam(name = "discount", required = false, defaultValue = "0") BigDecimal discount,
                                     @RequestParam(name = "stage", required = false, defaultValue = "FINAL_SETTLED") String stage,
                                     @RequestParam(name = "treatmentTypeId", required = false) Integer treatmentTypeId,
                                     @RequestParam(name = "treatmentTypeIds", required = false) java.util.List<Integer> treatmentTypeIds,
                                     @RequestParam(name = "paymentMethod", required = false, defaultValue = "Cash") String paymentMethod,
                                     @RequestParam(name = "settle", required = false, defaultValue = "false") boolean settle,
                                     Model model) {
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        if (treatmentTypeIds != null && !treatmentTypeIds.isEmpty()) {
            ids.addAll(treatmentTypeIds);
        } else if (treatmentTypeId != null) {
            ids.add(treatmentTypeId);
        }
        BillingDTO bill = billingService.calculateMultiProcedureBill(appointmentId, ids, discount, stage, paymentMethod, settle);
        model.addAttribute("bill", bill);
        return "receipt"; // Renders /WEB-INF/views/receipt.jsp
    }

    @PostMapping("/dentists/save")
    public String handleWebSaveDentist(@ModelAttribute("dentistDTO") DentistDTO dentistDTO, jakarta.servlet.http.HttpSession session, Model model) {
        try {
            dentistService.saveDentist(dentistDTO);
            model.addAttribute("successMessage", "Doctor profile saved successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @PostMapping("/dentists/schedule/save")
    public String handleWebSaveSchedule(@ModelAttribute("scheduleDTO") DentistScheduleDTO scheduleDTO, jakarta.servlet.http.HttpSession session, Model model) {
        try {
            dentistService.saveSchedule(scheduleDTO);
            model.addAttribute("successMessage", "Doctor availability schedule saved successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @PostMapping("/dentists/schedule/off-duty")
    public String handleMarkDoctorOffDuty(@RequestParam("dentistId") Integer dentistId,
                                          @RequestParam("scheduleDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate scheduleDate,
                                          jakarta.servlet.http.HttpSession session,
                                          Model model) {
        try {
            dentistService.markDoctorOffDuty(dentistId, scheduleDate);
            model.addAttribute("successMessage", "Doctor marked OFF DUTY for " + scheduleDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " and removed from operational roster.");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @PostMapping("/settings/clinic/save")
    public String handleSaveClinicProfile(@RequestParam("clinicName") String clinicName,
                                          @RequestParam("clinicAddress") String clinicAddress,
                                          @RequestParam("clinicPhone") String clinicPhone,
                                          @RequestParam("clinicCharge") BigDecimal clinicCharge,
                                          jakarta.servlet.http.HttpSession session,
                                          Model model) {
        try {
            if (clinicSettingService != null) {
                clinicSettingService.saveClinicProfile(clinicName, clinicAddress, clinicPhone, clinicCharge);
                model.addAttribute("successMessage", "Clinic Profile & Facility Charge updated successfully!");
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @PostMapping("/settings/treatment/save")
    public String handleSaveTreatmentType(@ModelAttribute com.dentalclinic.dto.TreatmentTypeDTO treatmentDTO, jakarta.servlet.http.HttpSession session, Model model) {
        try {
            if (treatmentTypeService != null) {
                treatmentTypeService.saveTreatmentType(treatmentDTO);
                model.addAttribute("successMessage", "Treatment procedure & tariff price saved successfully!");
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    @PostMapping("/admin/users/save")
    public String handleSaveStaffUser(@RequestParam("username") String username,
                                      @RequestParam(name = "password", required = false) String password,
                                      @RequestParam("fullName") String fullName,
                                      @RequestParam(name = "email") String email,
                                      @RequestParam(name = "role", defaultValue = "Receptionist") String role,
                                      jakarta.servlet.http.HttpSession session,
                                      Model model) {
        try {
            authService.registerStaffUser(username, password, fullName, email, role);
            model.addAttribute("successMessage", "Staff account '" + username + "' (" + role + ") registered successfully! An activation email with password setup instructions has been sent to " + email + ".");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", sanitizeErrorMessage(ex));
        }
        populateDashboardModel(session, model);
        return "dashboard";
    }

    private String sanitizeErrorMessage(Throwable ex) {
        if (ex == null) return "An unexpected error occurred.";
        
        String msg = ex.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            return "An unexpected error occurred. Please try again.";
        }
        
        String lower = msg.toLowerCase();
        
        if (lower.contains("sql") || lower.contains("hibernate") || lower.contains("could not execute statement") ||
            lower.contains("insert into") || lower.contains("update ") || lower.contains("delete from") ||
            lower.contains("column") || lower.contains("table") || lower.contains("truncat")) {
            
            if (lower.contains("duplicate") || lower.contains("unique") || lower.contains("primary key")) {
                return "A patient or record with the same unique information (e.g. NIC or Username) already exists in the system.";
            }
            if (lower.contains("truncat") || lower.contains("binary data")) {
                return "One or more input values exceed the allowed character length limit. Please check your entries and try again.";
            }
            if (lower.contains("foreign key") || lower.contains("fk_")) {
                return "Unable to complete operation because related records exist or the selected item is invalid.";
            }
            return "Database operation failed. Please check your entries and try again.";
        }
        
        return msg;
    }

    private void populateDashboardModel(Model model) {
        populateDashboardModel(null, model);
    }

    private void populateDashboardModel(jakarta.servlet.http.HttpSession session, Model model) {
        AuthResponseDTO user = session != null ? (AuthResponseDTO) session.getAttribute("user") : null;
        if (user == null) {
            user = new AuthResponseDTO("session-token", 1, "admin", "System Administrator", "Admin");
            if (session != null) {
                session.setAttribute("user", user);
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("patients", patientService.getAllActivePatients());
        model.addAttribute("dentists", dentistRepository.findByIsActiveTrue());
        model.addAttribute("schedules", dentistService.getAllActiveSchedules());
        model.addAttribute("todayAppointments", appointmentService.getTodayAppointments());
        model.addAttribute("treatmentTypes", treatmentTypeService != null ? treatmentTypeService.getAllActiveTreatmentTypes() : java.util.Collections.emptyList());
        model.addAttribute("staffUsers", userRepository.findAll());
        model.addAttribute("clinicName", clinicSettingService != null ? clinicSettingService.getSettingValue("clinic_name", "Sunrise Dental Clinic") : "Sunrise Dental Clinic");
        model.addAttribute("clinicAddress", clinicSettingService != null ? clinicSettingService.getSettingValue("clinic_address", "123 Galle Road, Colombo 03") : "123 Galle Road, Colombo 03");
        model.addAttribute("clinicPhone", clinicSettingService != null ? clinicSettingService.getSettingValue("clinic_phone", "011-2345678 / 077-1234567") : "011-2345678 / 077-1234567");
        model.addAttribute("clinicCharge", clinicSettingService != null ? clinicSettingService.getClinicCharge() : new BigDecimal("500.00"));
        model.addAttribute("patientDTO", new PatientDTO());
        model.addAttribute("bookingRequest", new BookingRequestDTO());
        model.addAttribute("dentistDTO", new DentistDTO());
        model.addAttribute("scheduleDTO", new DentistScheduleDTO());
    }
}
