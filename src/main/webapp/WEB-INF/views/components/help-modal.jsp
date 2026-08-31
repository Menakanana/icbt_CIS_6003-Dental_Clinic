<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- HELP SECTION MODAL COMPONENT -->
<div id="helpModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.6); z-index: 9999; justify-content: center; align-items: center; padding: 1rem;">
    <div style="background: #FFFFFF; border-radius: 12px; max-width: 880px; width: 95%; max-height: 90vh; display: flex; flex-direction: column; box-shadow: 0 20px 40px rgba(0,0,0,0.3); overflow: hidden;">
        
        <!-- Modal Header -->
        <div style="background: #1F4E78; color: #FFFFFF; padding: 1.25rem 1.75rem; display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #153B5C;">
            <h3 style="margin: 0; font-size: 1.25rem; font-weight: 600; display: flex; align-items: center; gap: 10px;">
                <span>📖</span> Staff User Guide & System Walkthrough
            </h3>
            <button type="button" onclick="toggleHelpModal()" style="background: rgba(255,255,255,0.2); border: none; color: #FFF; font-size: 1.1rem; padding: 6px 14px; border-radius: 6px; cursor: pointer; font-weight: 600; transition: background 0.2s;" onmouseover="this.style.background='rgba(255,255,255,0.3)'" onmouseout="this.style.background='rgba(255,255,255,0.2)'">✕ Close</button>
        </div>

        <!-- Modal Body Content -->
        <div style="padding: 1.5rem 1.75rem; overflow-y: auto; flex: 1; display: flex; flex-direction: column; gap: 1.75rem; background: #F8FAFC;">
            
            <!-- Quick Intro Notice -->
            <div style="background: #EBF5FF; border-left: 4px solid #1F4E78; padding: 1rem 1.25rem; border-radius: 6px; font-size: 0.9rem; color: #1E3A8A; line-height: 1.5;">
                <strong>Welcome to Sunrise Dental Clinic Staff Management System!</strong><br>
                This visual guide outlines step-by-step instructions for user sign-in, patient registration, slot booking, billing receipt generation, doctor scheduling, and administrative reporting.
            </div>

            <!-- Step 1: Login & Staff Portal Authentication -->
            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <h4 style="margin-top: 0; color: #1F4E78; font-size: 1.1rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #1F4E78; color: #FFF; width: 26px; height: 26px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 0.85rem;">1</span>
                    User Sign In & Staff Portal Access
                </h4>
                <p style="font-size: 0.9rem; color: #475569; margin-bottom: 1rem; line-height: 1.5;">
                    Sign in to the system using your registered Staff Username and BCrypt encrypted password. System Administrators and Receptionists have role-based access to clinic features.
                </p>
                <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                    <div style="flex: 1; min-width: 260px; text-align: center;">
                        <img src="/images/01_login_page.png" alt="Staff Login Page" style="width: 100%; max-height: 240px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                        <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 1.1: Staff Portal Sign In Screen</span>
                    </div>
                    <div style="flex: 1; min-width: 260px; text-align: center;">
                        <img src="/images/12_forgot_password_modal.png" alt="Forgot Password Modal" style="width: 100%; max-height: 240px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                        <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 1.2: Password Reset Request Modal</span>
                    </div>
                </div>
            </div>

            <!-- Step 2: Patient Directory & Registration -->
            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <h4 style="margin-top: 0; color: #1F4E78; font-size: 1.1rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #1F4E78; color: #FFF; width: 26px; height: 26px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 0.85rem;">2</span>
                    Patient Directory & Registration
                </h4>
                <p style="font-size: 0.9rem; color: #475569; margin-bottom: 1rem; line-height: 1.5;">
                    Navigate to the <strong>Patient Directory</strong> tab to register new clinic patients with full demographic details (Full Name, NIC, Contact Number, Email, Date of Birth, Gender, Address). You can search patient records instantly by Name or NIC.
                </p>
                <div style="text-align: center;">
                    <img src="/images/03_dashboard_patients_tab.png" alt="Patient Directory Tab" style="width: 100%; max-height: 280px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                    <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 2: Patient Directory & Registration Interface</span>
                </div>
            </div>

            <!-- Step 3: Appointment Scheduling & Slot Booking -->
            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <h4 style="margin-top: 0; color: #1F4E78; font-size: 1.1rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #1F4E78; color: #FFF; width: 26px; height: 26px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 0.85rem;">3</span>
                    Appointment Scheduling & Slot Booking
                </h4>
                <p style="font-size: 0.9rem; color: #475569; margin-bottom: 1rem; line-height: 1.5;">
                    Select a patient, target Dentist, Treatment Type, and Date. Available 30-minute time slots generate dynamically with sequential <strong>Queue Token Numbers</strong> (e.g., Token #1, Token #2). Click a green slot button and hit <em>Confirm Booking</em> to print an official <strong>Appointment Ticket</strong>.
                </p>
                <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                    <div style="flex: 1; min-width: 260px; text-align: center;">
                        <img src="/images/02_dashboard_booking_tab.png" alt="Appointment Booking Tab" style="width: 100%; max-height: 240px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                        <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 3.1: Interactive Slot Selection & Booking Screen</span>
                    </div>
                    <div style="flex: 1; min-width: 260px; text-align: center;">
                        <img src="/images/09_printable_ticket.png" alt="Printable Appointment Ticket" style="width: 100%; max-height: 240px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                        <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 3.2: Official Printable Patient Ticket</span>
                    </div>
                </div>
            </div>

            <!-- Step 4: Billing, Discounts & Payment Receipt Generation -->
            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <h4 style="margin-top: 0; color: #1F4E78; font-size: 1.1rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #1F4E78; color: #FFF; width: 26px; height: 26px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 0.85rem;">4</span>
                    Financial Billing & Payment Receipts
                </h4>
                <p style="font-size: 0.9rem; color: #475569; margin-bottom: 1rem; line-height: 1.5;">
                    On the <strong>Billing & Invoices</strong> tab, lookup an appointment by Appointment ID. Enter any optional discount amount. The system calculates the itemized total: <br>
                    <code style="background: #F1F5F9; padding: 2px 6px; border-radius: 4px; color: #0F172A;">Total = Doctor Consultation Fee + Clinic Charge + Treatment Cost - Discount</code><br>
                    Click <em>Process Payment</em> to auto-print a payment receipt and dispatch an automated receipt to the patient's email.
                </p>
                <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                    <div style="flex: 1; min-width: 260px; text-align: center;">
                        <img src="/images/04_dashboard_billing_tab.png" alt="Billing Tab" style="width: 100%; max-height: 240px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                        <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 4.1: Billing Calculation & Invoice Processing</span>
                    </div>
                    <div style="flex: 1; min-width: 260px; text-align: center;">
                        <img src="/images/10_printable_receipt.png" alt="Printable Receipt" style="width: 100%; max-height: 240px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                        <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 4.2: Official Printable & Emailed Payment Receipt</span>
                    </div>
                </div>
            </div>

            <!-- Step 5: Doctor Shift Scheduling -->
            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <h4 style="margin-top: 0; color: #1F4E78; font-size: 1.1rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #1F4E78; color: #FFF; width: 26px; height: 26px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 0.85rem;">5</span>
                    Doctor Shift & Roster Management
                </h4>
                <p style="font-size: 0.9rem; color: #475569; margin-bottom: 1rem; line-height: 1.5;">
                    Clinic receptionists and administrators can manage daily doctor shifts on the <strong>Doctor Schedules</strong> tab. Set shift start/end times, maximum session capacity, and slot durations (e.g., 30 mins) to enable automated slot generation.
                </p>
                <div style="text-align: center;">
                    <img src="/images/06_dashboard_schedules_tab.png" alt="Doctor Schedules Tab" style="width: 100%; max-height: 280px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                    <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 5: Doctor Shift Roster & Schedule Management</span>
                </div>
            </div>

            <!-- Step 6: Admin Reports & Staff User Management -->
            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; border-radius: 10px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.03);">
                <h4 style="margin-top: 0; color: #1F4E78; font-size: 1.1rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #1F4E78; color: #FFF; width: 26px; height: 26px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; font-size: 0.85rem;">6</span>
                    Admin System Reports & Staff User Management
                </h4>
                <p style="font-size: 0.9rem; color: #475569; margin-bottom: 1rem; line-height: 1.5;">
                    Administrators can access the <strong>Admin Reports & System</strong> tab to generate 5 real-time operational reports (Daily Appointments, Doctor Performance, Financial Income Summary, Treatment Type Analytics, and Patient Visit History) as well as register new receptionists and staff accounts.
                </p>
                <div style="text-align: center;">
                    <img src="/images/08_dashboard_admin_reports_tab.png" alt="Admin Reports Tab" style="width: 100%; max-height: 280px; object-fit: contain; border: 1px solid #CBD5E1; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.08);">
                    <span style="font-size: 0.8rem; color: #64748B; display: block; margin-top: 6px;">Figure 6: Admin Reports & Staff User Management Console</span>
                </div>
            </div>

        </div>

        <!-- Modal Footer -->
        <div style="background: #FFFFFF; border-top: 1px solid #E2E8F0; padding: 1rem 1.75rem; display: flex; justify-content: space-between; align-items: center;">
            <span style="font-size: 0.85rem; color: #64748B;">Sunrise Dental Clinic Management System v1.0.0</span>
            <button type="button" onclick="toggleHelpModal()" style="background: #1F4E78; color: #FFFFFF; border: none; padding: 8px 22px; border-radius: 6px; cursor: pointer; font-weight: 600; font-size: 0.9rem;">Close Help Guide</button>
        </div>

    </div>
</div>

