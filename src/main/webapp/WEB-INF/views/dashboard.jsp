<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic - Staff Operations Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<!-- Header Component -->
<jsp:include page="components/header.jsp"/>

<div class="dashboard-container">

    <!-- Alert Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert-success">
            <strong>Success:</strong> <c:out value="${successMessage}"/>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert-error">
            <strong>Error:</strong> <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <!-- Metrics Summary Component -->
    <jsp:include page="components/metrics-summary.jsp"/>

    <!-- Navigation Tabs Header Component -->
    <jsp:include page="components/nav-tabs.jsp"/>

    <!-- Tab 1: Appointment Booking Engine Component -->
    <jsp:include page="components/booking-tab.jsp"/>

    <!-- Tab 2: Patient Directory Component -->
    <jsp:include page="components/patients-tab.jsp"/>

    <!-- Tab 3: Billing & Invoices Component -->
    <jsp:include page="components/billing-tab.jsp"/>

    <!-- Tab 4: Doctor Directory Component -->
    <jsp:include page="components/doctors-tab.jsp"/>

    <!-- Tab 5: Daily Shift Roster Component -->
    <jsp:include page="components/schedules-tab.jsp"/>

    <!-- Tab 6: Clinic Profile & System Settings Component -->
    <jsp:include page="components/clinic-settings-tab.jsp"/>

    <!-- Tab 7: Admin Reports & Analytics Component -->
    <jsp:include page="components/admin-reports-tab.jsp"/>

    <!-- Help Modal Component -->
    <jsp:include page="components/help-modal.jsp"/>

</div>

