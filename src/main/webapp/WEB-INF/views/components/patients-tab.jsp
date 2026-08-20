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

        <form action="${pageContext.request.contextPath}/patients/register" method="post" id="registrationForm" style="display: none;" onsubmit="return validatePatientRegistrationForm()">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem;">
                <div class="form-group">
                    <label>Patient Full Name *</label>
                    <input type="text" name="patientName" id="regPatientName" class="form-control" required placeholder="e.g. Kamani Perera" onblur="validateRegPatientName()" oninput="clearFieldError(this, document.getElementById('regPatientNameErrorMsg'))">
                    <span class="field-error-msg" id="regPatientNameErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Contact Phone Number *</label>
                    <input type="text" name="contactNumber" id="regContactNumber" class="form-control" required placeholder="e.g. 0771234567" onblur="validateRegContactNumber()" oninput="clearFieldError(this, document.getElementById('regContactNumberErrorMsg'))">
                    <span class="field-error-msg" id="regContactNumberErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>NIC Number</label>
                    <input type="text" name="nic" id="regNic" class="form-control" placeholder="e.g. 199256789102" onblur="validateRegNic()" oninput="clearFieldError(this, document.getElementById('regNicErrorMsg')); autoFillDobAndGenderFromNic('regNic', 'dobInput', 'gender');">
                    <span class="field-error-msg" id="regNicErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Gender</label>
                    <select name="gender" class="form-control">
                        <option value="M">Male</option>
                        <option value="F">Female</option>
                        <option value="O">Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Date of Birth</label>
                    <input type="date" name="dateOfBirth" id="dobInput" class="form-control" onblur="validateRegDob()" oninput="clearFieldError(this, document.getElementById('dobErrorMsg'))">
                    <span class="field-error-msg" id="dobErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Email Address</label>
                    <input type="email" name="email" id="regEmail" class="form-control" placeholder="e.g. kamani@example.com" onblur="validateRegEmail()" oninput="clearFieldError(this, document.getElementById('regEmailErrorMsg'))">
                    <span class="field-error-msg" id="regEmailErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Family Relationship Tag</label>
                    <select name="relationship" class="form-control">
                        <option value="Self">Self (Primary Patient)</option>
                        <option value="Child">Child (Son / Daughter)</option>
                        <option value="Spouse">Spouse (Husband / Wife)</option>
                        <option value="Parent">Parent (Mother / Father)</option>
                        <option value="Other">Other Family Member</option>
                    </select>
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label>Medical History & Critical Allergies (e.g. Penicillin, Diabetes, Hypertension)</label>
                    <input type="text" name="medicalHistory" class="form-control" placeholder="e.g. Penicillin Allergy, Diabetes Type 2">
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
                        <tr data-phone="${p.contactNumber}">
                            <td>#<c:out value="${p.patientId}"/></td>
                            <td>
                                <strong><c:out value="${p.patientName}"/></strong>
                                <c:if test="${not empty p.relationship && p.relationship != 'Self'}">
                                    <span class="badge badge-receptionist" style="font-size: 0.7rem; margin-left: 0.3rem;"><c:out value="${p.relationship}"/></span>
                                </c:if>
                                <c:if test="${empty p.relationship || p.relationship == 'Self'}">
                                    <span class="badge badge-admin" style="font-size: 0.7rem; margin-left: 0.3rem; background-color: #065F46; color: #ECFDF5;">🔑 Primary</span>
                                </c:if>
                                <c:if test="${not empty p.medicalHistory}">
                                    <br><span style="display: inline-block; margin-top: 0.2rem; padding: 0.15rem 0.4rem; background-color: #FEE2E2; color: #991B1B; border: 1px solid #FECACA; border-radius: 4px; font-size: 0.72rem; font-weight: 600;">⚠️ Alert: <c:out value="${p.medicalHistory}"/></span>
                                </c:if>
                            </td>
                            <td><c:out value="${p.nic != null && !empty p.nic ? p.nic : 'N/A'}"/></td>
                            <td><c:out value="${p.contactNumber}"/></td>
                            <td><c:out value="${p.address != null && !empty p.address ? p.address : 'N/A'}"/></td>
                            <td>
                                <button type="button" class="btn-logout" onclick="selectPatientForBooking(${p.patientId})">Book Slot</button>
                                <button type="button" class="btn-primary" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; margin-left: 0.4rem;" onclick="openPatientHistoryModal(${p.patientId}, '${p.patientName}')">📁 History</button>
                                <button type="button" class="btn-logout" style="padding: 0.25rem 0.6rem; font-size: 0.75rem; margin-left: 0.4rem; background-color: #EFF6FF; color: #1D4ED8; border: 1px solid #BFDBFE;" onclick="prefillFamilyMemberRegistration('${p.contactNumber}', '${p.address}')">+ Family</button>
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
    function validateRegPatientName() {
        const input = document.getElementById('regPatientName');
        const err = document.getElementById('regPatientNameErrorMsg');
        if (!input) return true;
        if (!input.value.trim()) {
            showFieldError(input, err, "⚠️ Patient full name is required.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validateRegContactNumber() {
        const input = document.getElementById('regContactNumber');
        const err = document.getElementById('regContactNumberErrorMsg');
        if (!input) return true;
        const val = input.value.trim();
        if (!val) {
            showFieldError(input, err, "⚠️ Contact phone number is required.");
            return false;
        }
        const phoneRegex = /^(?:\+94|0)?[0-9]{9,10}$/;
        if (!phoneRegex.test(val.replace(/\s+/g, ''))) {
            showFieldError(input, err, "⚠️ Enter a valid 10-digit phone number (e.g. 0771234567).");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function parseSriLankanNIC(nicStr) {
        if (!nicStr) return null;
        const clean = nicStr.trim();
        let year = 0;
        let dayCode = 0;

        if (/^[0-9]{9}[vVxX]$/.test(clean)) {
            year = parseInt("19" + clean.substring(0, 2), 10);
            dayCode = parseInt(clean.substring(2, 5), 10);
        } else if (/^[0-9]{12}$/.test(clean)) {
            year = parseInt(clean.substring(0, 4), 10);
            dayCode = parseInt(clean.substring(4, 7), 10);
        } else {
            return null;
        }

        let gender = "M";
        if (dayCode > 500) {
            gender = "F";
            dayCode -= 500;
        }

        if (dayCode < 1 || dayCode > 366) return null;

        const monthDays = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        let month = 0;
        let day = dayCode;

        for (let i = 0; i < monthDays.length; i++) {
            if (day <= monthDays[i]) {
                month = i + 1;
                break;
            }
            day -= monthDays[i];
        }

        if (month < 1 || month > 12) return null;

        const formattedMonth = month < 10 ? '0' + month : '' + month;
        const formattedDay = day < 10 ? '0' + day : '' + day;
        const dob = year + '-' + formattedMonth + '-' + formattedDay;

        return { year: year, dob: dob, gender: gender };
    }

    function autoFillDobAndGenderFromNic(nicInputId, dobInputId, genderSelectName) {
        const input = document.getElementById(nicInputId);
        if (!input) return;
        const parsed = parseSriLankanNIC(input.value);
        if (parsed) {
            const dobInput = document.getElementById(dobInputId);
            if (dobInput && !dobInput.value) {
                dobInput.value = parsed.dob;
                const err = document.getElementById('dobErrorMsg');
                if (err) clearFieldError(dobInput, err);
            }
            const genderSelect = document.querySelector('select[name="' + genderSelectName + '"]');
            if (genderSelect) {
                genderSelect.value = parsed.gender;
            }
        }
    }

    function prefillFamilyMemberRegistration(phone, address) {
        const regForm = document.getElementById('registrationForm');
        const toggleBtn = document.getElementById('toggleRegBtn');
        if (regForm && regForm.style.display === 'none') {
            regForm.style.display = 'block';
            if (toggleBtn) toggleBtn.innerText = '- Hide Registration Form';
        }
        const phoneInput = document.getElementById('regContactNumber');
        const addressInput = document.querySelector('#registrationForm input[name="address"]');
        const relationshipSelect = document.querySelector('#registrationForm select[name="relationship"]');

        if (phoneInput) phoneInput.value = phone || '';
        if (addressInput) addressInput.value = address || '';
        if (relationshipSelect) relationshipSelect.value = 'Child';

        if (regForm) regForm.scrollIntoView({ behavior: 'smooth' });
    }

    function validateRegNic() {
        const input = document.getElementById('regNic');
        const err = document.getElementById('regNicErrorMsg');
        if (!input) return true;
        const val = input.value.trim();
        if (val) {
            const nicRegex = /^(?:[0-9]{9}[vVxX]|[0-9]{12})$/;
            if (!nicRegex.test(val)) {
                showFieldError(input, err, "⚠️ Enter valid NIC (9 digits + V/X or 12 digits).");
                return false;
            }
            autoFillDobAndGenderFromNic('regNic', 'dobInput', 'gender');
        }
        clearFieldError(input, err);
        return true;
    }

    function validateRegDob() {
        const input = document.getElementById('dobInput');
        const err = document.getElementById('dobErrorMsg');
        if (!input) return true;
        const val = input.value;
        if (val) {
            const selectedDate = new Date(val);
            const today = new Date();
            today.setHours(23, 59, 59, 999);
            if (selectedDate > today) {
                showFieldError(input, err, "⚠️ Date of birth cannot be in the future.");
                return false;
            }
        }
        clearFieldError(input, err);
        return true;
    }

    function validateRegEmail() {
        const input = document.getElementById('regEmail');
        const err = document.getElementById('regEmailErrorMsg');
        if (!input) return true;
        const val = input.value.trim();
        if (val) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(val)) {
                showFieldError(input, err, "⚠️ Enter a valid email address.");
                return false;
            }
        }
        clearFieldError(input, err);
        return true;
    }

    function validatePatientRegistrationForm() {
        let valid = true;
        if (!validateRegPatientName()) valid = false;
        if (!validateRegContactNumber()) valid = false;
        if (!validateRegNic()) valid = false;
        if (!validateRegDob()) valid = false;
        if (!validateRegEmail()) valid = false;
        return valid;
    }

    function openPatientHistoryModal(patientId, patientName) {
        document.getElementById('historyPatientTitle').innerText = 'Patient: ' + patientName + ' (ID #' + patientId + ')';
        const modal = document.getElementById('patientHistoryModal');
        modal.style.display = 'flex';

        const container = document.getElementById('historyTimelineContainer');
        container.innerHTML = '<p style="text-align: center; color: var(--primary); padding: 2rem;">🔍 Fetching dental visit records...</p>';

        fetch('${pageContext.request.contextPath}/api/patients/' + patientId + '/history')
            .then(res => {
                if(!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(historyList => {
                if(!historyList || historyList.length === 0) {
                    container.innerHTML = 
                        '<div style="text-align: center; padding: 2.5rem; color: var(--text-muted);">' +
                            '<p style="font-size: 1.1rem; margin-bottom: 0.5rem;">📁 No Previous Visit Records</p>' +
                            '<p style="font-size: 0.85rem;">This patient has not completed any clinic visits yet.</p>' +
                        '</div>';
                    return;
                }

                let html = '<div style="display: flex; flex-direction: column; gap: 1rem;">';
                historyList.forEach(item => {
                    const statusClass = item.status === 'COMPLETED' ? 'badge-admin' : 'badge-receptionist';
                    const treatment = item.treatmentName || 'General Consultation';
                    const notes = item.notes ? item.notes : 'No doctor notes logged for this visit.';
                    
                    html += 
                        '<div style="background: #F8FAFC; border-left: 4px solid var(--primary); border-radius: 6px; padding: 1.25rem; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">' +
                            '<div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">' +
                                '<div>' +
                                    '<span style="font-size: 0.8rem; font-weight: 700; color: var(--primary); text-transform: uppercase;">Visit Date: ' + item.appointmentDate + '</span>' +
                                    '<h4 style="margin: 0.2rem 0; color: #1E293B; font-size: 1.05rem;">🦷 Procedure: ' + treatment + '</h4>' +
                                '</div>' +
                                '<div style="text-align: right;">' +
                                    '<span class="badge ' + statusClass + '">' + item.status + '</span>' +
                                    '<div style="font-size: 0.75rem; color: #64748B; margin-top: 0.2rem;">APT-#' + item.appointmentId + ' | Token #' + item.tokenNumber + '</div>' +
                                '</div>' +
                            '</div>' +
                            '<div style="font-size: 0.85rem; color: #334155; margin-bottom: 0.75rem;">' +
                                '<strong>Attending Dentist:</strong> ' + item.dentistName + ' (' + item.specialization + ') | <strong>Time:</strong> ' + item.displayTimeRange +
                            '</div>' +
                            '<div style="background: #FFFFFF; border: 1px solid #E2E8F0; padding: 0.75rem; border-radius: 6px; font-size: 0.85rem; color: #475569; margin-bottom: 0.75rem;">' +
                                '<strong>📝 Doctor Clinical Notes / Complaint:</strong><br>' + notes +
                            '</div>' +
                            '<div style="display: flex; justify-content: space-between; font-size: 0.85rem; font-weight: 600; color: var(--primary);">' +
                                '<span>Consultation Fee: LKR ' + (item.consultationFee ? item.consultationFee.toFixed(2) : '0.00') + '</span>' +
                                '<a href="${pageContext.request.contextPath}/booking/ticket/' + item.appointmentId + '" target="_blank" style="color: var(--primary); text-decoration: underline;">🖨️ View Ticket</a>' +
                            '</div>' +
                        '</div>';
                });
                html += '</div>';
                container.innerHTML = html;
            })
            .catch(err => {
                container.innerHTML = '<p style="color: var(--accent-red); text-align: center; padding: 2rem;">Unable to load patient history (' + err.message + ').</p>';
            });
    }

    function closePatientHistoryModal() {
        document.getElementById('patientHistoryModal').style.display = 'none';
    }
</script>
