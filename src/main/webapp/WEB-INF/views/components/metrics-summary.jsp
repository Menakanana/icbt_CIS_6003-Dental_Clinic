<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Metrics Summary Cards Component -->
<div class="metrics-grid">
    <div class="metric-card">
        <div class="metric-info">
            <h4>Today's Bookings</h4>
            <div class="value"><c:out value="${todayAppointments != null ? todayAppointments.size() : 0}"/></div>
        </div>
        <div class="metric-icon">📅</div>
    </div>
    <div class="metric-card">
        <div class="metric-info">
            <h4>Registered Patients</h4>
            <div class="value"><c:out value="${patients != null ? patients.size() : 0}"/></div>
        </div>
        <div class="metric-icon">👥</div>
    </div>
    <div class="metric-card">
        <div class="metric-info">
            <h4>Active Dentists</h4>
            <div class="value"><c:out value="${dentists != null ? dentists.size() : 0}"/></div>
        </div>
        <div class="metric-icon">🩺</div>
    </div>
    <c:if test="${user.role == 'Admin'}">
        <div class="metric-card">
            <div class="metric-info">
                <h4>Daily Revenue (Est.)</h4>
                <div class="value">LKR 12,500.00</div>
            </div>
            <div class="metric-icon">💰</div>
        </div>
    </c:if>
</div>