<!-- Global Client-Side Scripts -->
<script>
    // Initialize default dates & apply past/future date restrictions
    const todayStr = new Date().toISOString().split('T')[0];

    const bookingDateEl = document.getElementById('bookingDate');
    if (bookingDateEl) {
        bookingDateEl.value = todayStr;
        bookingDateEl.min = todayStr;
    }

    const rosterDateEl = document.getElementById('rosterDatePicker');
    if (rosterDateEl) {
        rosterDateEl.value = todayStr;
        rosterDateEl.min = todayStr;
    }

    const dobEl = document.getElementById('dobInput');
    if (dobEl) {
        dobEl.max = todayStr;
    }

    function switchTab(tabId, btn) {
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.getElementById(tabId).classList.add('active');
        btn.classList.add('active');
    }

    function filterPatientSelectDropdown() {
        const query = document.getElementById('patientSearchInput').value.toLowerCase();
        const options = document.querySelectorAll('#patientSelect option');
        options.forEach(opt => {
            if(!opt.value) return;
            const searchData = (opt.getAttribute('data-search') || '').toLowerCase();
            const text = opt.innerText.toLowerCase();
            if(searchData.includes(query) || text.includes(query)) {
                opt.style.display = '';
            } else {
                opt.style.display = 'none';
            }
        });
    }

    function onPatientSelectChange() {
        const val = document.getElementById('patientSelect').value;
        if(val) {
            document.getElementById('quickName').value = '';
            document.getElementById('quickPhone').value = '';
            document.getElementById('quickNic').value = '';
        }
    }

    function onQuickAddInput() {
        document.getElementById('patientSelect').value = '';
    }

    function loadSlots() {
        const dentistId = document.getElementById('dentistSelect').value;
        const date = document.getElementById('bookingDate').value;
        const container = document.getElementById('slotsContainer');

        if(!dentistId || !date) {
            container.innerHTML = '<p style="color: var(--text-muted); font-size: 0.85rem; grid-column: 1/-1;">Please select a Dentist and Date to load available slots.</p>';
            return;
        }

        container.innerHTML = '<p style="color: var(--primary); font-size: 0.85rem; grid-column: 1/-1;">Loading available slots...</p>';

        const apiUrl = `${pageContext.request.contextPath}/api/slots/available?dentistId=${dentistId}&date=${date}`;

        fetch(apiUrl)
            .then(res => {
                if(!res.ok) {
                    throw new Error(`Server returned HTTP status ${res.status}`);
                }
                return res.json();
            })
            .then(slots => {
                if(!slots || slots.length === 0) {
                    container.innerHTML = '<p style="color: var(--text-muted); font-size: 0.85rem; grid-column: 1/-1;">No available slots for this date.</p>';
                    return;
                }
                let html = '';
                slots.forEach(s => {
                    if(s.isAvailable) {
                        html += `<button type="button" class="slot-btn available" onclick="selectSlot('${s.startTime}', '${s.displayTime}', ${s.tokenNumber}, this)">
                                    #${s.tokenNumber}<br>${s.displayTime}
                                 </button>`;
                    } else {
                        html += `<button type="button" class="slot-btn disabled" disabled title="Booked">
                                    #${s.tokenNumber}<br>${s.displayTime}
                                 </button>`;
                    }
                });
                container.innerHTML = html;
            })
            .catch(err => {
                console.error("Error fetching slots:", err);
                container.innerHTML = `<p style="color: var(--accent-red); font-size: 0.85rem; grid-column: 1/-1;">Unable to load slots (${err.message}).</p>`;
            });
    }

    function selectSlot(startTime, displayTime, tokenNumber, btn) {
        document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');

        document.getElementById('formStartTime').value = startTime;
        document.getElementById('formTokenNumber').value = tokenNumber;

        document.getElementById('summarySlot').innerText = displayTime;
        document.getElementById('summaryToken').innerText = '#' + tokenNumber;

        // Sync Form Hidden inputs
        document.getElementById('formDentistId').value = document.getElementById('dentistSelect').value;
        document.getElementById('formDate').value = document.getElementById('bookingDate').value;
        document.getElementById('formPatientId').value = document.getElementById('patientSelect').value;
        document.getElementById('formQuickName').value = document.getElementById('quickName').value;
        document.getElementById('formQuickPhone').value = document.getElementById('quickPhone').value;
        document.getElementById('formQuickNic').value = document.getElementById('quickNic').value;
    }

    function validateBookingForm() {
        const patientId = document.getElementById('patientSelect').value;
        const quickName = document.getElementById('quickName').value.trim();
        const startTime = document.getElementById('formStartTime').value;

        if(!patientId && !quickName) {
            alert("Please select an existing patient or type a patient's full name under Quick-Add.");
            return false;
        }

        if(!startTime) {
            alert("Please click an available time slot button (green) to select a booking time.");
            return false;
        }

        // Sync values right before submission
        document.getElementById('formDentistId').value = document.getElementById('dentistSelect').value;
        document.getElementById('formDate').value = document.getElementById('bookingDate').value;
        document.getElementById('formPatientId').value = patientId;
        document.getElementById('formQuickName').value = quickName;
        document.getElementById('formQuickPhone').value = document.getElementById('quickPhone').value;
        document.getElementById('formQuickNic').value = document.getElementById('quickNic').value;

        return true;
    }

    function filterPatientDirectoryTable() {
        const query = document.getElementById('searchPatientDirectoryInput').value.toLowerCase();
        const rows = document.querySelectorAll('#patientDirectoryTable tbody tr');
        rows.forEach(r => {
            const text = r.innerText.toLowerCase();
            r.style.display = text.includes(query) ? '' : 'none';
        });
    }

    function selectPatientForBooking(patientId) {
        document.getElementById('patientSelect').value = patientId;
        switchTab('booking-tab', document.querySelectorAll('.tab-btn')[0]);
    }

    function toggleRegistrationForm() {
        const form = document.getElementById('registrationForm');
        const btn = document.getElementById('toggleRegBtn');
        if (form.style.display === 'none' || !form.style.display) {
            form.style.display = 'block';
            btn.innerText = '✕ Hide Registration Form';
        } else {
            form.style.display = 'none';
            btn.innerText = '+ Show Registration Form';
        }
    }

    function toggleHelpModal() {
        const modal = document.getElementById('helpModal');
        modal.style.display = (modal.style.display === 'none' || !modal.style.display) ? 'flex' : 'none';
    }

    function generateReceipt(appointmentId) {
        const discountInput = document.getElementById('discount_' + appointmentId);
        const discount = discountInput ? discountInput.value : 0;
        window.location.href = `${pageContext.request.contextPath}/billing/receipt/${appointmentId}?discount=${discount}`;
    }
</script>

</body>
</html>
