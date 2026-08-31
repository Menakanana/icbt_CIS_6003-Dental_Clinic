<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- TAB 1: APPOINTMENT BOOKING ENGINE COMPONENT -->
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
                    <label>Select Treatment Procedure</label>
                    <select name="treatmentTypeId" class="form-control">
                        <option value="">-- General Consultation (Default) --</option>
                        <c:forEach items="${treatmentTypes}" var="tt">
                            <option value="${tt.treatmentTypeId}"><c:out value="${tt.treatmentName}"/> (LKR <fmt:formatNumber value="${tt.baseCost}" type="currency" currencySymbol=""/>)</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Notes / Patient Complaint</label>
                    <textarea name="notes" class="form-control" rows="2" placeholder="e.g. Toothache upper right molar"></textarea>
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
                        <th>Action</th>
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
                            <td>
                                <a href="${pageContext.request.contextPath}/booking/ticket/${app.appointmentId}" target="_blank" class="btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; text-decoration: none;">🖨️ Print Ticket</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty todayAppointments}">
                        <tr>
                            <td colspan="7" style="text-align: center; color: var(--text-muted);">No appointments booked for today yet.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
