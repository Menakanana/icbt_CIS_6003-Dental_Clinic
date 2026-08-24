<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- TAB 3: BILLING & INVOICES COMPONENT -->
<div id="billing-tab" class="tab-content">
    <div class="card-panel">
        <h3 style="margin-bottom: 0.5rem; color: var(--primary);">💳 Billing & Patient Invoice Generator</h3>
        <p style="color: var(--text-muted); font-size: 0.9rem; margin-bottom: 1.5rem;">Select an appointment below to calculate total treatment costs (Consultation Fee + Procedure Base Charge - Discount) and print an official receipt.</p>

        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>APT #</th>
                        <th>Token #</th>
                        <th>Patient Name</th>
                        <th>Dentist</th>
                        <th>Consultation Fee</th>
                        <th>Discount (LKR)</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${todayAppointments}" var="app">
                        <tr>
                            <td><strong>APT-<c:out value="${app.appointmentId}"/></strong></td>
                            <td>#<c:out value="${app.tokenNumber}"/></td>
                            <td><strong><c:out value="${app.patientName}"/></strong></td>
                            <td><c:out value="${app.dentistName}"/></td>
                            <td>LKR <fmt:formatNumber value="${app.consultationFee}" type="currency" currencySymbol=""/></td>
                            <td>
                                <input type="number" id="discount_${app.appointmentId}" class="form-control" style="width: 100px;" value="0" min="0" step="50" placeholder="0.00">
                            </td>
                            <td>
                                <button type="button" class="btn-primary" style="padding: 0.4rem 0.8rem; font-size: 0.85rem;" onclick="generateReceipt(${app.appointmentId})">🖨️ Calculate & Print Receipt</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty todayAppointments}">
                        <tr>
                            <td colspan="7" style="text-align: center; color: var(--text-muted);">No appointments booked for billing calculation today.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
