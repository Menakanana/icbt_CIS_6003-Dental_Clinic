<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- TAB 1: APPOINTMENT BOOKING ENGINE COMPONENT -->
<div id="booking-tab" class="tab-content active">
    <div class="booking-grid">
        
        <!-- Col 1: Phone Search & Patient Selection -->
        <div class="card-panel">
            <h3 style="margin-bottom: 1rem; color: var(--primary);">1. Phone Search & Patient Selection</h3>
            
            <div class="form-group" style="margin-bottom: 1rem;">
                <label style="font-weight: 600;">Patient Phone Number *</label>
                <div style="display: flex; gap: 0.5rem;">
                    <input type="text" id="phoneSearchInput" class="form-control" placeholder="e.g. 0771234567" oninput="onPhoneInputChanged()" onkeyup="if(event.key==='Enter') searchPatientsByPhoneUI()">
                    <button type="button" class="btn-primary" style="white-space: nowrap; padding: 0.4rem 0.8rem;" onclick="searchPatientsByPhoneUI()">🔍 Find</button>
                </div>
                <span id="patientErrorMsg" class="field-error-msg" style="display:none;"></span>
            </div>

            <!-- Matching Patients under this Phone Number -->
            <div id="phoneSearchResultsSection" style="margin-bottom: 1rem;">
                <label style="font-weight: 600; font-size: 0.85rem; display: block; margin-bottom: 0.4rem; color: var(--text-dark);">
                    Registered Patients under Phone Number
                </label>
                <div id="patientCardsContainer" style="max-height: 160px; overflow-y: auto; border: 1px solid var(--border-color); border-radius: var(--radius-sm); padding: 0.5rem; background-color: #FAFAFA;">
                    <p style="color: var(--text-muted); font-size: 0.82rem; margin: 0.4rem 0;">Enter a phone number above to search existing patients.</p>
                </div>
            </div>

            <div style="text-align: center; margin: 0.75rem 0; color: var(--text-muted); font-size: 0.8rem;">- OR REGISTER NEW PATIENT -</div>

            <!-- Quick Add New Patient under Searched Phone -->
            <div style="background-color: #F8FAFC; border: 1px dashed var(--border-color); padding: 1rem; border-radius: var(--radius-sm);">
                <h4 style="font-size: 0.9rem; margin-bottom: 0.5rem; color: var(--primary);">+ Add New Patient for this Number</h4>
                <div class="form-group">
                    <label>Patient Full Name</label>
                    <input type="text" id="quickName" class="form-control" placeholder="e.g. Perera Silva" oninput="onQuickAddInput()" onblur="validatePatientSelection()">
                    <span id="quickNameErrorMsg" class="field-error-msg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Phone Number (Auto-Synced)</label>
                    <input type="text" id="quickPhone" class="form-control" placeholder="e.g. 0771234567" oninput="onQuickAddInput()" onblur="validateQuickPhone()">
                    <span id="quickPhoneErrorMsg" class="field-error-msg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>NIC Number (Optional)</label>
                    <input type="text" id="quickNic" class="form-control" placeholder="e.g. 199012345678" oninput="onQuickAddInput()">
                </div>
                <div class="form-group">
                    <label>Patient Email for Receipt (Optional)</label>
                    <input type="email" id="quickEmail" class="form-control" placeholder="e.g. patient@example.com" oninput="onQuickAddInput()">
                </div>
            </div>
            <!-- Hidden element to maintain compatibility with patientSelect logic -->
            <input type="hidden" id="patientSelect" value="">
        </div>

        <!-- Col 2: Dentist Selection & Dynamic Slot Grid -->
        <div class="card-panel">
            <h3 style="margin-bottom: 1rem; color: var(--primary);">2. Choose Dentist & Time Slot</h3>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label>Select Dentist</label>
                    <select id="dentistSelect" class="form-control" onchange="loadSlots(); updateBookingAmountSummary();">
                        <option value="">-- Select Dentist --</option>
                        <c:forEach items="${dentists}" var="d">
                            <option value="${d.dentistId}" data-fee="${d.consultationFee}"><c:out value="${d.dentistName}"/> (<c:out value="${d.specialization}"/>) - LKR <fmt:formatNumber value="${d.consultationFee}" type="currency" currencySymbol=""/></option>
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
            <span id="slotErrorMsg" class="field-error-msg" style="display:none;"></span>
        </div>

        <!-- Col 3: Booking Summary Ticket & Confirm -->
        <div class="card-panel">
            <h3 style="margin-bottom: 1rem; color: var(--primary);">3. Booking Ticket Summary</h3>
            
            <form action="${pageContext.request.contextPath}/booking/create" method="post" id="bookingForm" onsubmit="return validateBookingForm()">
                <input type="hidden" name="patientId" id="formPatientId">
                <input type="hidden" name="quickPatientName" id="formQuickName">
                <input type="hidden" name="quickContactNumber" id="formQuickPhone">
                <input type="hidden" name="quickNic" id="formQuickNic">
                <input type="hidden" name="quickEmail" id="formQuickEmail">
                <input type="hidden" name="dentistId" id="formDentistId">
                <input type="hidden" name="appointmentDate" id="formDate">
                <input type="hidden" name="startTime" id="formStartTime">
                <input type="hidden" name="tokenNumber" id="formTokenNumber">

                <div style="background-color: #ECFDF5; border: 1px solid #A7F3D0; padding: 1rem; border-radius: var(--radius-sm); margin-bottom: 1rem;">
                    <p style="font-size: 0.85rem; color: #065F46;"><strong>Selected Slot:</strong> <span id="summarySlot">None Selected</span></p>
                    <p style="font-size: 0.85rem; color: #065F46; margin-top: 0.25rem;"><strong>Queue Token:</strong> <span id="summaryToken">-</span></p>
                </div>

                <div class="form-group" style="margin-bottom: 0.75rem;">
                    <label style="font-weight: 600;">Registration Deposit Payment *</label>
                    <select name="initialPaymentOption" id="initialPaymentOption" class="form-control" onchange="toggleBookingPaymentMethod(); updateBookingAmountSummary();">
                        <option value="PAY_NOW">💵 Pay Deposit Now (Consultation + Clinic Fee)</option>
                        <option value="PAY_LATER">⏳ Pay Later / Pending (Settle After Channeling)</option>
                    </select>
                </div>

                <div class="form-group" id="bookingPaymentMethodGroup" style="margin-bottom: 0.75rem;">
                    <label>Payment Method</label>
                    <select name="paymentMethod" class="form-control">
                        <option value="Cash">💵 Cash</option>
                        <option value="Credit/Debit Card">💳 Credit / Debit Card</option>
                        <option value="Bank Transfer">📱 Bank / QR Transfer</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Select Primary Treatment Procedure (Optional)</label>
                    <select name="treatmentTypeId" id="bookingTreatmentSelect" class="form-control" onchange="updateBookingAmountSummary()">
                        <option value="">-- General Consultation (Default) --</option>
                        <c:forEach items="${treatmentTypes}" var="tt">
                            <option value="${tt.treatmentTypeId}" data-cost="${tt.baseCost}" data-name="<c:out value='${tt.treatmentName}'/>"><c:out value="${tt.treatmentName}"/> (LKR <fmt:formatNumber value="${tt.baseCost}" type="currency" currencySymbol=""/></option>
                        </c:forEach>
                    </select>
                </div>

                <!-- LIVE BOOKING PAYMENT & DEPOSIT SUMMARY BOX -->
                <div id="bookingAmountSummaryBox" style="background-color: #ECFDF5; border: 1.5px solid #059669; padding: 0.85rem 1rem; border-radius: var(--radius-sm); margin-bottom: 1rem; transition: all 0.2s ease;">
                    <div style="font-size: 0.82rem; color: #065F46; font-weight: 700; text-transform: uppercase; margin-bottom: 0.35rem;">💰 Registration Payment Summary</div>
                    <div style="display: flex; justify-content: space-between; font-size: 0.82rem; color: var(--text-dark); margin-bottom: 0.2rem;">
                        <span>Doctor Consultation Fee:</span>
                        <strong id="summaryConsultationFee">LKR 0.00</strong>
                    </div>
                    <div style="display: flex; justify-content: space-between; font-size: 0.82rem; color: var(--text-dark); margin-bottom: 0.2rem;">
                        <span>Clinic Facility Charge:</span>
                        <strong>LKR 500.00</strong>
                    </div>
                    <div id="summaryTreatmentRow" style="display: none; justify-content: space-between; font-size: 0.82rem; color: var(--text-dark); margin-bottom: 0.2rem;">
                        <span id="summaryTreatmentName">Selected Procedure:</span>
                        <strong id="summaryTreatmentFee">LKR 0.00</strong>
                    </div>
                    <div style="border-top: 1px dashed #A7F3D0; margin-top: 0.4rem; padding-top: 0.4rem; display: flex; justify-content: space-between; font-size: 0.95rem; font-weight: 800; color: #065F46;">
                        <span>TOTAL INITIAL DEPOSIT:</span>
                        <span id="summaryTotalDeposit">LKR 0.00</span>
                    </div>
                </div>

                <div class="form-group">
                    <label>Notes / Patient Complaint</label>
                    <textarea name="notes" class="form-control" rows="2" placeholder="e.g. Toothache upper right molar"></textarea>
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; padding: 0.75rem;">Confirm & Generate Ticket</button>
            </form>
        </div>

    </div>

    <!-- Confirmed Appointments Schedule Card with Date Selection -->
    <div class="card-panel" style="margin-top: 1.5rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; margin-bottom: 1rem;">
            <div>
                <h3 style="margin: 0; color: var(--primary);">📋 Confirmed Appointments Schedule</h3>
                <p style="color: var(--text-muted); font-size: 0.85rem; margin: 0.2rem 0 0 0;">View confirmed patient appointments, queue tokens, and printable passes for any selected date.</p>
            </div>
            <div style="display: flex; align-items: center; gap: 0.5rem;">
                <label for="confirmedAppDatePicker" style="font-weight: 600; color: var(--primary); font-size: 0.9rem;">Select Date:</label>
                <input type="date" id="confirmedAppDatePicker" class="form-control" style="width: 170px;" onchange="onConfirmedAppDateChange()" oninput="onConfirmedAppDateChange()">
            </div>
        </div>

        <div class="table-responsive">
            <table class="data-table" id="confirmedAppointmentsTable">
                <thead>
                    <tr>
                        <th>Appointment No.</th>
                        <th>Token #</th>
                        <th>Date</th>
                        <th>Time Window</th>
                        <th>Patient Name</th>
                        <th>Contact</th>
                        <th>Dentist</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="confirmedAppointmentsTbody">
                    <c:forEach items="${todayAppointments}" var="app">
                        <tr>
                            <td><strong><c:out value="${app.appointmentId}"/></strong></td>
                            <td><strong>#<c:out value="${app.tokenNumber}"/></strong></td>
                            <td><c:out value="${app.formattedAppointmentDate}"/></td>
                            <td><c:out value="${app.displayTimeRange}"/></td>
                            <td>
                                <strong><c:out value="${app.patientName}"/></strong>
                                <c:if test="${not empty app.medicalHistory}">
                                    <br><span style="display: inline-block; margin-top: 0.2rem; padding: 0.15rem 0.4rem; background-color: #FEE2E2; color: #991B1B; border: 1px solid #FECACA; border-radius: 4px; font-size: 0.72rem; font-weight: 600;">⚠️ Alert: <c:out value="${app.medicalHistory}"/></span>
                                </c:if>
                            </td>
                            <td><c:out value="${app.contactNumber}"/></td>
                            <td><c:out value="${app.dentistName}"/></td>
                            <td><span class="badge badge-receptionist"><c:out value="${app.status}"/></span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/booking/ticket/${app.appointmentId}" target="_blank" class="btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; text-decoration: none;">🖨️ Print Ticket</a>
                                <c:set var="isClosedStatus" value="${app.status eq 'COMPLETED' or app.status eq 'CLOSED' or app.status eq 'CANCELLED'}" />
                                <c:set var="isPaidDeposit" value="${not empty app.paidAmount and app.paidAmount > 0 or app.paymentStatus eq 'PAID_DEPOSIT' or app.paymentStatus eq 'FULL_PAID'}" />
                                <c:choose>
                                    <c:when test="${not isClosedStatus}">
                                        <button type="button" class="btn-logout" style="padding: 0.25rem 0.5rem; font-size: 0.75rem; margin-left: 0.3rem;" onclick="openRescheduleModal(${app.appointmentId}, '<c:out value="${app.patientName}"/>', '<c:out value="${app.formattedAppointmentDate}"/>', ${app.dentistId})">🔄 Reschedule</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn-logout" disabled style="padding: 0.25rem 0.5rem; font-size: 0.75rem; margin-left: 0.3rem; opacity: 0.4; cursor: not-allowed;" title="Cannot reschedule <c:out value="${app.status}"/> appointment">🔒 Reschedule</button>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty todayAppointments}">
                        <tr>
                            <td colspan="9" style="text-align: center; color: var(--text-muted);">No appointments booked for today yet.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Reschedule Appointment Modal -->
<div id="rescheduleModal" class="modal-overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; justify-content: center; align-items: center;">
    <div class="modal-card" style="background: #FFF; padding: 2rem; border-radius: 12px; max-width: 550px; width: 90%; max-height: 90vh; overflow-y: auto; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1);">
        <h3 style="color: var(--primary); margin-bottom: 0.5rem;">🔄 Reschedule Patient Appointment</h3>
        <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 1.25rem;" id="rescheduleModalSubtitle">Select dentist, date, and available time slot.</p>
        
        <input type="hidden" id="rescheduleAppId">
        <input type="hidden" id="rescheduleSelectedStartTime">
        <input type="hidden" id="rescheduleSelectedToken">

        <div class="form-group" style="margin-bottom: 1rem;">
            <label style="font-weight: 600;">Select Dentist *</label>
            <select id="rescheduleDentistSelect" class="form-control" onchange="onRescheduleSelectionChange()">
                <c:forEach items="${dentists}" var="d">
                    <option value="${d.dentistId}" data-fee="${d.consultationFee}"><c:out value="${d.dentistName}"/> (<c:out value="${d.specialization}"/>) - LKR <fmt:formatNumber value="${d.consultationFee}" type="currency" currencySymbol=""/></option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group" style="margin-bottom: 1rem;">
            <label style="font-weight: 600;">New Appointment Date *</label>
            <input type="date" id="rescheduleDateInput" class="form-control" required onchange="onRescheduleSelectionChange()" oninput="onRescheduleSelectionChange()">
        </div>

        <div class="form-group" style="margin-bottom: 1.25rem;">
            <label style="font-weight: 600; display: block; margin-bottom: 0.4rem;">Available Time Slots & Queue Tokens *</label>
            <div id="rescheduleSlotsContainer" class="slots-container" style="max-height: 180px; overflow-y: auto; padding: 0.5rem; border: 1px solid var(--border-color); border-radius: var(--radius-sm); background-color: #FAFAFA;">
                <p style="color: var(--text-muted); font-size: 0.85rem; margin: 0.5rem 0;">Loading available time slots...</p>
            </div>
            <span id="rescheduleSlotErrorMsg" class="field-error-msg" style="display:none;"></span>
        </div>

        <div id="rescheduleFeeNotice" style="background-color: #ECFDF5; border: 1px solid #A7F3D0; padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; font-size: 0.82rem; color: #065F46; display: none;">
            <strong>💡 Consultation Fee:</strong> <span id="rescheduleFeeText">LKR 0.00</span> (Any fee variance will be adjusted on final billing receipt).
        </div>

        <div id="rescheduleFeedback" class="field-error-msg" style="display: none; margin-bottom: 1rem;"></div>

        <div style="display: flex; gap: 0.75rem; justify-content: flex-end;">
            <button type="button" class="btn-logout" onclick="closeRescheduleModal()">Cancel</button>
            <button type="button" class="btn-primary" onclick="submitReschedule()">Confirm Reschedule</button>
        </div>
    </div>
</div>

<script>
    function getLocalDateString() {
        const d = new Date();
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return year + '-' + month + '-' + day;
    }

    document.addEventListener("DOMContentLoaded", function() {
        const picker = document.getElementById('confirmedAppDatePicker');
        if (picker && !picker.value) {
            picker.value = getLocalDateString();
        }
    });

    function onConfirmedAppDateChange() {
        const dateVal = document.getElementById('confirmedAppDatePicker').value;
        if (!dateVal) return;
        loadConfirmedAppointmentsByDate(dateVal);
    }

    async function loadConfirmedAppointmentsByDate(dateVal) {
        const tbody = document.getElementById('confirmedAppointmentsTbody');
        if (!tbody) return;

        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/appointments/date?date=' + dateVal);
            if (resp.ok) {
                const list = await resp.json();
                const table = document.getElementById('confirmedAppointmentsTable');
                if (!list || list.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="9" style="text-align: center; color: var(--text-muted);">No appointments booked for ' + dateVal + '.</td></tr>';
                    if (table && typeof initTablePagination === 'function') {
                        initTablePagination(table, 10);
                    }
                    return;
                }

                let html = '';
                list.forEach(app => {
                    const alertTag = app.medicalHistory ? '<br><span style="display: inline-block; margin-top: 0.2rem; padding: 0.15rem 0.4rem; background-color: #FEE2E2; color: #991B1B; border: 1px solid #FECACA; border-radius: 4px; font-size: 0.72rem; font-weight: 600;">⚠️ Alert: ' + escapeHtml(app.medicalHistory) + '</span>' : '';
                    const apptNo = (app.appointmentId || app.appointmentNumber || '');
                    const statusUpper = (app.status || 'BOOKED').toUpperCase();
                    const isClosedStatus = (statusUpper === 'COMPLETED' || statusUpper === 'CLOSED' || statusUpper === 'CANCELLED');
                    const isPaidDeposit = (app.paidAmount && parseFloat(app.paidAmount) > 0) || (app.paymentStatus === 'PAID_DEPOSIT' || app.paymentStatus === 'FULL_PAID');
                    
                    const isReschedulable = !isClosedStatus;

                    const rescheduleBtnHtml = isReschedulable
                        ? '<button type="button" class="btn-logout" style="padding: 0.25rem 0.5rem; font-size: 0.75rem; margin-left: 0.3rem;" onclick="openRescheduleModal(' + app.appointmentId + ', \'' + escapeHtml(app.patientName) + '\', \'' + app.appointmentDate + '\', ' + app.dentistId + ')">🔄 Reschedule</button>'
                        : '<button type="button" class="btn-logout" disabled style="padding: 0.25rem 0.5rem; font-size: 0.75rem; margin-left: 0.3rem; opacity: 0.4; cursor: not-allowed;" title="Cannot reschedule ' + escapeHtml(app.status || '') + ' appointment">🔒 Reschedule</button>';

                    html += 
                        '<tr>' +
                            '<td><strong>' + apptNo + '</strong></td>' +
                            '<td><strong>#' + app.tokenNumber + '</strong></td>' +
                            '<td>' + (app.appointmentDate || '') + '</td>' +
                            '<td>' + (app.displayTimeRange || '') + '</td>' +
                            '<td><strong>' + escapeHtml(app.patientName || '') + '</strong>' + alertTag + '</td>' +
                            '<td>' + escapeHtml(app.contactNumber || '') + '</td>' +
                            '<td>' + escapeHtml(app.dentistName || '') + '</td>' +
                            '<td><span class="badge badge-receptionist">' + escapeHtml(app.status || 'BOOKED') + '</span></td>' +
                            '<td>' +
                                '<a href="${pageContext.request.contextPath}/booking/ticket/' + app.appointmentId + '" target="_blank" class="btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; text-decoration: none;">🖨️ Print Ticket</a>' +
                                rescheduleBtnHtml +
                            '</td>' +
                        '</tr>';
                });
                tbody.innerHTML = html;

                if (table && typeof initTablePagination === 'function') {
                    initTablePagination(table, 10);
                }
            } else {
                console.error("API returned error status:", resp.status);
            }
        } catch (err) {
            console.error("Failed to load appointments by date:", err);
        }
    }

    function openRescheduleModal(appId, patientName, dateStr, dentistId) {
        document.getElementById('rescheduleAppId').value = appId;
        document.getElementById('rescheduleModalSubtitle').innerText = 'Rescheduling appointment for ' + patientName + ' (APT-#' + appId + ')';
        
        const dentistSelect = document.getElementById('rescheduleDentistSelect');
        if (dentistSelect && dentistId) {
            dentistSelect.value = dentistId;
        }

        const dateInput = document.getElementById('rescheduleDateInput');
        dateInput.value = dateStr || getLocalDateString();
        dateInput.min = getLocalDateString();

        document.getElementById('rescheduleSelectedStartTime').value = '';
        document.getElementById('rescheduleSelectedToken').value = '';
        document.getElementById('rescheduleFeedback').style.display = 'none';
        
        document.getElementById('rescheduleModal').style.display = 'flex';

        onRescheduleSelectionChange();
    }

    function closeRescheduleModal() {
        document.getElementById('rescheduleModal').style.display = 'none';
    }

    function onRescheduleSelectionChange() {
        const dentistId = document.getElementById('rescheduleDentistSelect').value;
        const dateVal = document.getElementById('rescheduleDateInput').value;
        const container = document.getElementById('rescheduleSlotsContainer');
        const feeNotice = document.getElementById('rescheduleFeeNotice');
        const feeText = document.getElementById('rescheduleFeeText');

        document.getElementById('rescheduleSelectedStartTime').value = '';
        document.getElementById('rescheduleSelectedToken').value = '';

        const dentistSelect = document.getElementById('rescheduleDentistSelect');
        if (dentistSelect && dentistSelect.selectedIndex >= 0) {
            const opt = dentistSelect.options[dentistSelect.selectedIndex];
            const fee = opt ? opt.getAttribute('data-fee') : null;
            if (fee && feeNotice && feeText) {
                feeText.innerText = 'LKR ' + parseFloat(fee).toFixed(2);
                feeNotice.style.display = 'block';
            }
        }

        if (!dentistId || !dateVal) {
            container.innerHTML = '<p style="color: var(--text-muted); font-size: 0.85rem;">Select dentist and date to view time slots.</p>';
            return;
        }

        container.innerHTML = '<p style="color: var(--primary); font-size: 0.85rem;">🔍 Querying live doctor shift availability...</p>';

        fetch('${pageContext.request.contextPath}/api/slots/available?dentistId=' + dentistId + '&date=' + dateVal)
            .then(res => {
                if(!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(slots => {
                if (!slots || slots.length === 0) {
                    container.innerHTML = '<p style="color: #991B1B; background-color: #FEE2E2; border: 1px solid #FECACA; padding: 0.5rem; border-radius: 4px; font-size: 0.82rem;">⚠️ Selected doctor is off-duty or has no shift scheduled on ' + dateVal + '.</p>';
                    return;
                }

                let html = '<div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(130px, 1fr)); gap: 0.5rem;">';
                slots.forEach(slot => {
                    const isAvailable = (slot.available !== undefined) ? slot.available : slot.isAvailable;
                    const timeText = slot.displayTime || slot.displayTimeRange || '';
                    const availableClass = isAvailable ? 'btn-primary' : 'btn-logout';
                    const disabledAttr = isAvailable ? '' : 'disabled style="opacity: 0.5; cursor: not-allowed; text-decoration: line-through;"';
                    
                    html += '<button type="button" class="' + availableClass + ' reschedule-slot-btn" ' + disabledAttr +
                            ' style="font-size: 0.8rem; font-weight: 700; padding: 0.5rem 0.3rem; width: 100%;" ' +
                            ' onclick="selectRescheduleSlot(this, \'' + slot.startTime + '\', ' + slot.tokenNumber + ')">' +
                            'Token #' + slot.tokenNumber +
                            '</button>';
                });
                html += '</div>';
                container.innerHTML = html;
            })
            .catch(err => {
                container.innerHTML = '<p style="color: var(--accent-red); font-size: 0.85rem;">Unable to load time slots (' + err.message + ').</p>';
            });
    }

    function selectRescheduleSlot(btn, startTime, tokenNumber) {
        document.querySelectorAll('.reschedule-slot-btn').forEach(b => {
            b.style.border = 'none';
            b.style.boxShadow = 'none';
        });
        btn.style.border = '2px solid #047857';
        btn.style.boxShadow = '0 0 0 3px rgba(16, 185, 129, 0.3)';

        document.getElementById('rescheduleSelectedStartTime').value = startTime;
        document.getElementById('rescheduleSelectedToken').value = tokenNumber;
        document.getElementById('rescheduleFeedback').style.display = 'none';
    }

    async function submitReschedule() {
        const appId = document.getElementById('rescheduleAppId').value;
        const dentistId = document.getElementById('rescheduleDentistSelect').value;
        const newDate = document.getElementById('rescheduleDateInput').value;
        const startTime = document.getElementById('rescheduleSelectedStartTime').value;
        const tokenNumber = document.getElementById('rescheduleSelectedToken').value;
        const feedback = document.getElementById('rescheduleFeedback');

        if (!newDate || !startTime) {
            feedback.innerText = "⚠️ Please select a date and click an available time slot pill above.";
            feedback.style.display = 'block';
            return;
        }

        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/appointments/' + appId + '/reschedule?dentistId=' + dentistId + '&date=' + newDate + '&startTime=' + startTime + '&tokenNumber=' + tokenNumber, {
                method: 'POST'
            });
            if (resp.ok) {
                const updatedTicket = await resp.json();
                closeRescheduleModal();
                showToast('✅ Appointment APT-#' + appId + ' successfully rescheduled to ' + (updatedTicket.appointmentDate || newDate) + ' (Token #' + (updatedTicket.tokenNumber || tokenNumber) + ') with ' + (updatedTicket.dentistName || 'Doctor') + '!', 'success', 5000);
                const currentDate = document.getElementById('confirmedAppDatePicker').value;
                loadConfirmedAppointmentsByDate(currentDate || getLocalDateString());
            } else {
                const errText = await resp.text();
                feedback.innerText = "⚠️ Unable to reschedule (" + (errText || "Slot no longer available") + ").";
                feedback.style.display = 'block';
            }
        } catch (e) {
            feedback.innerText = "⚠️ Server connection error.";
            feedback.style.display = 'block';
        }
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
</script>
