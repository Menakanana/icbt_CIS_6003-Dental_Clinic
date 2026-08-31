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
                <input type="date" id="rosterDatePicker" class="form-control" style="width: 170px;" onchange="onRosterDateChange()">
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
                                <form action="${pageContext.request.contextPath}/dentists/schedule/save" method="post" id="scheduleForm_${d.dentistId}" onsubmit="prepareScheduleSubmit(${d.dentistId})">
                                    <input type="hidden" name="dentistId" value="${d.dentistId}">
                                    <input type="hidden" name="scheduleDate" id="formScheduleDate_${d.dentistId}">
                                    <input type="hidden" name="sessionStartTime" id="formStartTime_${d.dentistId}">
                                    <input type="hidden" name="sessionEndTime" id="formEndTime_${d.dentistId}">
                                    <input type="hidden" name="maxPatientsInSession" id="formMaxPatients_${d.dentistId}">
                                    <button type="submit" class="btn-primary" style="padding: 0.4rem 0.8rem; font-size: 0.85rem; background-color: #059669; border-color: #047857;">Save Shift</button>
                                </form>
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
    if (document.getElementById('rosterDatePicker')) {
        document.getElementById('rosterDatePicker').valueAsDate = new Date();
    }

    function onRosterDateChange() {
        const dateVal = document.getElementById('rosterDatePicker').value;
        console.log("Roster date changed to:", dateVal);
    }

    function calculateRosterAllocation(dentistId) {
        const startVal = document.getElementById(`startTime_\${dentistId}`).value;
        const endVal = document.getElementById(`endTime_\${dentistId}`).value;
        const countVal = parseInt(document.getElementById(`maxPatients_\${dentistId}`).value) || 1;
        const badge = document.getElementById(`allocationBadge_\${dentistId}`);

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
        badge.innerText = `\${minsPerPatient} mins / patient (\${countVal} Tokens)`;
    }

    function prepareScheduleSubmit(dentistId) {
        const dateVal = document.getElementById('rosterDatePicker').value || new Date().toISOString().split('T')[0];
        document.getElementById(`formScheduleDate_\${dentistId}`).value = dateVal;
        document.getElementById(`formStartTime_\${dentistId}`).value = document.getElementById(`startTime_\${dentistId}`).value;
        document.getElementById(`formEndTime_\${dentistId}`).value = document.getElementById(`endTime_\${dentistId}`).value;
        document.getElementById(`formMaxPatients_\${dentistId}`).value = document.getElementById(`maxPatients_\${dentistId}`).value;
        return true;
    }

    // Trigger initial calculation for all doctors
    document.addEventListener("DOMContentLoaded", function() {
        <c:forEach items="${dentists}" var="d">
            calculateRosterAllocation(${d.dentistId});
        </c:forEach>
    });
</script>
