<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- DOCTOR PROFILES & DIRECTORY COMPONENT -->
<div id="doctors-tab" class="tab-content">
    
    <!-- Register / Configure Doctor Profile Form -->
    <div class="card-panel" style="margin-bottom: 1.5rem; background-color: #F8FAFC; border: 1px solid var(--border-color);">
        <h3 style="margin-bottom: 1rem; color: var(--primary); font-size: 1.1rem;">🩺 Register / Add New Doctor Profile</h3>
        
        <form action="${pageContext.request.contextPath}/dentists/save" method="post" id="doctorForm" onsubmit="return validateDoctorForm()">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem;">
                <div class="form-group">
                    <label>Doctor Full Name *</label>
                    <input type="text" name="dentistName" id="docName" class="form-control" required placeholder="e.g. Dr. Ruwan Jayasinghe" onblur="validateDocName()" oninput="clearFieldError(this, document.getElementById('docNameErrorMsg'))">
                    <span class="field-error-msg" id="docNameErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Specialization / Clinical Area *</label>
                    <input type="text" name="specialization" id="docSpec" class="form-control" required placeholder="e.g. Orthodontics & Restorative" onblur="validateDocSpec()" oninput="clearFieldError(this, document.getElementById('docSpecErrorMsg'))">
                    <span class="field-error-msg" id="docSpecErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Consultation Fee (LKR) *</label>
                    <input type="number" name="consultationFee" id="docFee" class="form-control" step="100" min="0.01" required placeholder="e.g. 2000.00" onblur="validateDocFee()" oninput="clearFieldError(this, document.getElementById('docFeeErrorMsg'))">
                    <span class="field-error-msg" id="docFeeErrorMsg" style="display:none;"></span>
                </div>
                <div class="form-group">
                    <label>Contact Phone Number *</label>
                    <input type="text" name="contactNumber" id="docPhone" class="form-control" required placeholder="e.g. 0773334455" onblur="validateDocPhone()" oninput="clearFieldError(this, document.getElementById('docPhoneErrorMsg'))">
                    <span class="field-error-msg" id="docPhoneErrorMsg" style="display:none;"></span>
                </div>
            </div>
            <div style="margin-top: 1rem; text-align: right;">
                <button type="submit" class="btn-primary" style="padding: 0.6rem 1.5rem;">Save Doctor Profile</button>
            </div>
        </form>
    </div>

    <!-- Active Doctor Profiles Directory Table -->
    <div class="card-panel">
        <h3 style="margin-bottom: 1rem; color: var(--primary);">Active Doctor Profiles Directory</h3>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Doctor ID</th>
                        <th>Full Name</th>
                        <th>Specialization</th>
                        <th>Contact Number</th>
                        <th>Consultation Fee</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${dentists}" var="d">
                        <tr>
                            <td>#<c:out value="${d.dentistId}"/></td>
                            <td><strong><c:out value="${d.dentistName}"/></strong></td>
                            <td><c:out value="${d.specialization}"/></td>
                            <td><c:out value="${d.contactNumber}"/></td>
                            <td>LKR <fmt:formatNumber value="${d.consultationFee}" type="currency" currencySymbol=""/></td>
                            <td><span class="badge badge-admin">Active</span></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty dentists}">
                        <tr>
                            <td colspan="6" style="text-align: center; color: var(--text-muted);">No doctors configured.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
    function validateDocName() {
        const input = document.getElementById('docName');
        const err = document.getElementById('docNameErrorMsg');
        if (!input) return true;
        if (!input.value.trim()) {
            showFieldError(input, err, "⚠️ Doctor full name is required.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validateDocSpec() {
        const input = document.getElementById('docSpec');
        const err = document.getElementById('docSpecErrorMsg');
        if (!input) return true;
        if (!input.value.trim()) {
            showFieldError(input, err, "⚠️ Specialization is required.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validateDocFee() {
        const input = document.getElementById('docFee');
        const err = document.getElementById('docFeeErrorMsg');
        if (!input) return true;
        const val = parseFloat(input.value);
        if (isNaN(val) || val <= 0) {
            showFieldError(input, err, "⚠️ Consultation fee must be greater than 0 LKR.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validateDocPhone() {
        const input = document.getElementById('docPhone');
        const err = document.getElementById('docPhoneErrorMsg');
        if (!input) return true;
        const val = input.value.trim();
        if (!val) {
            showFieldError(input, err, "⚠️ Contact phone number is required.");
            return false;
        }
        const phoneRegex = /^(?:\+94|0)?[0-9]{9,10}$/;
        if (!phoneRegex.test(val.replace(/\s+/g, ''))) {
            showFieldError(input, err, "⚠️ Enter a valid 10-digit phone number.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validateDoctorForm() {
        let valid = true;
        if (!validateDocName()) valid = false;
        if (!validateDocSpec()) valid = false;
        if (!validateDocFee()) valid = false;
        if (!validateDocPhone()) valid = false;
        return valid;
    }
</script>
