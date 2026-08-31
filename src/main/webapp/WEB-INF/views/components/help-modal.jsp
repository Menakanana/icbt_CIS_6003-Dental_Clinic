<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- HELP SECTION MODAL COMPONENT -->
<div id="helpModal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 999; justify-content: center; align-items: center;">
    <div style="background: #FFF; padding: 2rem; border-radius: var(--radius-md); max-width: 600px; width: 90%; max-height: 85vh; overflow-y: auto;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
            <h3 style="color: var(--primary);">❓ Staff User Manual & Step-by-Step Instructions</h3>
            <button type="button" class="btn-logout" onclick="toggleHelpModal()">✕ Close</button>
        </div>
        <ol style="line-height: 1.8rem; font-size: 0.9rem; padding-left: 1.2rem;">
            <li><strong>User Authentication (Login):</strong> Securely sign in using your username and BCrypt password. Receptionists and Admins have role-based access.</li>
            <li><strong>Registering Patients:</strong> Go to the <em>Patient Directory</em> tab to register new patients, or use the 1-Step Fast Quick Add on the booking screen.</li>
            <li><strong>Booking Appointments:</strong> Select a patient, choose an available Dentist and Date, click a green time slot button, and click <em>Confirm & Generate Ticket</em>.</li>
            <li><strong>Display & Search Appointments:</strong> Use the Search Bar at the top of the Appointment Booking tab to lookup appointment details by Appointment ID.</li>
            <li><strong>Calculating & Printing Bills:</strong> Navigate to the <em>Billing & Invoices</em> tab, enter any optional discount, and click <em>Calculate & Print Receipt</em> to generate an official printable invoice.</li>
            <li><strong>Exit System:</strong> Click the red <em>Logout</em> button at the top right of the navigation header to safely end your session.</li>
        </ol>
    </div>
</div>
