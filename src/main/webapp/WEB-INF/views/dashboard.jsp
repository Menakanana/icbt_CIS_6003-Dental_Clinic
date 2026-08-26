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
    <script>
        function switchTab(tabId, btn) {
            document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            const target = document.getElementById(tabId);
            if (target) {
                target.classList.add('active');
                target.querySelectorAll('table.data-table').forEach(table => {
                    if (typeof table.updatePagination === 'function') {
                        table.updatePagination();
                    }
                });
            }
            if (btn) btn.classList.add('active');
        }
    </script>
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

    <!-- UI Dialogs Component (Toasts & Custom Modals) -->
    <jsp:include page="components/ui-dialogs.jsp"/>

</div>

<!-- Global Client-Side Scripts -->
<script>
    // Initialize default dates & apply past/future date restrictions
    const dNow = new Date();
    const todayStr = dNow.getFullYear() + '-' + String(dNow.getMonth() + 1).padStart(2, '0') + '-' + String(dNow.getDate()).padStart(2, '0');

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

    function toggleBookingPaymentMethod() {
        const payOpt = document.getElementById('initialPaymentOption');
        const group = document.getElementById('bookingPaymentMethodGroup');
        if (group && payOpt) {
            group.style.display = (payOpt.value === 'PAY_NOW') ? 'block' : 'none';
        }
        updateBookingAmountSummary();
    }

    function updateBookingAmountSummary() {
        const dentistSelect = document.getElementById("dentistSelect");
        const treatmentSelect = document.getElementById("bookingTreatmentSelect");
        const payOpt = document.getElementById("initialPaymentOption");

        let consultFee = 0;
        if (dentistSelect && dentistSelect.selectedIndex > 0) {
            const opt = dentistSelect.options[dentistSelect.selectedIndex];
            consultFee = parseFloat(opt.getAttribute("data-fee")) || 0;
        }

        const clinicCharge = 500.00;

        let procCost = 0;
        let procName = "";
        if (treatmentSelect && treatmentSelect.selectedIndex > 0) {
            const tOpt = treatmentSelect.options[treatmentSelect.selectedIndex];
            procCost = parseFloat(tOpt.getAttribute("data-cost")) || 0;
            procName = tOpt.getAttribute("data-name") || "Procedure";
        }

        const consultElem = document.getElementById("summaryConsultationFee");
        if (consultElem) consultElem.innerText = "LKR " + consultFee.toFixed(2);

        const treatRow = document.getElementById("summaryTreatmentRow");
        const treatNameElem = document.getElementById("summaryTreatmentName");
        const treatFeeElem = document.getElementById("summaryTreatmentFee");
        if (treatRow) {
            if (procCost > 0) {
                if (treatNameElem) treatNameElem.innerText = procName + ":";
                if (treatFeeElem) treatFeeElem.innerText = "LKR " + procCost.toFixed(2);
                treatRow.style.display = "flex";
            } else {
                treatRow.style.display = "none";
            }
        }

        const totalDepositElem = document.getElementById("summaryTotalDeposit");
        const summaryBox = document.getElementById("bookingAmountSummaryBox");

        if (payOpt && payOpt.value === "PAY_NOW") {
            const total = consultFee + clinicCharge + procCost;
            if (totalDepositElem) totalDepositElem.innerText = "LKR " + total.toFixed(2);
            if (summaryBox) {
                summaryBox.style.backgroundColor = "#ECFDF5";
                summaryBox.style.borderColor = "#059669";
                const title = summaryBox.querySelector('div');
                if (title) title.style.color = "#065F46";
            }
        } else {
            if (totalDepositElem) totalDepositElem.innerText = "LKR 0.00 (Pay Later)";
            if (summaryBox) {
                summaryBox.style.backgroundColor = "#FFFBEB";
                summaryBox.style.borderColor = "#F59E0B";
                const title = summaryBox.querySelector('div');
                if (title) title.style.color = "#D97706";
            }
        }
    }

    document.addEventListener("DOMContentLoaded", function() {
        updateBookingAmountSummary();
    });

    function onPhoneInputChanged() {
        const phoneVal = document.getElementById('phoneSearchInput') ? document.getElementById('phoneSearchInput').value.trim() : '';
        const quickPhone = document.getElementById('quickPhone');
        if (quickPhone && (!quickPhone.value || quickPhone.dataset.autoSynced === 'true')) {
            quickPhone.value = phoneVal;
            quickPhone.dataset.autoSynced = 'true';
        }
        searchPatientsByPhoneUI();
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

        const apiUrl = '${pageContext.request.contextPath}/api/slots/available?dentistId=' + encodeURIComponent(dentistId) + '&date=' + encodeURIComponent(date);

        fetch(apiUrl)
            .then(res => {
                if(!res.ok) {
                    throw new Error('Server returned HTTP status ' + res.status);
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
                        html += '<button type="button" class="slot-btn available" onclick="selectSlot(\'' + s.startTime + '\', \'' + s.displayTime + '\', ' + s.tokenNumber + ', this)">' +
                                'Token #' + s.tokenNumber +
                                '</button>';
                    } else {
                        html += '<button type="button" class="slot-btn disabled" disabled title="Booked">' +
                                'Token #' + s.tokenNumber +
                                '</button>';
                    }
                });
                container.innerHTML = html;
            })
            .catch(err => {
                console.error("Error fetching slots:", err);
                container.innerHTML = '<p style="color: var(--accent-red); font-size: 0.85rem; grid-column: 1/-1;">Unable to load slots (' + err.message + ').</p>';
            });
    }

    function selectSlot(startTime, displayTime, tokenNumber, btn) {
        document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
        btn.classList.add('selected');

        document.getElementById('formStartTime').value = startTime;
        document.getElementById('formTokenNumber').value = tokenNumber;

        document.getElementById('summarySlot').innerText = displayTime;
        document.getElementById('summaryToken').innerText = '#' + tokenNumber;

        // Clear slot error message if present
        const slotErr = document.getElementById('slotErrorMsg');
        if (slotErr) {
            slotErr.innerText = '';
            slotErr.style.display = 'none';
        }

        // Sync Form Hidden inputs
        document.getElementById('formDentistId').value = document.getElementById('dentistSelect').value;
        document.getElementById('formDate').value = document.getElementById('bookingDate').value;
        document.getElementById('formPatientId').value = document.getElementById('patientSelect').value;
        document.getElementById('formQuickName').value = document.getElementById('quickName').value;
        document.getElementById('formQuickPhone').value = document.getElementById('quickPhone').value;
        document.getElementById('formQuickNic').value = document.getElementById('quickNic').value;
        if (document.getElementById('formQuickEmail') && document.getElementById('quickEmail')) {
            document.getElementById('formQuickEmail').value = document.getElementById('quickEmail').value;
        }
    }

    function showFieldError(inputEl, errorEl, message) {
        if (inputEl) {
            inputEl.classList.add('is-invalid');
            inputEl.classList.remove('is-valid');
        }
        if (errorEl) {
            errorEl.innerText = message;
            errorEl.style.display = 'block';
        }
    }

    function clearFieldError(inputEl, errorEl) {
        if (inputEl) {
            inputEl.classList.remove('is-invalid');
            if (inputEl.value && inputEl.value.trim() !== '') {
                inputEl.classList.add('is-valid');
            } else {
                inputEl.classList.remove('is-valid');
            }
        }
        if (errorEl) {
            errorEl.innerText = '';
            errorEl.style.display = 'none';
        }
    }

    async function searchPatientsByPhoneUI() {
        const phoneInput = document.getElementById('phoneSearchInput');
        const container = document.getElementById('patientCardsContainer');
        if (!phoneInput || !container) return;

        const phoneVal = phoneInput.value.trim();
        if (!phoneVal) {
            container.innerHTML = '<p style="color: var(--text-muted); font-size: 0.82rem; margin: 0.4rem 0;">Enter a phone number above to search existing patients.</p>';
            return;
        }

        container.innerHTML = '<p style="color: var(--primary); font-size: 0.82rem; margin: 0.4rem 0;">Searching registered patients...</p>';

        try {
            const response = await fetch('${pageContext.request.contextPath}/api/patients/search?phone=' + encodeURIComponent(phoneVal));
            if (response.ok) {
                const list = await response.json();
                if (!list || list.length === 0) {
                    container.innerHTML = '<p style="color: var(--text-muted); font-size: 0.82rem; margin: 0.4rem 0;">No existing patients registered under <strong>' + escapeHtml(phoneVal) + '</strong>. Fill the Quick Add form below to register a new patient under this number.</p>';
                    return;
                }

                let html = '';
                list.forEach(p => {
                    const nicInfo = p.nic ? ' • NIC: ' + escapeHtml(p.nic) : '';
                    const relInfo = p.relationship ? ' • <span style="background:#E0F2FE; color:#0369A1; padding:0.1rem 0.35rem; font-size:0.7rem; border-radius:4px; font-weight:600;">' + escapeHtml(p.relationship) + '</span>' : '';
                    const medicalAlert = p.medicalHistory ? '<br><span style="color:#B91C1C; font-size:0.73rem;">⚠️ Alert: ' + escapeHtml(p.medicalHistory) + '</span>' : '';
                    const safeName = (p.patientName || '').replace(/'/g, "\\'");
                    
                    html += '<div class="patient-card-item" id="pCard_' + p.patientId + '" onclick="selectPatientCard(' + p.patientId + ', \'' + safeName + '\', \'' + escapeHtml(p.contactNumber) + '\')"' +
                            ' style="background:#FFF; border:1px solid var(--border-color); border-radius:var(--radius-sm); padding:0.5rem 0.75rem; margin-bottom:0.4rem; cursor:pointer; transition:all 0.15s ease;">' +
                                '<div style="display:flex; justify-content:space-between; align-items:center;">' +
                                    '<strong style="color:var(--primary); font-size:0.88rem;">' + escapeHtml(p.patientName) + '</strong>' +
                                    '<span style="font-size:0.75rem; color:var(--text-muted);">' + escapeHtml(p.contactNumber) + '</span>' +
                                '</div>' +
                                '<div style="font-size:0.78rem; color:var(--text-muted); margin-top:0.15rem;">' +
                                    (p.gender ? 'Gender: ' + escapeHtml(p.gender) : '') + relInfo + nicInfo + medicalAlert +
                                '</div>' +
                            '</div>';
                });
                container.innerHTML = html;

                // Re-highlight currently selected patient card if matches
                const currentId = document.getElementById('patientSelect') ? document.getElementById('patientSelect').value : '';
                if (currentId) {
                    const card = document.getElementById('pCard_' + currentId);
                    if (card) {
                        card.style.borderColor = 'var(--primary)';
                        card.style.backgroundColor = '#E6FFFA';
                        card.style.boxShadow = '0 0 0 2px var(--primary)';
                    }
                }
            } else {
                container.innerHTML = '<p style="color: var(--accent-red); font-size: 0.82rem;">Error querying patients.</p>';
            }
        } catch (e) {
            console.error("Failed patient phone search:", e);
            container.innerHTML = '<p style="color: var(--accent-red); font-size: 0.82rem;">Connection error querying patients.</p>';
        }
    }

    function selectPatientCard(patientId, patientName, contactNumber) {
        document.querySelectorAll('.patient-card-item').forEach(c => {
            c.style.borderColor = 'var(--border-color)';
            c.style.backgroundColor = '#FFF';
            c.style.boxShadow = 'none';
        });

        const selectedCard = document.getElementById('pCard_' + patientId);
        if (selectedCard) {
            selectedCard.style.borderColor = 'var(--primary)';
            selectedCard.style.backgroundColor = '#E6FFFA';
            selectedCard.style.boxShadow = '0 0 0 2px var(--primary)';
        }

        const patientSelect = document.getElementById('patientSelect');
        const formPatientId = document.getElementById('formPatientId');
        if (patientSelect) patientSelect.value = patientId;
        if (formPatientId) formPatientId.value = patientId;

        // Clear quick add fields when selecting an existing patient
        const quickName = document.getElementById('quickName');
        const quickNic = document.getElementById('quickNic');
        if (quickName) quickName.value = '';
        if (quickNic) quickNic.value = '';

        validatePatientSelection();
    }

    function onQuickAddInput() {
        const quickName = document.getElementById('quickName');
        const quickPhone = document.getElementById('quickPhone');
        if (quickPhone) quickPhone.dataset.autoSynced = 'false';

        if (quickName && quickName.value.trim() !== '') {
            // Deselect any selected patient cards
            document.querySelectorAll('.patient-card-item').forEach(c => {
                c.style.borderColor = 'var(--border-color)';
                c.style.backgroundColor = '#FFF';
                c.style.boxShadow = 'none';
            });
            const patientSelect = document.getElementById('patientSelect');
            const formPatientId = document.getElementById('formPatientId');
            if (patientSelect) patientSelect.value = '';
            if (formPatientId) formPatientId.value = '';
        }
        validatePatientSelection();
    }

    function validatePatientSelection() {
        const patientId = document.getElementById('patientSelect') ? document.getElementById('patientSelect').value : '';
        const quickName = document.getElementById('quickName') ? document.getElementById('quickName').value.trim() : '';
        const patientErr = document.getElementById('patientErrorMsg');
        const quickNameErr = document.getElementById('quickNameErrorMsg');
        const quickNameInput = document.getElementById('quickName');

        if (!patientId && !quickName) {
            showFieldError(quickNameInput, quickNameErr, "⚠️ Search & select an existing patient above or enter full name under Quick-Add.");
            return false;
        } else {
            if (patientErr) {
                patientErr.innerText = '';
                patientErr.style.display = 'none';
            }
            clearFieldError(quickNameInput, quickNameErr);
            return true;
        }
    }

    function validateQuickPhone() {
        const quickPhoneInput = document.getElementById('quickPhone');
        const quickPhoneErr = document.getElementById('quickPhoneErrorMsg');
        if (!quickPhoneInput) return true;

        const val = quickPhoneInput.value.trim();
        const quickName = document.getElementById('quickName') ? document.getElementById('quickName').value.trim() : '';

        if (quickName && val) {
            const phoneRegex = /^(?:\+94|0)?7[0-9]{8}$/;
            if (!phoneRegex.test(val.replace(/\s+/g, ''))) {
                showFieldError(quickPhoneInput, quickPhoneErr, "⚠️ Enter a valid 10-digit mobile number (e.g. 0771234567).");
                return false;
            }
        }
        clearFieldError(quickPhoneInput, quickPhoneErr);
        return true;
    }

    function validateBookingForm() {
        let isValid = true;
        const patientId = document.getElementById('patientSelect') ? document.getElementById('patientSelect').value : '';
        const quickName = document.getElementById('quickName') ? document.getElementById('quickName').value.trim() : '';
        const startTime = document.getElementById('formStartTime') ? document.getElementById('formStartTime').value : '';

        const slotErr = document.getElementById('slotErrorMsg');

        if (!validatePatientSelection()) {
            isValid = false;
        }

        if (quickName && !validateQuickPhone()) {
            isValid = false;
        }

        if (!startTime) {
            if (slotErr) {
                slotErr.innerText = "⚠️ Please click an available green time slot button above to select your booking time.";
                slotErr.style.display = 'block';
            }
            isValid = false;
        } else {
            if (slotErr) {
                slotErr.innerText = '';
                slotErr.style.display = 'none';
            }
        }

        if (!isValid) return false;

        // Sync values right before submission
        if (document.getElementById('dentistSelect')) document.getElementById('formDentistId').value = document.getElementById('dentistSelect').value;
        if (document.getElementById('bookingDate')) document.getElementById('formDate').value = document.getElementById('bookingDate').value;
        if (document.getElementById('patientSelect')) document.getElementById('formPatientId').value = patientId;
        if (document.getElementById('quickName')) document.getElementById('formQuickName').value = quickName;
        if (document.getElementById('quickPhone')) document.getElementById('formQuickPhone').value = document.getElementById('quickPhone').value;
        if (document.getElementById('quickNic')) document.getElementById('formQuickNic').value = document.getElementById('quickNic').value;
        if (document.getElementById('quickEmail')) document.getElementById('formQuickEmail').value = document.getElementById('quickEmail').value;

        // Prevent double-clicking / duplicate form submission
        const submitBtn = document.querySelector('#bookingForm button[type="submit"]');
        if (submitBtn) {
            if (submitBtn.disabled) return false;
            submitBtn.disabled = true;
            submitBtn.innerText = '⏳ Processing Booking...';
        }

        return true;
    }

    function filterPatientDirectoryTable() {
        const query = document.getElementById('searchPatientDirectoryInput').value.toLowerCase();
        const table = document.getElementById('patientDirectoryTable');
        if (!table) return;
        const rows = table.querySelectorAll('tbody tr');
        rows.forEach(r => {
            const text = r.innerText.toLowerCase();
            r.dataset.searchHidden = text.includes(query) ? 'false' : 'true';
        });
        if (typeof table.updatePagination === 'function') {
            table.updatePagination();
        }
    }

    async function selectPatientForBooking(patientId) {
        switchTab('booking-tab', document.querySelectorAll('.tab-btn')[0]);
        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/patients/' + patientId);
            if (resp.ok) {
                const patient = await resp.json();
                if (patient && patient.contactNumber) {
                    const phoneInput = document.getElementById('phoneSearchInput');
                    if (phoneInput) phoneInput.value = patient.contactNumber;
                    await searchPatientsByPhoneUI();
                    selectPatientCard(patient.patientId, patient.patientName, patient.contactNumber);
                    return;
                }
            }
        } catch (e) {
            console.error("Error auto-selecting patient:", e);
        }
        if (document.getElementById('patientSelect')) document.getElementById('patientSelect').value = patientId;
        if (document.getElementById('formPatientId')) document.getElementById('formPatientId').value = patientId;
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

    function generateInitialReceipt(appointmentId) {
        const payMethod = document.getElementById('paymentMethod_' + appointmentId) ? document.getElementById('paymentMethod_' + appointmentId).value : 'Cash';
        window.location.href = '${pageContext.request.contextPath}/billing/receipt/' + appointmentId + '?stage=INITIAL_DEPOSIT&paymentMethod=' + encodeURIComponent(payMethod);
    }

    function generateFinalReceipt(appointmentId) {
        const discountInput = document.getElementById('discount_' + appointmentId);
        const discount = discountInput ? discountInput.value : 0;
        const treatmentSelect = document.getElementById('treatment_' + appointmentId);
        const treatmentTypeId = treatmentSelect ? treatmentSelect.value : '';
        const payMethod = document.getElementById('paymentMethod_' + appointmentId) ? document.getElementById('paymentMethod_' + appointmentId).value : 'Cash';

        let url = '${pageContext.request.contextPath}/billing/receipt/' + appointmentId + '?stage=FINAL_SETTLED&discount=' + discount + '&paymentMethod=' + encodeURIComponent(payMethod);
        if (treatmentTypeId) {
            url += '&treatmentTypeId=' + treatmentTypeId;
        }
        window.location.href = url;
    }

    function generateReceipt(appointmentId) {
        generateFinalReceipt(appointmentId);
    }
</script>

<script src="${pageContext.request.contextPath}/js/pagination.js"></script>

</body>
</html>
