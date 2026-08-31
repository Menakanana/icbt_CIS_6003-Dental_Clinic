<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- DEDICATED DAILY SHIFT ROSTER & SCHEDULE CONFIGURATOR COMPONENT -->
<div id="schedules-tab" class="tab-content">
    
    <div class="card-panel" style="margin-bottom: 1.5rem; background-color: #ECFDF5; border: 1px solid #A7F3D0;">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h3 style="color: #065F46; font-size: 1.2rem; margin-bottom: 0.25rem;">📅 Daily Operational Roster & Shift Availability</h3>
                <p style="color: #047857; font-size: 0.88rem;">Configure shift start time, end time, and max patient count for all clinic doctors on the selected date. Time slots & queue tokens will be automatically allocated!</p>
            </div>
            <div style="display: flex; align-items: center; gap: 0.5rem;">
                <label style="font-weight: 600; color: #065F46;">Roster Date:</label>
                <input type="date" id="rosterDatePicker" class="form-control" style="width: 170px;" onchange="onRosterDateChange()" oninput="onRosterDateChange()">
            </div>
        </div>
    </div>

    <!-- Multi-Doctor Daily Shift Roster Table -->
    <div class="card-panel">
        <h3 style="margin-bottom: 1rem; color: var(--primary);">Doctor Shift Schedule & Patient Capacity Roster</h3>
        
        <div class="table-responsive">
            <table class="data-table" id="dailyRosterTable">
                <thead>
                    <tr>
                        <th>Doctor Name & Specialization</th>
                        <th>Shift Start Time</th>
                        <th>Shift End Time</th>
                        <th>Max Patient Count</th>
                        <th>Auto-Allocated Time Window</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${dentists}" var="d">
                        <tr id="roster_row_${d.dentistId}">
                            <td>
                                <strong><c:out value="${d.dentistName}"/></strong>
                                <br><small style="color: var(--text-muted);"><c:out value="${d.specialization}"/></small>
                            </td>
                            <td>
                                <input type="time" id="startTime_${d.dentistId}" class="form-control" value="09:00" oninput="calculateRosterAllocation(${d.dentistId})">
                            </td>
                            <td>
                                <input type="time" id="endTime_${d.dentistId}" class="form-control" value="13:00" oninput="calculateRosterAllocation(${d.dentistId})">
                            </td>
                            <td>
                                <input type="number" id="maxPatients_${d.dentistId}" class="form-control" style="width: 100px;" value="10" min="1" max="50" oninput="calculateRosterAllocation(${d.dentistId})">
                            </td>
                            <td>
                                <span id="allocationBadge_${d.dentistId}" class="badge badge-receptionist" style="font-size: 0.85rem;">24 mins / patient (10 Tokens)</span>
                            </td>
                            <td>
                                <div style="display: flex; gap: 0.4rem; align-items: center;">
                                    <form action="${pageContext.request.contextPath}/dentists/schedule/save" method="post" id="scheduleForm_${d.dentistId}" onsubmit="prepareScheduleSubmit(${d.dentistId})">
                                        <input type="hidden" name="dentistId" value="${d.dentistId}">
                                        <input type="hidden" name="scheduleDate" id="formScheduleDate_${d.dentistId}">
                                        <input type="hidden" name="sessionStartTime" id="formStartTime_${d.dentistId}">
                                        <input type="hidden" name="sessionEndTime" id="formEndTime_${d.dentistId}">
                                        <input type="hidden" name="maxPatientsInSession" id="formMaxPatients_${d.dentistId}">
                                        <button type="submit" class="btn-primary" style="padding: 0.4rem 0.7rem; font-size: 0.82rem; background-color: #059669; border-color: #047857;">Save Shift</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/dentists/schedule/off-duty" method="post" id="offDutyForm_${d.dentistId}" onsubmit="return prepareOffDutySubmit(${d.dentistId}, '<c:out value="${d.dentistName}"/>')">
                                        <input type="hidden" name="dentistId" value="${d.dentistId}">
                                        <input type="hidden" name="scheduleDate" id="offDutyScheduleDate_${d.dentistId}">
                                        <button type="submit" class="btn-secondary" style="padding: 0.4rem 0.7rem; font-size: 0.82rem; background-color: #ef4444; border-color: #dc2626; color: white;">Mark Off Duty</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty dentists}">
                        <tr>
                            <td colspan="6" style="text-align: center; color: var(--text-muted);">No active doctors found to schedule.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const rosterPicker = document.getElementById('rosterDatePicker');
        if (rosterPicker && !rosterPicker.value) {
            const d = new Date();
            rosterPicker.value = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
        }
    });

    function onRosterDateChange() {
        const dateVal = document.getElementById('rosterDatePicker').value;
        if (!dateVal) return;
        loadRosterForDate(dateVal);
    }

    async function loadRosterForDate(dateVal) {
        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/dentists/schedules/date?scheduleDate=' + dateVal);
            if (resp.ok) {
                const schedules = await resp.json();
                schedules.forEach(s => {
                    const dId = s.dentistId;
                    const startTimeInput = document.getElementById('startTime_' + dId);
                    const endTimeInput = document.getElementById('endTime_' + dId);
                    const maxPatientsInput = document.getElementById('maxPatients_' + dId);
                    const badge = document.getElementById('allocationBadge_' + dId);

                    if (s.isActive === false) {
                        if (badge) {
                            badge.innerText = "OFF DUTY (Not Working)";
                            badge.style.backgroundColor = "#FEE2E2";
                            badge.style.color = "#991B1B";
                        }
                    } else if (s.sessionStartTime && s.sessionEndTime) {
                        if (startTimeInput) startTimeInput.value = s.sessionStartTime.substring(0, 5);
                        if (endTimeInput) endTimeInput.value = s.sessionEndTime.substring(0, 5);
                        if (maxPatientsInput && s.maxPatientsInSession) maxPatientsInput.value = s.maxPatientsInSession;
                        calculateRosterAllocation(dId);
                    }
                });
            }
        } catch (e) {
            console.error("Failed to load roster for date:", e);
        }
    }

    function calculateRosterAllocation(dentistId) {
        const startVal = document.getElementById('startTime_' + dentistId).value;
        const endVal = document.getElementById('endTime_' + dentistId).value;
        const countVal = parseInt(document.getElementById('maxPatients_' + dentistId).value) || 1;
        const badge = document.getElementById('allocationBadge_' + dentistId);

        if (!startVal || !endVal) {
            badge.innerText = "Invalid Time";
            return;
        }

        const [sHours, sMins] = startVal.split(':').map(Number);
        const [eHours, eMins] = endVal.split(':').map(Number);

        let startMinutes = sHours * 60 + sMins;
        let endMinutes = eHours * 60 + eMins;

        let diff = endMinutes - startMinutes;
        if (diff <= 0) {
            badge.innerText = "End time must be after start time";
            badge.style.backgroundColor = "#FEE2E2";
            badge.style.color = "#991B1B";
            return;
        }

        badge.style.backgroundColor = "#E0E7FF";
        badge.style.color = "#3730A3";

        let minsPerPatient = Math.max(5, Math.floor(diff / countVal));
        badge.innerText = minsPerPatient + ' mins / patient (' + countVal + ' Tokens)';
    }

    function prepareScheduleSubmit(dentistId) {
        const dateVal = document.getElementById('rosterDatePicker').value || new Date().toISOString().split('T')[0];
        const startVal = document.getElementById('startTime_' + dentistId).value;
        const endVal = document.getElementById('endTime_' + dentistId).value;
        const countVal = parseInt(document.getElementById('maxPatients_' + dentistId).value) || 0;
        const badge = document.getElementById('allocationBadge_' + dentistId);

        if (!startVal || !endVal) {
            if (badge) {
                badge.innerText = "Invalid Time";
                badge.style.backgroundColor = "#FEE2E2";
                badge.style.color = "#991B1B";
            }
            return false;
        }

        const [sHours, sMins] = startVal.split(':').map(Number);
        const [eHours, eMins] = endVal.split(':').map(Number);
        let diff = (eHours * 60 + eMins) - (sHours * 60 + sMins);

        if (diff <= 0) {
            if (badge) {
                badge.innerText = "End time must be after start time";
                badge.style.backgroundColor = "#FEE2E2";
                badge.style.color = "#991B1B";
            }
            return false;
        }

        if (countVal <= 0) {
            if (badge) {
                badge.innerText = "Max capacity must be at least 1";
                badge.style.backgroundColor = "#FEE2E2";
                badge.style.color = "#991B1B";
            }
            return false;
        }

        document.getElementById('formScheduleDate_' + dentistId).value = dateVal;
        document.getElementById('formStartTime_' + dentistId).value = startVal;
        document.getElementById('formEndTime_' + dentistId).value = endVal;
        document.getElementById('formMaxPatients_' + dentistId).value = countVal;
        return true;
    }

    function prepareOffDutySubmit(dentistId, dentistName) {
        const dateVal = document.getElementById('rosterDatePicker').value || new Date().toISOString().split('T')[0];
        document.getElementById('offDutyScheduleDate_' + dentistId).value = dateVal;
        return confirm("Are you sure you want to mark " + dentistName + " OFF DUTY for " + dateVal + "?\\n\\nThey will be removed from the operational booking roster on this day and no appointments can be scheduled.");
    }

    // Trigger initial calculation and schedule load for all doctors
    document.addEventListener("DOMContentLoaded", function() {
        const dateVal = document.getElementById('rosterDatePicker') ? document.getElementById('rosterDatePicker').value : new Date().toISOString().split('T')[0];
        <c:forEach items="${dentists}" var="d">
            calculateRosterAllocation(${d.dentistId});
        </c:forEach>
        if (dateVal) {
            loadRosterForDate(dateVal);
        }
    });
</script>
