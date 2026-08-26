<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

            <!-- TAB 3: BILLING & INVOICES COMPONENT -->
            <div id="billing-tab" class="tab-content">
                <div class="card-panel">
                    <h3 style="margin-bottom: 0.5rem; color: var(--primary);">💳 Post-Channeling Payment Lookup &
                        Multi-Procedure Settlement</h3>
                    <p style="color: var(--text-muted); font-size: 0.88rem; margin-bottom: 1.5rem;">
                        Search appointment by <strong>Patient Name, Phone, NIC, or Appointment No. (e.g. 1014)</strong> to view details, dynamically add treatment procedures, apply discounts, and complete settlement.
                    </p>

                    <!-- 1. APPOINTMENT SEARCH & LOOKUP BAR -->
                    <div
                        style="background-color: #F8FAFC; padding: 1.25rem; border-radius: 8px; border: 1px solid var(--border-color); margin-bottom: 1.5rem;">
                        <div style="display: flex; gap: 0.75rem; align-items: flex-end; flex-wrap: wrap;">
                            <div style="flex: 1; min-width: 280px;">
                                <label
                                    style="font-size: 0.8rem; font-weight: 700; color: var(--primary); text-transform: uppercase; display: block; margin-bottom: 0.3rem;">Find
                                    Appointment (by Name, Phone, NIC, or Appointment No.)</label>
                                <input type="text" id="billingApptSearchInput" class="form-control"
                                    placeholder="Enter Patient Name, Phone, NIC, or Appointment No. (e.g. 1014)..."
                                    onkeypress="if(event.key==='Enter') lookupAppointmentUniversal();">
                            </div>
                            <div style="display: flex; gap: 0.5rem;">
                                <button type="button" class="btn-primary"
                                    style="padding: 0.6rem 1.25rem; font-weight: 600;"
                                    onclick="lookupAppointmentUniversal()">🔍 Search & Load Details</button>
                                <button type="button" class="btn-secondary"
                                    style="padding: 0.6rem 0.9rem; font-weight: 600; background: #64748B; border-color: #475569; color: white;"
                                    onclick="resetBillingSearchFilters()">🔄 Clear</button>
                            </div>
                        </div>
                        <div id="billingSearchError"
                            style="display: none; margin-top: 0.75rem; color: #991B1B; font-weight: 600; font-size: 0.85rem; background-color: #FEE2E2; padding: 0.5rem 0.75rem; border-radius: 4px; border: 1px solid #FECACA;">
                        </div>

                        <!-- SEARCH RESULTS SELECTION CONTAINER -->
                        <div id="billingSearchResultsContainer"
                            style="display: none; margin-top: 1rem; background: #FFFFFF; border: 1px solid #CBD5E1; border-radius: 8px; padding: 1rem;">
                            <h5 style="margin: 0 0 0.5rem 0; color: var(--primary); font-size: 0.9rem;">🔎 Matching
                                Appointment Records (<span id="billingSearchMatchCount">0</span>)</h5>
                            <div class="table-responsive">
                                <table class="data-table" style="font-size: 0.85rem;">
                                    <thead>
                                        <tr style="background: #F1F5F9;">
                                            <th>Appointment No.</th>
                                            <th>Date</th>
                                            <th>Patient Name</th>
                                            <th>Contact / NIC</th>
                                            <th>Dentist</th>
                                            <th>Deposit Status</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody id="billingSearchResultsTbody"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <!-- 2. INTERACTIVE BILLING SETTLEMENT CARD (ACTIVE WORKSPACE) -->
                    <div id="billingActiveCard"
                        style="display: none; background: #FFFFFF; border: 2px solid var(--primary); border-radius: 10px; padding: 1.5rem; margin-bottom: 2rem; box-shadow: 0 4px 12px rgba(0,0,0,0.05);">
                        <div
                            style="display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 2px solid #E2E8F0; padding-bottom: 1rem; margin-bottom: 1.25rem; flex-wrap: wrap; gap: 1rem;">
                            <div>
                                <h4 style="margin: 0; color: var(--primary); font-size: 1.2rem;">
                                    Appointment #<span id="activeApptNo"></span> &mdash; <span id="activePatientName"
                                        style="color: var(--text-main);"></span>
                                </h4>
                                <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.25rem;">
                                    <strong>📅 Date:</strong> <span id="activeAppointmentDate"
                                        style="font-weight: 700; color: var(--primary);"></span> |
                                    <strong>Phone:</strong> <span id="activePatientContact"></span> |
                                    <strong>Dentist:</strong> <span id="activeDentistName"></span> |
                                    <strong>Token #:</strong> #<span id="activeTokenNo"></span>
                                </div>
                                <div id="activeMedicalHistoryAlert"
                                    style="display: none; margin-top: 0.4rem; background-color: #FEE2E2; color: #991B1B; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.78rem; font-weight: 700; border: 1px solid #FECACA;">
                                    ⚠️ Medical Alert: <span id="activeMedicalHistoryText"></span>
                                </div>
                            </div>

                            <div id="activePaymentBadgeContainer">
                                <!-- Dynamic Badge: PAID DEPOSIT vs UNPAID -->
                            </div>
                        </div>

                        <!-- MULTI-PROCEDURE ADDER SECTION -->
                        <div
                            style="background-color: #F0F9FF; border: 1px solid #BAE6FD; border-radius: 8px; padding: 1.25rem; margin-bottom: 1.5rem;">
                            <h5 style="margin: 0 0 0.75rem 0; color: #0369A1; font-size: 0.95rem;">🩺 Add Treatment
                                Procedures (Post-Channeling)</h5>

                            <div
                                style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap; margin-bottom: 1rem;">
                                <select id="billingProcedureDropdown" class="form-control"
                                    style="flex: 1; min-width: 250px;">
                                    <option value="">-- Select Treatment Procedure to Add --</option>
                                    <c:forEach items="${treatmentTypes}" var="tt">
                                        <option value="${tt.treatmentTypeId}"
                                            data-name="<c:out value='${tt.treatmentName}'/>" data-cost="${tt.baseCost}">
                                            <c:out value="${tt.treatmentName}" /> (LKR
                                            <fmt:formatNumber value="${tt.baseCost}" type="currency"
                                                currencySymbol="" />)
                                        </option>
                                    </c:forEach>
                                </select>
                                <button type="button" class="btn-secondary"
                                    style="background-color: #0284C7; border-color: #0369A1; color: white; padding: 0.55rem 1rem; font-weight: 600;"
                                    onclick="addBillingProcedure()">➕ Add Procedure</button>
                            </div>

                            <!-- ADDED PROCEDURES LIST TABLE -->
                            <table class="data-table" style="background-color: #FFFFFF; font-size: 0.85rem;">
                                <thead>
                                    <tr style="background-color: #E0F2FE;">
                                        <th>Procedure Name</th>
                                        <th style="text-align: right; width: 140px;">Base Cost (LKR)</th>
                                        <th style="text-align: center; width: 80px;">Action</th>
                                    </tr>
                                </thead>
                                <tbody id="addedProceduresTbody">
                                    <tr>
                                        <td colspan="3"
                                            style="text-align: center; color: var(--text-muted); font-style: italic;">No
                                            extra procedures added yet. Select from the dropdown above to add treatment
                                            procedures.</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <!-- LIVE FINANCIAL BREAKDOWN & CALCULATION PANEL -->
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem;">
                            <div
                                style="background-color: #F8FAFC; padding: 1rem; border-radius: 8px; border: 1px solid var(--border-color);">
                                <h5
                                    style="margin: 0 0 0.75rem 0; color: var(--primary); font-size: 0.9rem; border-bottom: 1px solid #E2E8F0; padding-bottom: 0.4rem;">
                                    Billing Summary Breakdown</h5>

                                <div
                                    style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.4rem;">
                                    <span>Doctor Consultation Fee:</span>
                                    <strong>LKR <span id="summaryConsultFee">0.00</span></strong>
                                </div>
                                <div
                                    style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.4rem;">
                                    <span>Clinic Facility Charge:</span>
                                    <strong>LKR <span id="summaryClinicCharge">500.00</span></strong>
                                </div>
                                <div
                                    style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.4rem; color: #0284C7;">
                                    <span>Added Procedures Subtotal:</span>
                                    <strong>LKR <span id="summaryProceduresSubtotal">0.00</span></strong>
                                </div>
                                <div
                                    style="border-top: 1px dashed #CBD5E1; margin: 0.5rem 0; padding-top: 0.5rem; display: flex; justify-content: space-between; font-size: 0.9rem; font-weight: 700;">
                                    <span>Gross Bill Total:</span>
                                    <strong>LKR <span id="summaryGrossTotal">0.00</span></strong>
                                </div>
                            </div>

                            <div
                                style="background-color: #ECFDF5; padding: 1rem; border-radius: 8px; border: 1px solid #A7F3D0;">
                                <h5
                                    style="margin: 0 0 0.75rem 0; color: #065F46; font-size: 0.9rem; border-bottom: 1px solid #A7F3D0; padding-bottom: 0.4rem;">
                                    Payments & Net Settlement</h5>

                                <div
                                    style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.4rem; color: #0369A1;">
                                    <span>Less: Deposit Already Paid:</span>
                                    <strong>- LKR <span id="summaryPrevPaid">0.00</span></strong>
                                </div>

                                <div
                                    style="display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; margin-bottom: 0.6rem;">
                                    <label for="summaryDiscountInput" style="font-weight: 600;">Special Discount
                                        (LKR):</label>
                                    <input type="number" id="summaryDiscountInput" class="form-control"
                                        style="width: 110px; padding: 0.25rem 0.5rem; text-align: right;" value="0"
                                        min="0" step="50" oninput="recalculateBillingTotals()">
                                </div>

                                <div
                                    style="border-top: 2px solid #059669; margin-top: 0.6rem; padding-top: 0.6rem; display: flex; justify-content: space-between; font-size: 1.1rem; font-weight: 800; color: #065F46;">
                                    <span>NET BALANCE DUE:</span>
                                    <span>LKR <span id="summaryNetBalance">0.00</span></span>
                                </div>

                                <!-- EXPLICIT AMOUNT PAID & CHANGE RETURN SECTION -->
                                <div style="margin-top: 0.85rem; padding-top: 0.75rem; border-top: 1px dashed #A7F3D0;">
                                    <div
                                        style="display: flex; justify-content: space-between; align-items: center; font-size: 0.88rem; margin-bottom: 0.4rem;">
                                        <label for="summaryAmountPaidInput" style="font-weight: 700; color: #065F46;">💵
                                            Amount Paid by Patient (LKR):</label>
                                        <input type="number" id="summaryAmountPaidInput" class="form-control"
                                            style="width: 130px; padding: 0.3rem 0.5rem; text-align: right; font-weight: 700; color: #065F46; border: 2px solid #059669;"
                                            value="0" min="0" step="50" oninput="recalculateChangeReturn()">
                                    </div>

                                    <div
                                        style="display: flex; justify-content: space-between; align-items: center; font-size: 0.88rem; font-weight: 700; color: #0284C7;">
                                        <span>🔄 Change / Balance Return to Patient:</span>
                                        <span>LKR <span id="summaryChangeReturn">0.00</span></span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- SETTLEMENT FORM SUBMISSION -->
                        <form id="settlementSubmitForm" action="${pageContext.request.contextPath}/billing/receipt/0"
                            method="GET" target="_blank">
                            <input type="hidden" id="formDiscount" name="discount" value="0">
                            <input type="hidden" id="formPaymentMethod" name="paymentMethod" value="Cash">
                            <input type="hidden" name="settle" value="true">
                            <div id="formTreatmentTypeIdsContainer"></div>

                            <div
                                style="display: flex; justify-content: space-between; align-items: center; background-color: #F8FAFC; padding: 1rem; border-radius: 8px; border: 1px solid var(--border-color); flex-wrap: wrap; gap: 1rem;">
                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                    <label
                                        style="font-size: 0.85rem; font-weight: 700; color: var(--text-main);">Payment
                                        Method:</label>
                                    <select id="settlementPaymentMethodSelect" class="form-control"
                                        style="width: 180px;"
                                        onchange="document.getElementById('formPaymentMethod').value = this.value">
                                        <option value="Cash">💵 Cash</option>
                                        <option value="Credit/Debit Card">💳 Credit / Debit Card</option>
                                        <option value="Bank Transfer">📱 Bank / QR Transfer</option>
                                    </select>
                                </div>

                                <button type="button" class="btn-primary"
                                    style="padding: 0.75rem 1.75rem; font-size: 1rem; font-weight: 700; background-color: #059669; border-color: #047857;"
                                    onclick="submitFinalSettledInvoice()">
                                    💳 Complete Payment & Generate Final Receipt
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- 3. APPOINTMENTS PATIENT ROSTER TABLE -->
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; flex-wrap: wrap; gap: 0.75rem;">
                        <h4 style="margin: 0; color: var(--primary); font-size: 1rem;">📅 Patient Roster (Click Row to Load for Settlement)</h4>
                        <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
                            <div style="display: flex; align-items: center; gap: 0.35rem;">
                                <label style="font-size: 0.8rem; font-weight: 700; color: var(--text-muted); margin: 0;">Date Filter:</label>
                                <input type="date" id="billingRosterDateInput" class="form-control" style="width: 155px; padding: 0.35rem 0.5rem; font-size: 0.82rem;" onchange="loadBillingRosterForDate(this.value)">
                            </div>
                            <div style="display: flex; align-items: center; gap: 0.35rem;">
                                <input type="text" id="billingRosterFilterInput" class="form-control" style="width: 220px; padding: 0.35rem 0.6rem; font-size: 0.82rem;" placeholder="Filter roster by Name or Appt No..." onkeyup="filterBillingRosterTable()">
                            </div>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="data-table" id="billingRosterTable">
                            <thead>
                                <tr>
                                    <th>Appointment No.</th>
                                    <th>Date</th>
                                    <th>Token #</th>
                                    <th>Patient Name & Doctor</th>
                                    <th>Contact Number</th>
                                    <th>Deposit Payment Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${not empty allAppointments ? allAppointments : todayAppointments}" var="app">
                                    <tr class="billing-roster-row" data-date="${app.appointmentDate}">
                                        <td><strong><c:out value="${app.appointmentNumber != null ? app.appointmentNumber : app.appointmentId}" /></strong></td>
                                        <td><strong style="color: var(--primary);">
                                                <c:out
                                                    value="${app.formattedAppointmentDate != null ? app.formattedAppointmentDate : app.appointmentDate}" />
                                            </strong></td>
                                        <td>#
                                            <c:out value="${app.tokenNumber}" />
                                        </td>
                                        <td>
                                            <strong>
                                                <c:out value="${app.patientName}" />
                                            </strong>
                                            <br><small style="color: var(--text-muted);">
                                                <c:out value="${app.dentistName}" />
                                            </small>
                                            <c:if test="${not empty app.medicalHistory}">
                                                <br><span
                                                    style="display: inline-block; margin-top: 0.15rem; padding: 0.1rem 0.3rem; background-color: #FEE2E2; color: #991B1B; border: 1px solid #FECACA; border-radius: 4px; font-size: 0.7rem; font-weight: 600;">⚠️
                                                    <c:out value="${app.medicalHistory}" />
                                                </span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:out value="${app.contactNumber}" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when
                                                    test="${app.paymentStatus eq 'PAID_DEPOSIT' or app.paymentStatus eq 'FULL_PAID'}">
                                                    <span
                                                        style="color: #065F46; background-color: #D1FAE5; border: 1px solid #A7F3D0; padding: 3px 8px; border-radius: 4px; font-size: 0.78rem; font-weight: 700;">
                                                        🎟️ PAID (LKR
                                                        <fmt:formatNumber value="${app.paidAmount}" minFractionDigits="2" maxFractionDigits="2" />)
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span
                                                        style="color: #991B1B; background-color: #FEE2E2; border: 1px solid #FECACA; padding: 3px 8px; border-radius: 4px; font-size: 0.78rem; font-weight: 700;">
                                                        ⚠️ UNPAID (Pay Later)
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <button type="button" class="btn-secondary"
                                                style="padding: 0.3rem 0.65rem; font-size: 0.78rem; background-color: #0284C7; border-color: #0369A1; color: white;"
                                                onclick="loadAppointmentIntoBilling(${app.appointmentId})">⚡ Load to
                                                Billing</button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty allAppointments && empty todayAppointments}">
                                    <tr>
                                        <td colspan="7" style="text-align: center; color: var(--text-muted);">No
                                            appointments booked for billing calculation.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <script>
                // Global Billing State
                window.currentBillingAppt = null;
                window.addedProcedures = [];

                function filterBillingRosterTable() {
                    const filterInput = document.getElementById("billingRosterFilterInput");
                    const dateInput = document.getElementById("billingRosterDateInput");
                    const filterVal = filterInput ? filterInput.value.toLowerCase().trim() : "";
                    const dateVal = dateInput ? dateInput.value : "";
                    const table = document.getElementById("billingRosterTable");
                    if (!table) return;

                    const tbody = table.querySelector("tbody");
                    if (!tbody) return;

                    const rows = tbody.querySelectorAll("tr");
                    rows.forEach(row => {
                        if (row.querySelector("td[colspan]")) return;

                        const text = row.innerText.toLowerCase();
                        const rowDate = row.getAttribute("data-date") || "";

                        let matchesSearch = !filterVal || text.includes(filterVal);
                        let matchesDate = !dateVal || rowDate === dateVal;

                        if (matchesSearch && matchesDate) {
                            delete row.dataset.searchHidden;
                        } else {
                            row.dataset.searchHidden = "true";
                        }
                    });

                    if (table.updatePagination) {
                        table.updatePagination();
                    }
                }

                function loadBillingRosterForDate(selectedDate) {
                    filterBillingRosterTable();
                }

                function resetBillingSearchFilters() {
                    const textInp = document.getElementById("billingApptSearchInput");
                    if (textInp) textInp.value = "";

                    document.getElementById("billingSearchError").style.display = "none";
                    document.getElementById("billingSearchResultsContainer").style.display = "none";
                    document.getElementById("billingSearchResultsTbody").innerHTML = "";
                }

                function lookupAppointmentUniversal() {
                    const rawInput = document.getElementById("billingApptSearchInput").value.trim();
                    const errDiv = document.getElementById("billingSearchError");
                    const resultsContainer = document.getElementById("billingSearchResultsContainer");
                    const tbody = document.getElementById("billingSearchResultsTbody");
                    const matchCountSpan = document.getElementById("billingSearchMatchCount");

                    errDiv.style.display = "none";
                    resultsContainer.style.display = "none";
                    tbody.innerHTML = "";

                    if (!rawInput) {
                        errDiv.innerText = "Please enter a Patient Name, Phone, NIC, or APT # (e.g. APT-1014) to search.";
                        errDiv.style.display = "block";
                        return;
                    }

                    const url = "${pageContext.request.contextPath}/api/appointments/search?q=" + encodeURIComponent(rawInput);

                    fetch(url)
                        .then(res => {
                            if (!res.ok) throw new Error("Error querying appointment records.");
                            return res.json();
                        })
                        .then(tickets => {
                            if (!tickets || tickets.length === 0) {
                                errDiv.innerText = "No appointment records found matching '" + rawInput + "'.";
                                errDiv.style.display = "block";
                                return;
                            }

                            matchCountSpan.innerText = tickets.length;
                            let html = "";
                            tickets.forEach(t => {
                                const aptNoStr = (t.appointmentNumber || t.appointmentId);
                                const dateStr = t.formattedAppointmentDate || t.appointmentDate || "N/A";
                                const pName = t.patientName || "Unknown Patient";
                                const pContact = (t.contactNumber || "") + (t.nic ? " (" + t.nic + ")" : "");
                                const dName = t.dentistName || "N/A";
                                const isPaid = (t.paymentStatus === 'PAID_DEPOSIT' || t.paymentStatus === 'FULL_PAID');
                                const badgeHtml = isPaid 
                                    ? '<span style="color: #065F46; background-color: #D1FAE5; border: 1px solid #A7F3D0; padding: 2px 6px; border-radius: 4px; font-weight: 700; font-size: 0.75rem;">🎟️ PAID</span>'
                                    : '<span style="color: #991B1B; background-color: #FEE2E2; border: 1px solid #FECACA; padding: 2px 6px; border-radius: 4px; font-weight: 700; font-size: 0.75rem;">⚠️ UNPAID</span>';

                                html += '<tr>' +
                                    '<td><strong>' + aptNoStr + '</strong></td>' +
                                    '<td><strong style="color: var(--primary);">' + dateStr + '</strong></td>' +
                                    '<td><strong>' + pName + '</strong></td>' +
                                    '<td>' + pContact + '</td>' +
                                    '<td>' + dName + '</td>' +
                                    '<td>' + badgeHtml + '</td>' +
                                    '<td><button type="button" class="btn-secondary" style="padding: 0.25rem 0.6rem; font-size: 0.78rem; background-color: #0284C7; border-color: #0369A1; color: white;" onclick="loadAppointmentIntoBilling(' + t.appointmentId + ')">⚡ Select</button></td>' +
                                    '</tr>';
                            });
                            tbody.innerHTML = html;
                            resultsContainer.style.display = "block";

                            // Auto-load if exactly 1 match
                            if (tickets.length === 1) {
                                loadAppointmentIntoBilling(tickets[0].appointmentId);
                            }
                        })
                        .catch(err => {
                            errDiv.innerText = err.message || "Failed to search appointments.";
                            errDiv.style.display = "block";
                        });
                }

                function loadBillingRosterForDate(dateVal) {
                    if (!dateVal) return;
                    const tbody = document.getElementById("billingRosterTable").getElementsByTagName("tbody")[0];

                    fetch("${pageContext.request.contextPath}/api/appointments/date?date=" + encodeURIComponent(dateVal))
                        .then(res => res.json())
                        .then(appointments => {
                            if (!appointments || appointments.length === 0) {
                                tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No appointments booked for date ' + dateVal + '.</td></tr>';
                                return;
                            }
                            let html = "";
                            appointments.forEach(app => {
                                const aptNo = (app.appointmentNumber || app.appointmentId);
                                const appDate = app.formattedAppointmentDate || app.appointmentDate || dateVal;
                                const isPaid = (app.paymentStatus === 'PAID_DEPOSIT' || app.paymentStatus === 'FULL_PAID');
                                const badgeHtml = isPaid
                                    ? '<span style="color: #065F46; background-color: #D1FAE5; border: 1px solid #A7F3D0; padding: 3px 8px; border-radius: 4px; font-size: 0.78rem; font-weight: 700;">🎟️ PAID</span>'
                                    : '<span style="color: #991B1B; background-color: #FEE2E2; border: 1px solid #FECACA; padding: 3px 8px; border-radius: 4px; font-size: 0.78rem; font-weight: 700;">⚠️ UNPAID</span>';

                                let medHistHtml = "";
                                if (app.medicalHistory && app.medicalHistory.trim() !== "") {
                                    medHistHtml = '<br><span style="display: inline-block; margin-top: 0.15rem; padding: 0.1rem 0.3rem; background-color: #FEE2E2; color: #991B1B; border: 1px solid #FECACA; border-radius: 4px; font-size: 0.7rem; font-weight: 600;">⚠️ ' + app.medicalHistory + '</span>';
                                }

                                html += '<tr>' +
                                    '<td><strong>' + aptNo + '</strong></td>' +
                                    '<td><strong style="color: var(--primary);">' + appDate + '</strong></td>' +
                                    '<td>#' + (app.tokenNumber || "N/A") + '</td>' +
                                    '<td><strong>' + (app.patientName || "") + '</strong><br><small style="color: var(--text-muted);">' + (app.dentistName || "") + '</small>' + medHistHtml + '</td>' +
                                    '<td>' + (app.contactNumber || "") + '</td>' +
                                    '<td>' + badgeHtml + '</td>' +
                                    '<td><button type="button" class="btn-secondary" style="padding: 0.3rem 0.65rem; font-size: 0.78rem; background-color: #0284C7; border-color: #0369A1; color: white;" onclick="loadAppointmentIntoBilling(' + app.appointmentId + ')">⚡ Load to Billing</button></td>' +
                                    '</tr>';
                            });
                            tbody.innerHTML = html;
                        })
                        .catch(err => {
                            console.error("Failed to load roster for date:", err);
                        });
                }

                function lookupAppointmentByNumber() {
                    lookupAppointmentUniversal();
                }

                function loadAppointmentIntoBilling(appointmentId) {
                    const errDiv = document.getElementById("billingSearchError");
                    errDiv.style.display = "none";

                    fetch("${pageContext.request.contextPath}/api/billing/appointment/" + appointmentId)
                        .then(res => {
                            if (!res.ok) {
                                throw new Error("Appointment #" + appointmentId + " not found in clinic records.");
                            }
                            return res.json();
                        })
                        .then(billData => {
                            window.currentBillingAppt = billData;
                            window.addedProcedures = [];
                            renderActiveBillingCard(billData);
                        })
                        .catch(err => {
                            errDiv.innerText = err.message || "Failed to find appointment.";
                            errDiv.style.display = "block";
                            document.getElementById("billingActiveCard").style.display = "none";
                        });
                }

                function renderActiveBillingCard(bill) {
                    document.getElementById("activeApptNo").innerText = bill.appointmentId;
                    document.getElementById("activePatientName").innerText = bill.patientName || "Patient";
                    document.getElementById("activeAppointmentDate").innerText = bill.formattedAppointmentDate || bill.appointmentDate || "N/A";
                    document.getElementById("activePatientContact").innerText = bill.patientContact || "N/A";
                    document.getElementById("activeDentistName").innerText = bill.dentistName || "Doctor";
                    document.getElementById("activeTokenNo").innerText = bill.tokenNumber || "N/A";

                    // Medical Alert
                    const medAlertDiv = document.getElementById("activeMedicalHistoryAlert");
                    if (bill.medicalHistory && bill.medicalHistory.trim() !== "") {
                        document.getElementById("activeMedicalHistoryText").innerText = bill.medicalHistory;
                        medAlertDiv.style.display = "block";
                    } else {
                        medAlertDiv.style.display = "none";
                    }

                    // Payment Badge
                    const badgeContainer = document.getElementById("activePaymentBadgeContainer");
                    const prevPaid = parseFloat(bill.previousPaidAmount) || 0;
                    if (prevPaid > 0) {
                        badgeContainer.innerHTML = '<span style="color: #065F46; background-color: #D1FAE5; border: 1px solid #A7F3D0; padding: 6px 12px; border-radius: 6px; font-weight: 700; font-size: 0.85rem; display: inline-block;">🎟️ STAGE 1 DEPOSIT PAID: LKR ' + prevPaid.toFixed(2) + '</span>';
                    } else {
                        badgeContainer.innerHTML = '<span style="color: #991B1B; background-color: #FEE2E2; border: 1px solid #FECACA; padding: 6px 12px; border-radius: 6px; font-weight: 700; font-size: 0.85rem; display: inline-block;">⚠️ STAGE 1 UNPAID (Pay Deposit + Procedure Now)</span>';
                    }

                    // Breakdown Base Values
                    document.getElementById("summaryConsultFee").innerText = (parseFloat(bill.consultationFee) || 0).toFixed(2);
                    document.getElementById("summaryClinicCharge").innerText = (parseFloat(bill.clinicCharge) || 500).toFixed(2);
                    document.getElementById("summaryPrevPaid").innerText = prevPaid.toFixed(2);
                    document.getElementById("summaryDiscountInput").value = "0";

                    renderProceduresTable();
                    recalculateBillingTotals();

                    const activeCard = document.getElementById("billingActiveCard");
                    activeCard.style.display = "block";
                    activeCard.scrollIntoView({ behavior: "smooth", block: "nearest" });
                }

                function filterBillingRosterTable() {
                    const input = document.getElementById("billingRosterFilterInput");
                    if (!input) return;
                    const filter = input.value.toLowerCase().trim();
                    const table = document.getElementById("billingRosterTable");
                    if (!table) return;
                    const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

                    for (let i = 0; i < rows.length; i++) {
                        const rowText = rows[i].innerText.toLowerCase();
                        if (filter === "" || rowText.includes(filter)) {
                            rows[i].style.display = "";
                        } else {
                            rows[i].style.display = "none";
                        }
                    }
                }

                function addBillingProcedure() {
                    const select = document.getElementById("billingProcedureDropdown");
                    const opt = select.options[select.selectedIndex];
                    if (!opt || !opt.value) return;

                    const id = parseInt(opt.value, 10);
                    const name = opt.getAttribute("data-name");
                    const cost = parseFloat(opt.getAttribute("data-cost")) || 0;

                    window.addedProcedures.push({ id: id, name: name, cost: cost });
                    select.selectedIndex = 0;

                    renderProceduresTable();
                    recalculateBillingTotals();
                }

                function removeBillingProcedure(index) {
                    window.addedProcedures.splice(index, 1);
                    renderProceduresTable();
                    recalculateBillingTotals();
                }

                function renderProceduresTable() {
                    const tbody = document.getElementById("addedProceduresTbody");
                    if (!window.addedProcedures || window.addedProcedures.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="3" style="text-align: center; color: var(--text-muted); font-style: italic;">No extra procedures added yet. Select from the dropdown above to add treatment procedures.</td></tr>';
                        return;
                    }

                    let html = "";
                    window.addedProcedures.forEach((proc, idx) => {
                        html += '<tr>' +
                            '<td><strong>' + proc.name + '</strong></td>' +
                            '<td style="text-align: right;">LKR ' + proc.cost.toFixed(2) + '</td>' +
                            '<td style="text-align: center;"><button type="button" class="btn-logout" style="padding: 0.15rem 0.4rem; font-size: 0.7rem; background-color: #EF4444; color: #FFFFFF !important; font-weight: 700; border: none; border-radius: 4px; cursor: pointer;" onclick="removeBillingProcedure(' + idx + ')">❌ Remove</button></td>' +
                            '</tr>';
                    });
                    tbody.innerHTML = html;
                }

                function recalculateBillingTotals() {
                    if (!window.currentBillingAppt) return;

                    const consultFee = parseFloat(window.currentBillingAppt.consultationFee) || 0;
                    const clinicCharge = parseFloat(window.currentBillingAppt.clinicCharge) || 500;

                    let proceduresSubtotal = 0;
                    if (window.addedProcedures) {
                        window.addedProcedures.forEach(p => proceduresSubtotal += p.cost);
                    }

                    const grossTotal = consultFee + clinicCharge + proceduresSubtotal;
                    const prevPaid = parseFloat(window.currentBillingAppt.previousPaidAmount) || 0;
                    const discount = parseFloat(document.getElementById("summaryDiscountInput").value) || 0;

                    const netBalance = Math.max(0, grossTotal - prevPaid - discount);

                    document.getElementById("summaryProceduresSubtotal").innerText = proceduresSubtotal.toFixed(2);
                    document.getElementById("summaryGrossTotal").innerText = grossTotal.toFixed(2);
                    document.getElementById("summaryNetBalance").innerText = netBalance.toFixed(2);

                    // Sync Amount Paid default if user hasn't modified it manually
                    const paidInput = document.getElementById("summaryAmountPaidInput");
                    if (paidInput) {
                        paidInput.value = netBalance.toFixed(2);
                    }
                    recalculateChangeReturn();

                    // Update Form hidden values
                    document.getElementById("formDiscount").value = discount;
                    document.getElementById("formPaymentMethod").value = document.getElementById("settlementPaymentMethodSelect").value;
                }

                function recalculateChangeReturn() {
                    const netDue = parseFloat(document.getElementById("summaryNetBalance").innerText) || 0;
                    const paidVal = parseFloat(document.getElementById("summaryAmountPaidInput").value) || 0;
                    const changeReturn = Math.max(0, paidVal - netDue);
                    document.getElementById("summaryChangeReturn").innerText = changeReturn.toFixed(2);
                }

                function submitFinalSettledInvoice() {
                    if (!window.currentBillingAppt) return;

                    const apptId = window.currentBillingAppt.appointmentId;
                    const form = document.getElementById("settlementSubmitForm");
                    form.action = "${pageContext.request.contextPath}/billing/receipt/" + apptId;

                    // Container for treatmentTypeIds
                    const container = document.getElementById("formTreatmentTypeIdsContainer");
                    container.innerHTML = "";

                    if (window.addedProcedures && window.addedProcedures.length > 0) {
                        window.addedProcedures.forEach(p => {
                            const inp = document.createElement("input");
                            inp.type = "hidden";
                            inp.name = "treatmentTypeIds";
                            inp.value = p.id;
                            container.appendChild(inp);
                        });
                    }

                    document.getElementById("formDiscount").value = document.getElementById("summaryDiscountInput").value || "0";
                    document.getElementById("formPaymentMethod").value = document.getElementById("settlementPaymentMethodSelect").value;

                    form.submit();
                }
            </script>