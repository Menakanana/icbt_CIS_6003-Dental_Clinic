<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- MULTI-SESSION DOCTOR SHIFT ROSTER & BREAK MANAGEMENT COMPONENT -->
<div id="schedules-tab" class="tab-content">
    
    <div class="card-panel" style="margin-bottom: 1.5rem; background-color: #ECFDF5; border: 1.5px solid #A7F3D0;">
        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                <h3 style="color: #065F46; font-size: 1.25rem; margin-bottom: 0.25rem;">📅 Doctor Operational Shift Roster</h3>
                <p style="color: #047857; font-size: 0.88rem; margin: 0;">Configure multiple 1–2 hour session blocks and breaks for each doctor. Time slots & tokens will be allocated automatically per session!</p>
            </div>
            <div style="display: flex; align-items: center; gap: 1rem; flex-wrap: wrap;">
                <div style="display: flex; align-items: center; gap: 0.5rem;">
                    <label style="font-weight: 700; color: #065F46;">Roster Date:</label>
                    <input type="date" id="rosterDatePicker" class="form-control" style="width: 160px; font-weight: 600;" onchange="onRosterDateChange()" oninput="onRosterDateChange()">
                </div>
                <button type="button" class="btn-primary" style="background-color: #059669; border-color: #047857; padding: 0.6rem 1.2rem; font-weight: 700; font-size: 0.9rem;" onclick="saveAllRosterSchedules()">💾 Save Master Roster</button>
            </div>
        </div>
    </div>

    <div id="rosterToastMsg" style="display: none; padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; font-weight: 600; font-size: 0.9rem;"></div>

    <!-- Doctor Session Cards Grid -->
    <div id="doctorCardsRosterContainer" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(360px, 1fr)); gap: 1.25rem;">
        <c:forEach items="${dentists}" var="d">
            <div class="card-panel doctor-roster-card" id="doctorCard_${d.dentistId}" data-dentist-id="${d.dentistId}" data-dentist-name="<c:out value='${d.dentistName}'/>" style="border: 1px solid var(--border-color); position: relative;">
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; border-bottom: 1px solid var(--border-color); padding-bottom: 0.75rem;">
                    <div>
                        <h4 style="margin: 0; color: var(--primary); font-size: 1.05rem;">🩺 <c:out value="${d.dentistName}"/></h4>
                        <span style="font-size: 0.8rem; color: var(--text-muted);"><c:out value="${d.specialization}"/> | Fee: LKR <fmt:formatNumber value="${d.consultationFee}" type="currency" currencySymbol=""/></span>
                    </div>
                    <button type="button" id="docDutyBtn_${d.dentistId}" class="btn-logout" style="padding: 0.25rem 0.65rem; font-size: 0.75rem; background-color: #EF4444; color: #FFFFFF !important; font-weight: 700; border: none; border-radius: 6px; cursor: pointer;" onclick="markDoctorOffDutyUI(${d.dentistId}, '<c:out value="${d.dentistName}"/>')">🚫 Mark Off Duty</button>
                </div>

                <div id="sessionsContainer_${d.dentistId}" class="doctor-sessions-list" style="display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 1rem;">
                    <!-- Session rows injected dynamically via JS -->
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed var(--border-color); padding-top: 0.75rem;">
                    <button type="button" class="btn-primary" style="padding: 0.35rem 0.75rem; font-size: 0.8rem; background-color: #0284C7; border-color: #0369A1;" onclick="addSessionBlockRow(${d.dentistId}, '09:00', '11:00', 6, null)">+ Add Session Block</button>
                    <span id="doctorSummaryBadge_${d.dentistId}" style="font-size: 0.78rem; font-weight: 700; color: var(--text-muted);">No Sessions</span>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty dentists}">
            <div class="card-panel" style="grid-column: 1 / -1; text-align: center; color: var(--text-muted);">
                No active doctors found in the clinic directory.
            </div>
        </c:if>
    </div>

