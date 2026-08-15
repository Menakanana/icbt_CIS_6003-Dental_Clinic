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

<!-- Header -->
<div class="app-header">
    <div class="brand-title">
        <h1>Sunrise Dental Clinic</h1>
        <p>Staff Operations & Management Portal</p>
    </div>
    <div class="user-profile">
        <span>Welcome, <strong><c:out value="${user.fullName != null ? user.fullName : 'Staff Member'}"/></strong></span>
        <span class="badge ${user.role == 'Admin' ? 'badge-admin' : 'badge-receptionist'}">
            <c:out value="${user.role != null ? user.role : 'Receptionist'}"/>
        </span>
        <a href="${pageContext.request.contextPath}/login" class="btn-logout">Logout</a>
    </div>
</div>

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

    <!-- Metrics Summary Cards -->
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

    <!-- Navigation Tabs -->
    <div class="tabs-header">
        <button class="tab-btn active" onclick="switchTab('booking-tab', this)">📅 Appointment Booking Engine</button>
        <button class="tab-btn" onclick="switchTab('patients-tab', this)">👥 Patient Directory</button>
        <button class="tab-btn" onclick="switchTab('billing-tab', this)">💳 Billing & Invoices</button>
        <c:if test="${user.role == 'Admin'}">
            <button class="tab-btn" onclick="switchTab('admin-tab', this)">📊 Admin Reports & System</button>
        </c:if>
    </div>

    <!-- TAB 1: APPOINTMENT BOOKING ENGINE -->
    <div id="booking-tab" class="tab-content active">
        <div class="booking-grid">
            
            <!-- Col 1: Patient Search & Fast 1-Step Quick Add -->
            <div class="card-panel">
                <h3 style="margin-bottom: 1rem; color: var(--primary);">1. Select / Quick-Add Patient</h3>
                
                <div class="form-group">
                    <label>Search Existing Patient</label>
                    <input type="text" id="patientSearchInput" class="form-control" placeholder="🔍 Search patient by name, NIC, phone..." oninput="filterPatientSelectDropdown()" style="margin-bottom: 0.5rem;">
                    <select id="patientSelect" class="form-control" onchange="onPatientSelectChange()" size="4" style="height: 110px;">
                        <option value="">-- Choose Existing Patient --</option>
                        <c:forEach items="${patients}" var="p">
                            <option value="${p.patientId}" data-search="${p.patientName.toLowerCase()} ${p.contactNumber} ${p.nic}">${p.patientName} (${p.contactNumber})</option>
                        </c:forEach>
                    </select>
                </div>

                <div style="text-align: center; margin: 0.75rem 0; color: var(--text-muted); font-size: 0.85rem;">- OR -</div>

                <div style="background-color: #F8FAFC; border: 1px dashed var(--border-color); padding: 1rem; border-radius: var(--radius-sm);">
                    <h4 style="font-size: 0.9rem; margin-bottom: 0.5rem; color: var(--primary);">+ Fast 1-Step Quick Add</h4>
                    <div class="form-group">
                        <label>Patient Full Name</label>
                        <input type="text" id="quickName" class="form-control" placeholder="e.g. Perera Silva" oninput="onQuickAddInput()">
                    </div>
                    <div class="form-group">
                        <label>Phone Number</label>
                        <input type="text" id="quickPhone" class="form-control" placeholder="e.g. 0771234567" oninput="onQuickAddInput()">
                    </div>
                    <div class="form-group">
                        <label>NIC Number (Optional)</label>
                        <input type="text" id="quickNic" class="form-control" placeholder="e.g. 199012345678" oninput="onQuickAddInput()">
                    </div>
                </div>
            </div>

            <!-- Col 2: Dentist Selection & Dynamic Slot Grid -->
            <div class="card-panel">
                <h3 style="margin-bottom: 1rem; color: var(--primary);">2. Choose Dentist & Time Slot</h3>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label>Select Dentist</label>
                        <select id="dentistSelect" class="form-control" onchange="loadSlots()">
                            <option value="">-- Select Dentist --</option>
                            <c:forEach items="${dentists}" var="d">
                                <option value="${d.dentistId}">${d.dentistName} (${d.specialization})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Date</label>
                        <input type="date" id="bookingDate" class="form-control" onchange="loadSlots()">
                    </div>
                </div>

                <label style="font-weight: 600; font-size: 0.85rem; margin-top: 0.5rem; display: block;">Available Time Slots & Queue Tokens</label>
                <div id="slotsContainer" class="slots-container">
                    <p style="color: var(--text-muted); font-size: 0.85rem; grid-column: 1/-1;">Please select a Dentist and Date to load available slots.</p>
                </div>
            </div>

            <!-- Col 3: Booking Summary Ticket & Confirm -->
            <div class="card-panel">
                <h3 style="margin-bottom: 1rem; color: var(--primary);">3. Booking Ticket Summary</h3>
                
                <form action="${pageContext.request.contextPath}/booking/create" method="post" id="bookingForm" onsubmit="return validateBookingForm()">
                    <input type="hidden" name="patientId" id="formPatientId">
                    <input type="hidden" name="quickPatientName" id="formQuickName">
                    <input type="hidden" name="quickContactNumber" id="formQuickPhone">
                    <input type="hidden" name="quickNic" id="formQuickNic">
                    <input type="hidden" name="dentistId" id="formDentistId">
                    <input type="hidden" name="appointmentDate" id="formDate">
                    <input type="hidden" name="startTime" id="formStartTime">
                    <input type="hidden" name="tokenNumber" id="formTokenNumber">

                    <div style="background-color: #ECFDF5; border: 1px solid #A7F3D0; padding: 1rem; border-radius: var(--radius-sm); margin-bottom: 1rem;">
                        <p style="font-size: 0.85rem; color: #065F46;"><strong>Selected Slot:</strong> <span id="summarySlot">None Selected</span></p>
                        <p style="font-size: 0.85rem; color: #065F46; margin-top: 0.25rem;"><strong>Queue Token:</strong> <span id="summaryToken">-</span></p>
                    </div>

                    <div class="form-group">
                        <label>Notes / Patient Complaint</label>
                        <textarea name="notes" class="form-control" rows="3" placeholder="e.g. Toothache upper right molar"></textarea>
                    </div>

                    <button type="submit" class="btn-primary" style="width: 100%; padding: 0.75rem;">Confirm & Generate Ticket</button>
                </form>
            </div>

        </div>

        <!-- Today's Schedule Table -->
        <div class="card-panel" style="margin-top: 1.5rem;">
            <h3 style="margin-bottom: 1rem; color: var(--primary);">Today's Confirmed Appointments</h3>
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Token #</th>
                            <th>Time Window</th>
                            <th>Patient Name</th>
                            <th>Contact</th>
                            <th>Dentist</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${todayAppointments}" var="app">
                            <tr>
                                <td><strong>#<c:out value="${app.tokenNumber}"/></strong></td>
                                <td><c:out value="${app.displayTimeRange}"/></td>
                                <td><c:out value="${app.patientName}"/></td>
                                <td><c:out value="${app.contactNumber}"/></td>
                                <td><c:out value="${app.dentistName}"/></td>
                                <td><span class="badge badge-receptionist"><c:out value="${app.status}"/></span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty todayAppointments}">
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted);">No appointments booked for today yet.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

    <!-- TAB 2: PATIENT DIRECTORY -->
    <div id="patients-tab" class="tab-content">
        <div class="card-panel">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                <h3 style="color: var(--primary);">Patient Directory & Profiles</h3>
                <input type="text" id="searchPatientDirectoryInput" class="form-control" style="width: 280px;" placeholder="Search by name, NIC, phone..." onkeyup="filterPatientDirectoryTable()">
            </div>
            
            <div class="table-responsive">
                <table class="data-table" id="patientDirectoryTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Patient Name</th>
                            <th>NIC</th>
                            <th>Contact Number</th>
                            <th>Address</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${patients}" var="p">
                            <tr>
                                <td>#<c:out value="${p.patientId}"/></td>
                                <td><strong><c:out value="${p.patientName}"/></strong></td>
                                <td><c:out value="${p.nic != null ? p.nic : 'N/A'}"/></td>
                                <td><c:out value="${p.contactNumber}"/></td>
                                <td><c:out value="${p.address != null ? p.address : 'N/A'}"/></td>
                                <td>
                                    <button type="button" class="btn-logout" onclick="selectPatientForBooking(${p.patientId})">Book Slot</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty patients}">
                            <tr>
                                <td colspan="6" style="text-align: center; color: var(--text-muted);">No patients registered.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- TAB 3: BILLING & INVOICES -->
    <div id="billing-tab" class="tab-content">
        <div class="card-panel">
            <h3 style="margin-bottom: 1rem; color: var(--primary);">Billing & Invoice Generator</h3>
            <p style="color: var(--text-muted); font-size: 0.9rem;">Select a completed appointment to generate payment receipt (Consultation Fee + Procedure Charges - Discount).</p>
        </div>
    </div>

    <!-- TAB 4: ADMIN REPORTS -->
    <c:if test="${user.role == 'Admin'}">
        <div id="admin-tab" class="tab-content">
            <div class="card-panel">
                <h3 style="margin-bottom: 1rem; color: var(--primary);">Executive Analytics & Reports</h3>
                <p style="color: var(--text-muted); font-size: 0.9rem;">Daily/Monthly revenue summary, staff user account creation, and clinic schedule configuration.</p>
            </div>
        </div>
    </c:if>

</div>

<script>
    // Initialize default date to today
    document.getElementById('bookingDate').valueAsDate = new Date();

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
</script>

</body>
</html>
