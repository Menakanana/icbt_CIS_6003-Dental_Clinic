package com.dentalclinic.controller;

import com.dentalclinic.dto.AuthResponseDTO;
import com.dentalclinic.dto.LoginRequestDTO;
import com.dentalclinic.exception.BadCredentialsException;
import com.dentalclinic.service.AuthService;
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

    @Autowired
    public WebViewController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Root URL redirect to /login.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * Displays the login page.
     * 
     * @param model Spring MVC model to pass form backing object
     * @return JSP view name "login" (maps to /WEB-INF/views/login.jsp)
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDTO());
        return "login";
    }

    /**
     * Handles web form submission for login.
     * 
     * @param loginRequest Form data bound to LoginRequestDTO
     * @param model Spring MVC model for rendering view attributes
     * @return "dashboard" view on success, or "login" view with error message on failure
     */
    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequestDTO loginRequest, Model model) {
        try {
            AuthResponseDTO authResponse = authService.authenticate(loginRequest);
            model.addAttribute("user", authResponse);
            return "dashboard"; // Renders /WEB-INF/views/dashboard.jsp
        } catch (BadCredentialsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "login"; // Re-renders login.jsp with error alert
        }
    }
}
