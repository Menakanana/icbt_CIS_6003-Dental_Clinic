<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Navigation Tabs Component -->
<div class="tabs-header">
    <button class="tab-btn active" onclick="switchTab('booking-tab', this)">📅 Appointment Booking Engine</button>
    <button class="tab-btn" onclick="switchTab('patients-tab', this)">👥 Patient Directory</button>
    <button class="tab-btn" onclick="switchTab('billing-tab', this)">💳 Billing & Invoices</button>
    <button class="tab-btn" onclick="switchTab('doctors-tab', this)">🩺 Doctor Directory</button>
    <button class="tab-btn" onclick="switchTab('schedules-tab', this)">📅 Daily Shift Roster</button>
    <button class="tab-btn" onclick="switchTab('settings-tab', this)">⚙️ Clinic Settings</button>
    <c:if test="${user.role == 'Admin'}">
        <button class="tab-btn" onclick="switchTab('admin-tab', this)">📊 Admin Reports & System</button>
    </c:if>
</div>