</div>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const rosterPicker = document.getElementById('rosterDatePicker');
        if (rosterPicker && !rosterPicker.value) {
            const d = new Date();
            rosterPicker.value = d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0');
        }
        onRosterDateChange();
    });

    function onRosterDateChange() {
        const dateVal = document.getElementById('rosterDatePicker').value;
        if (!dateVal) return;
        loadRosterForDate(dateVal);
    }

    async function loadRosterForDate(dateVal) {
        // Clear existing session rows
        document.querySelectorAll('.doctor-sessions-list').forEach(el => el.innerHTML = '');

        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/dentists/schedules/date?scheduleDate=' + dateVal);
            if (resp.ok) {
                const schedules = await resp.json();
                const grouped = {};

                schedules.forEach(s => {
                    if (!grouped[s.dentistId]) grouped[s.dentistId] = [];
                    grouped[s.dentistId].push(s);
                });

                document.querySelectorAll('.doctor-roster-card').forEach(card => {
                    const dId = parseInt(card.dataset.dentistId);
                    const dName = card.dataset.dentistName || 'Doctor';
                    const docSchedules = grouped[dId] || [];
                    const container = document.getElementById('sessionsContainer_' + dId);
                    const dutyBtn = document.getElementById('docDutyBtn_' + dId);

                    const activeSchedules = docSchedules.filter(s => s.isActive === true || s.active === true);
                    const isOffDuty = docSchedules.length > 0 && activeSchedules.length === 0;

                    if (dutyBtn) {
                        if (isOffDuty) {
                            dutyBtn.innerText = '✅ Mark On Duty';
                            dutyBtn.style.backgroundColor = '#059669';
                            dutyBtn.onclick = function() { markDoctorOnDutyUI(dId, dName); };
                        } else {
                            dutyBtn.innerText = '🚫 Mark Off Duty';
                            dutyBtn.style.backgroundColor = '#EF4444';
                            dutyBtn.onclick = function() { markDoctorOffDutyUI(dId, dName); };
                        }
                    }

                    if (isOffDuty) {
                        const safeName = dName.replace(/'/g, "\\'");
                        container.innerHTML = '<div style="background-color: #FEE2E2; color: #991B1B; padding: 0.8rem; border-radius: 6px; font-size: 0.85rem; font-weight: 700; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 0.5rem;">' +
                            '<span>🚫 OFF DUTY (Doctor Not Working on ' + dateVal + ')</span>' +
                            '<button type="button" class="btn-primary" style="background-color: #059669; border-color: #047857; padding: 0.35rem 0.85rem; font-size: 0.8rem; font-weight: 700; cursor: pointer;" onclick="markDoctorOnDutyUI(' + dId + ', \'' + safeName + '\')">✅ Enable / Mark On Duty</button>' +
                            '</div>';
                        updateDoctorSummaryBadge(dId);
                    } else if (activeSchedules.length > 0) {
                        activeSchedules.forEach(s => {
                            const startStr = s.sessionStartTime ? s.sessionStartTime.substring(0, 5) : '09:00';
                            const endStr = s.sessionEndTime ? s.sessionEndTime.substring(0, 5) : '11:00';
                            addSessionBlockRow(dId, startStr, endStr, s.maxPatientsInSession || 6, s.scheduleId);
                        });
                    } else {
                        // Default single morning session if not configured yet
                        addSessionBlockRow(dId, '09:00', '13:00', 10, null);
                    }
                });
            }
        } catch (e) {
            console.error("Failed to load multi-session roster:", e);
        }
    }

    function addSessionBlockRow(dentistId, defaultStart, defaultEnd, defaultCapacity, scheduleId) {
        const container = document.getElementById('sessionsContainer_' + dentistId);
        if (!container) return;

        // If card was in Off Duty alert state, clear it
        if (container.querySelector('div')?.innerText.includes('OFF DUTY')) {
            container.innerHTML = '';
        }

        const blockId = 'session_row_' + Date.now() + '_' + Math.floor(Math.random() * 1000);
        const validSchedId = (scheduleId && scheduleId !== 'null' && scheduleId !== 'undefined') ? scheduleId : 'null';
        const rowHtml = `
            <div id="${blockId}" class="session-block-row" data-schedule-id="${scheduleId || ''}" style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 0.65rem; border-radius: 6px;">
                <div style="display: flex; justify-content: space-between; align-items: center; gap: 0.5rem; margin-bottom: 0.35rem;">
                    <div style="font-size: 0.8rem; font-weight: 700; color: #0F766E;">⏰ Session Block</div>
                    <button type="button" class="btn-logout" style="padding: 0.2rem 0.5rem; font-size: 0.72rem; background: #EF4444; color: #FFFFFF !important; font-weight: 700; border: none; border-radius: 4px; cursor: pointer;" onclick="removeSessionBlockRow('${blockId}', ${dentistId}, ${validSchedId})">🗑️ Delete</button>
                </div>
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 0.5rem;">
                    <div>
                        <label style="font-size: 0.72rem; font-weight: 600; display: block; color: var(--text-muted);">Start Time</label>
                        <input type="time" class="form-control session-start" value="${defaultStart}" style="font-size: 0.82rem; padding: 0.25rem 0.4rem;" oninput="updateBlockAllocation('${blockId}', ${dentistId})">
                    </div>
                    <div>
                        <label style="font-size: 0.72rem; font-weight: 600; display: block; color: var(--text-muted);">End Time</label>
                        <input type="time" class="form-control session-end" value="${defaultEnd}" style="font-size: 0.82rem; padding: 0.25rem 0.4rem;" oninput="updateBlockAllocation('${blockId}', ${dentistId})">
                    </div>
                    <div>
                        <label style="font-size: 0.72rem; font-weight: 600; display: block; color: var(--text-muted);">Max Patients</label>
                        <input type="number" class="form-control session-capacity" value="${defaultCapacity}" min="1" max="50" style="font-size: 0.82rem; padding: 0.25rem 0.4rem;" oninput="updateBlockAllocation('${blockId}', ${dentistId})">
                    </div>
                </div>
                <div class="session-badge" style="margin-top: 0.4rem; font-size: 0.75rem; font-weight: 700; color: #3730A3; background: #E0E7FF; padding: 0.2rem 0.5rem; border-radius: 4px; display: inline-block;">
                    Calculating...
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', rowHtml);
        updateBlockAllocation(blockId, dentistId);
    }

    async function removeSessionBlockRow(blockId, dentistId, scheduleId) {
        if (scheduleId && scheduleId !== 'null' && scheduleId !== 'undefined' && scheduleId !== 0) {
            try {
                await fetch('${pageContext.request.contextPath}/api/dentists/schedules/' + scheduleId, { method: 'DELETE' });
            } catch (e) {
                console.error("Failed to delete schedule block:", e);
            }
        }
        const row = document.getElementById(blockId);
        if (row) row.remove();
        updateDoctorSummaryBadge(dentistId);
    }

    function updateBlockAllocation(blockId, dentistId) {
        const row = document.getElementById(blockId);
        if (!row) return;

        const startVal = row.querySelector('.session-start').value;
        const endVal = row.querySelector('.session-end').value;
        const capVal = parseInt(row.querySelector('.session-capacity').value) || 1;
        const badge = row.querySelector('.session-badge');

        if (!startVal || !endVal) {
            badge.innerText = "Invalid Time";
            return;
        }

        const [sHours, sMins] = startVal.split(':').map(Number);
        const [eHours, eMins] = endVal.split(':').map(Number);
        let diff = (eHours * 60 + eMins) - (sHours * 60 + sMins);

        if (diff <= 0) {
            badge.innerText = "End time must be after start time";
            badge.style.background = "#FEE2E2";
            badge.style.color = "#991B1B";
        } else {
            let minsPerPatient = Math.max(5, Math.floor(diff / capVal));
            badge.style.background = "#E0E7FF";
            badge.style.color = "#3730A3";
            badge.innerText = minsPerPatient + ' mins / patient (' + capVal + ' Tokens)';
        }
        updateDoctorSummaryBadge(dentistId);
    }

    function updateDoctorSummaryBadge(dentistId) {
        const container = document.getElementById('sessionsContainer_' + dentistId);
        const badge = document.getElementById('doctorSummaryBadge_' + dentistId);
        if (!container || !badge) return;

        const rows = container.querySelectorAll('.session-block-row');
        if (rows.length === 0) {
            if (container.innerText.includes('OFF DUTY')) {
                badge.innerText = "Status: OFF DUTY";
                badge.style.color = "#991B1B";
            } else {
                badge.innerText = "No Active Sessions";
                badge.style.color = "var(--text-muted)";
            }
            return;
        }

        let totalTokens = 0;
        rows.forEach(r => {
            totalTokens += parseInt(r.querySelector('.session-capacity')?.value || 0);
        });
        badge.innerText = rows.length + ' Session Block(s) | Total: ' + totalTokens + ' Tokens';
        badge.style.color = "#047857";
    }

    async function markDoctorOffDutyUI(dentistId, dentistName) {
        const dateVal = document.getElementById('rosterDatePicker').value;
        if (!dateVal) {
            showRosterToast("⚠️ Please select a date first.", false);
            return;
        }

        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/dentists/schedules/off-duty?dentistId=' + dentistId + '&scheduleDate=' + dateVal, {
                method: 'POST'
            });
            if (resp.ok) {
                showRosterToast("✅ " + dentistName + " marked OFF DUTY for " + dateVal, true);
                loadRosterForDate(dateVal);
            } else {
                showRosterToast("⚠️ Failed to mark doctor off duty", false);
            }
        } catch (e) {
            showRosterToast("⚠️ Connection error marking doctor off duty", false);
        }
    }

    async function markDoctorOnDutyUI(dentistId, dentistName) {
        const dateVal = document.getElementById('rosterDatePicker').value;
        if (!dateVal) {
            showRosterToast("⚠️ Please select a date first.", false);
            return;
        }

        try {
            const resp = await fetch('${pageContext.request.contextPath}/api/dentists/schedules/on-duty?dentistId=' + dentistId + '&scheduleDate=' + dateVal, {
                method: 'POST'
            });
            if (resp.ok) {
                showRosterToast("✅ " + dentistName + " is now ON DUTY for " + dateVal, true);
                loadRosterForDate(dateVal);
            } else {
                showRosterToast("⚠️ Failed to mark doctor on duty", false);
            }
        } catch (e) {
            showRosterToast("⚠️ Connection error marking doctor on duty", false);
        }
    }

    async function saveAllRosterSchedules() {
        const dateVal = document.getElementById('rosterDatePicker').value;
        if (!dateVal) {
            showRosterToast("⚠️ Please pick a roster date.", false);
            return;
        }

        const cards = document.querySelectorAll('.doctor-roster-card');
        let saveCount = 0;
        let hasErrors = false;

        for (const card of cards) {
            const dId = parseInt(card.dataset.dentistId);
            const rows = card.querySelectorAll('.session-block-row');

            for (const row of rows) {
                const sStart = row.querySelector('.session-start').value;
                const sEnd = row.querySelector('.session-end').value;
                const sCap = parseInt(row.querySelector('.session-capacity').value) || 0;

                if (!sStart || !sEnd || sCap <= 0) {
                    hasErrors = true;
                    continue;
                }

                const payload = {
                    dentistId: dId,
                    scheduleDate: dateVal,
                    sessionStartTime: sStart.length === 5 ? sStart + ":00" : sStart,
                    sessionEndTime: sEnd.length === 5 ? sEnd + ":00" : sEnd,
                    maxPatientsInSession: sCap
                };

                try {
                    const res = await fetch('${pageContext.request.contextPath}/api/dentists/schedules', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    });
                    if (res.ok) saveCount++;
                } catch (e) {
                    hasErrors = true;
                }
            }
        }

        if (hasErrors) {
            showRosterToast("⚠️ Master Roster saved with warnings. Please verify invalid shift inputs.", false);
        } else {
            showRosterToast("✅ Master Daily Roster for " + dateVal + " successfully saved (" + saveCount + " shift session blocks updated)!", true);
        }
        loadRosterForDate(dateVal);
    }

    function showRosterToast(msg, isSuccess) {
        const toast = document.getElementById('rosterToastMsg');
        if (!toast) return;
        toast.innerText = msg;
        toast.style.display = 'block';
        toast.style.backgroundColor = isSuccess ? '#D1FAE5' : '#FEE2E2';
        toast.style.color = isSuccess ? '#065F46' : '#991B1B';
        toast.style.border = isSuccess ? '1px solid #A7F3D0' : '1px solid #FECACA';

        setTimeout(() => {
            toast.style.display = 'none';
        }, 5000);
    }
</script>

