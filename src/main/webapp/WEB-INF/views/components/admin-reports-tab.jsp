<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- TAB 4: ADMIN REPORTS & ANALYTICS COMPONENT -->
<c:if test="${user.role == 'Admin'}">
    <div id="admin-tab" class="tab-content">
        <div class="card-panel" style="margin-bottom: 1.5rem;">
            <h3 style="margin-bottom: 0.5rem; color: var(--primary);">📊 Executive Analytics & Performance Summary</h3>
            <p style="color: var(--text-muted); font-size: 0.9rem;">Operational metrics, daily revenue overview, and system audit log status.</p>
        </div>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem;">
            <div class="card-panel">
                <h4 style="color: var(--primary); margin-bottom: 1rem;">Revenue & Financial Breakdown</h4>
                <ul style="list-style: none; padding: 0; line-height: 2rem; font-size: 0.95rem;">
                    <li><strong>Est. Total Daily Revenue:</strong> LKR 12,500.00</li>
                    <li><strong>Consultation Revenue:</strong> LKR 7,500.00</li>
                    <li><strong>Procedure Base Revenue:</strong> LKR 5,000.00</li>
                    <li><strong>Active Billing Accounts:</strong> <c:out value="${patients.size()}"/> Active Patients</li>
                </ul>
            </div>
            <div class="card-panel">
                <h4 style="color: var(--primary); margin-bottom: 1rem;">Clinic Staff & Resource Allocation</h4>
                <ul style="list-style: none; padding: 0; line-height: 2rem; font-size: 0.95rem;">
                    <li><strong>Active Dentists on Duty:</strong> <c:out value="${dentists.size()}"/> Specialists</li>
                    <li><strong>Today's Total Bookings:</strong> <c:out value="${todayAppointments.size()}"/> Appointments</li>
                    <li><strong>System Security Mode:</strong> BCrypt Hashed Passwords</li>
                    <li><strong>Database Provider:</strong> Spring Data JPA / Hibernate</li>
                </ul>
            </div>
        </div>
    </div>
</c:if>
