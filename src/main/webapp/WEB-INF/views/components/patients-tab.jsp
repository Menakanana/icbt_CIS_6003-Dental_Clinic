<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- TAB 2: PATIENT DIRECTORY COMPONENT -->
<div id="patients-tab" class="tab-content">
    <!-- Patient Registration Form Panel -->
    <div class="card-panel" style="margin-bottom: 1.5rem; background-color: #F8FAFC; border: 1px solid var(--border-color);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
            <h3 style="color: var(--primary); font-size: 1.1rem;">👤 Register New Patient Record</h3>
            <button type="button" class="btn-logout" onclick="toggleRegistrationForm()" id="toggleRegBtn">+ Show Registration Form</button>
        </div>

        <form action="${pageContext.request.contextPath}/patients/register" method="post" id="registrationForm" style="display: none;">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem;">
                <div class="form-group">
                    <label>Patient Full Name *</label>
                    <input type="text" name="patientName" class="form-control" required placeholder="e.g. Kamani Perera">
                </div>
                <div class="form-group">
                    <label>Contact Phone Number *</label>
                    <input type="text" name="contactNumber" class="form-control" required placeholder="e.g. 0771234567">
                </div>
                <div class="form-group">
                    <label>NIC Number</label>
                    <input type="text" name="nic" class="form-control" placeholder="e.g. 199256789102">
                </div>
                <div class="form-group">
                    <label>Gender</label>
                    <select name="gender" class="form-control">
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Date of Birth</label>
                    <input type="date" name="dateOfBirth" id="dobInput" class="form-control">
                </div>
                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email" name="email" class="form-control" placeholder="e.g. kamani@example.com">
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label>Home Address</label>
                    <input type="text" name="address" class="form-control" placeholder="e.g. 45 Galle Road, Colombo 03">
                </div>
            </div>
            <div style="margin-top: 1rem; text-align: right;">
                <button type="submit" class="btn-primary" style="padding: 0.6rem 1.5rem;">Save & Register Patient</button>
            </div>
        </form>
    </div>

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
                                <button type="button" class="btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; margin-left: 0.4rem;" onclick="openPatientHistoryModal(${p.patientId}, '${p.patientName}')">📁 History</button>
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

<!-- Patient Dental History Modal Component -->
<div id="patientHistoryModal" class="modal-overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; justify-content: center; align-items: center;">
    <div class="modal-content" style="background: #FFFFFF; border-radius: 12px; padding: 2rem; max-width: 750px; width: 90%; max-height: 85vh; overflow-y: auto; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid var(--primary); padding-bottom: 0.75rem; margin-bottom: 1rem;">
            <h3 style="color: var(--primary); margin: 0; font-size: 1.25rem;">📁 Patient Dental & Visit History Log</h3>
            <button type="button" class="btn-logout" onclick="closePatientHistoryModal()" style="padding: 0.3rem 0.75rem; font-weight: bold; cursor: pointer;">✕ Close</button>
        </div>

        <div id="historyPatientHeader" style="background: #ECFDF5; border: 1px solid #A7F3D0; padding: 1rem; border-radius: 8px; margin-bottom: 1.25rem;">
            <h4 id="historyPatientTitle" style="color: #065F46; margin: 0; font-size: 1.1rem;">Patient History Record</h4>
            <p style="font-size: 0.85rem; color: #047857; margin: 0.25rem 0 0 0;">Chronological timeline of all past appointments, treatments, dentist findings, and payments.</p>
        </div>

        <div id="historyTimelineContainer">
            <p style="text-align: center; color: var(--text-muted); padding: 2rem;">Loading patient treatment history...</p>
        </div>
    </div>
</div>

<script>
    function openPatientHistoryModal(patientId, patientName) {
        document.getElementById('historyPatientTitle').innerText = 'Patient: ' + patientName + ' (ID #' + patientId + ')';
        const modal = document.getElementById('patientHistoryModal');
        modal.style.display = 'flex';

        const container = document.getElementById('historyTimelineContainer');
        container.innerHTML = '<p style="text-align: center; color: var(--primary); padding: 2rem;">🔍 Fetching dental visit records...</p>';

        fetch(`${pageContext.request.contextPath}/api/patients/${patientId}/history`)
            .then(res => {
                if(!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(historyList => {
                if(!historyList || historyList.length === 0) {
                    container.innerHTML = `
                        <div style="text-align: center; padding: 2.5rem; color: var(--text-muted);">
                            <p style="font-size: 1.1rem; margin-bottom: 0.5rem;">📁 No Previous Visit Records</p>
                            <p style="font-size: 0.85rem;">This patient has not completed any clinic visits yet.</p>
                        </div>`;
                    return;
                }

                let html = '<div style="display: flex; flex-direction: column; gap: 1rem;">';
                historyList.forEach(item => {
                    const statusClass = item.status === 'COMPLETED' ? 'badge-admin' : 'badge-receptionist';
                    const treatment = item.treatmentName || 'General Consultation';
                    const notes = item.notes ? item.notes : 'No doctor notes logged for this visit.';
                    
                    html += `
                        <div style="background: #F8FAFC; border-left: 4px solid var(--primary); border-radius: 6px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
                            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
                                <div>
                                    <span style="font-size: 0.8rem; font-weight: 700; color: var(--primary); text-transform: uppercase;">Visit Date: ${item.appointmentDate}</span>
                                    <h4 style="margin: 0.2rem 0; color: #1E293B; font-size: 1.05rem;">🦷 Procedure: ${treatment}</h4>
                                </div>
                                <div style="text-align: right;">
                                    <span class="badge ${statusClass}">${item.status}</span>
                                    <div style="font-size: 0.75rem; color: #64748B; margin-top: 0.2rem;">APT-#${item.appointmentId} | Token #${item.tokenNumber}</div>
                                </div>
                            </div>
                            <div style="font-size: 0.85rem; color: #334155; margin-bottom: 0.75rem;">
                                <strong>Attending Dentist:</strong> ${item.dentistName} (${item.specialization}) | <strong>Time:</strong> ${item.displayTimeRange}
                            </div>
                            <div style="background: #FFFFFF; border: 1px solid #E2E8F0; padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; color: #475569; margin-bottom: 0.75rem;">
                                <strong>📝 Doctor Clinical Notes / Complaint:</strong><br>${notes}
                            </div>
                            <div style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600; color: var(--primary);">
                                <span>Consultation Fee: LKR ${item.consultationFee ? item.consultationFee.toFixed(2) : '0.00'}</span>
                                <a href="${pageContext.request.contextPath}/booking/ticket/${item.appointmentId}" target="_blank" style="color: var(--primary); text-decoration: underline;">🖨️ View Ticket</a>
                            </div>
                        </div>`;
                });
                html += '</div>';
                container.innerHTML = html;
            })
            .catch(err => {
                container.innerHTML = `<p style="color: var(--accent-red); text-align: center; padding: 2rem;">Unable to load patient history (${err.message}).</p>`;
            });
    }

    function closePatientHistoryModal() {
        document.getElementById('patientHistoryModal').style.display = 'none';
    }
</script>
