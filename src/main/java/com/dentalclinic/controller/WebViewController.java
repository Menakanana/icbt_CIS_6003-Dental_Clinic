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
    private final AppointmentService appointmentService;
    private final BillingService billingService;
    private final DentistService dentistService;
    private final ClinicSettingService clinicSettingService;
    private final TreatmentTypeService treatmentTypeService;

    @Autowired
    public WebViewController(AuthService authService,
                              PatientService patientService,
                              DentistRepository dentistRepository,
                              AppointmentService appointmentService,
                              BillingService billingService,
                              DentistService dentistService,
                              @Autowired(required = false) ClinicSettingService clinicSettingService,
                              @Autowired(required = false) TreatmentTypeService treatmentTypeService) {
        this.authService = authService;
        this.patientService = patientService;
        this.dentistRepository = dentistRepository;
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

    @GetMapping("/booking/ticket/{appointmentId}")
    public String showAppointmentTicket(@PathVariable("appointmentId") Integer appointmentId, Model model) {
        AppointmentTicketDTO ticket = appointmentService.getAppointmentById(appointmentId);
        model.addAttribute("ticket", ticket);
        return "ticket"; // Renders /WEB-INF/views/ticket.jsp
    }

    @GetMapping("/billing/receipt/{appointmentId}")
    public String showBillingReceipt(@PathVariable("appointmentId") Integer appointmentId,
                                     @RequestParam(name = "discount", required = false, defaultValue = "0") BigDecimal discount,
                                     Model model) {
        BillingDTO bill = billingService.calculateBill(appointmentId, discount);
        model.addAttribute("bill", bill);
        return "receipt"; // Renders /WEB-INF/views/receipt.jsp
    }

    @PostMapping("/dentists/save")
    public String handleWebSaveDentist(@ModelAttribute("dentistDTO") DentistDTO dentistDTO, Model model) {
        try {
            dentistService.saveDentist(dentistDTO);
            model.addAttribute("successMessage", "Doctor profile saved successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        populateDashboardModel(model);
        return "dashboard";
    }

    @PostMapping("/dentists/schedule/save")
    public String handleWebSaveSchedule(@ModelAttribute("scheduleDTO") DentistScheduleDTO scheduleDTO, Model model) {
        try {
            dentistService.saveSchedule(scheduleDTO);
            model.addAttribute("successMessage", "Doctor availability schedule saved successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        populateDashboardModel(model);
        return "dashboard";
    }

    @PostMapping("/settings/clinic/save")
    public String handleSaveClinicProfile(@RequestParam("clinicName") String clinicName,
                                          @RequestParam("clinicAddress") String clinicAddress,
                                          @RequestParam("clinicPhone") String clinicPhone,
                                          @RequestParam("clinicCharge") BigDecimal clinicCharge,
                                          Model model) {
        try {
            if (clinicSettingService != null) {
                clinicSettingService.saveClinicProfile(clinicName, clinicAddress, clinicPhone, clinicCharge);
                model.addAttribute("successMessage", "Clinic Profile & Facility Charge updated successfully!");
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        populateDashboardModel(model);
        return "dashboard";
    }

    @PostMapping("/settings/treatment/save")
    public String handleSaveTreatmentType(@ModelAttribute com.dentalclinic.dto.TreatmentTypeDTO treatmentDTO, Model model) {
        try {
            if (treatmentTypeService != null) {
                treatmentTypeService.saveTreatmentType(treatmentDTO);
                model.addAttribute("successMessage", "Treatment procedure & tariff price saved successfully!");
            }
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        populateDashboardModel(model);
        return "dashboard";
    }

    private void populateDashboardModel(Model model) {
        model.addAttribute("patients", patientService.getAllActivePatients());
        model.addAttribute("dentists", dentistRepository.findByIsActiveTrue());
        model.addAttribute("schedules", dentistService.getAllActiveSchedules());
        model.addAttribute("todayAppointments", appointmentService.getTodayAppointments());
        model.addAttribute("treatmentTypes", treatmentTypeService != null ? treatmentTypeService.getAllActiveTreatmentTypes() : java.util.Collections.emptyList());
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
