<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- TAB 4: ADMIN REPORTS & ANALYTICS COMPONENT -->
<c:if test="${user.role == 'Admin'}">
    <div id="admin-tab" class="tab-content">
        <div class="card-panel" style="margin-bottom: 1.5rem;">
            <h3 style="margin-bottom: 0.5rem; color: var(--primary);">📊 Executive Analytics & Performance Summary</h3>
            <p style="color: var(--text-muted); font-size: 0.9rem;">Operational metrics, daily revenue overview, and system audit log status.</p>
        </div>

        <!-- 🖨️ PRINTABLE ADMINISTRATIVE REPORTS CENTER -->
        <div class="card-panel reports-center-card">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                <div>
                    <h3 style="color: #0F172A; margin-bottom: 0.25rem;">📑 Printable Administrative Reports Center</h3>
                    <p style="color: #64748B; font-size: 0.875rem; margin: 0;">Generate executive report documents with clinic letterhead, KPI summaries, and A4 print alignment.</p>
                </div>
            </div>

            <!-- Global Quick Date Selector Bar -->
            <div class="reports-date-bar">
                <div>
                    <label style="font-size: 0.8rem; font-weight: 700; color: #475569; display: block; margin-bottom: 0.2rem;">Start Date</label>
                    <input type="date" id="globalReportStartDate" class="form-control" style="font-size: 0.85rem; padding: 0.4rem 0.6rem;" value="2026-08-01">
                </div>
                <div>
                    <label style="font-size: 0.8rem; font-weight: 700; color: #475569; display: block; margin-bottom: 0.2rem;">End Date</label>
                    <input type="date" id="globalReportEndDate" class="form-control" style="font-size: 0.85rem; padding: 0.4rem 0.6rem;" value="2026-09-10">
                </div>
                <div style="margin-top: 1rem;">
                    <span style="font-size: 0.8rem; color: #64748B;">📅 Set date range above, then launch any of the 7 report suites below.</span>
                </div>
            </div>

            <!-- 7 Executive Report Cards Grid -->
            <div class="reports-grid">
                
                <!-- 1. Doctor Demand -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #0284C7;">REPORT 01</div>
                        <h4 class="report-card-title">👨‍⚕️ Doctor Booking Demand & Revenue</h4>
                        <p class="report-card-desc">Ranks dentists by total bookings, completed consults, consultation revenue, and token capacity utilization.</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600;" onclick="launchReport('DOCTOR_DEMAND')">🖨️ Launch Doctor Demand Report</button>
                </div>

                <!-- 2. Procedure Usage -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #059669;">REPORT 02</div>
                        <h4 class="report-card-title">🩺 Most Performed Treatment Procedures</h4>
                        <p class="report-card-desc">Ranks dental procedures (Extractions, Fillings, Whitening) by volume, gross revenue, average cost, and percentage share.</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600; background-color: #059669; border-color: #047857;" onclick="launchReport('PROCEDURE_USAGE')">🖨️ Launch Procedure Report</button>
                </div>

                <!-- 3. Financial Summary -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #7C3AED;">REPORT 03</div>
                        <h4 class="report-card-title">📊 Financial Settlement Statement</h4>
                        <p class="report-card-desc">Breakdown of Stage 1 initial deposits, Stage 2 procedure collections, gross billings, and payment methods (Cash/Card/Bank).</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600; background-color: #7C3AED; border-color: #6D28D9;" onclick="launchReport('FINANCIAL_SUMMARY')">🖨️ Launch Financial Statement</button>
                </div>

                <!-- 4. Patient Demographics -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #D97706;">REPORT 04</div>
                        <h4 class="report-card-title">😷 Patient Demographics & Growth</h4>
                        <p class="report-card-desc">Patient growth, age distributions (<18, 18-59, 60+), gender breakdowns, and top recurring patient registers.</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600; background-color: #D97706; border-color: #B45309;" onclick="launchReport('PATIENT_DEMOGRAPHICS')">🖨️ Launch Demographics Report</button>
                </div>

                <!-- 5. Cancellation Audit -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #DC2626;">REPORT 05</div>
                        <h4 class="report-card-title">🎟️ Cancellation & Deposit Audit</h4>
                        <p class="report-card-desc">Status log breakdown (Booked, Completed, Rescheduled, Cancelled), cancellation rates, and deposit carry-over status.</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600; background-color: #DC2626; border-color: #B91C1C;" onclick="launchReport('CANCELLATION_AUDIT')">🖨️ Launch Cancellation Audit</button>
                </div>

                <!-- 6. Session Utilization -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #2563EB;">REPORT 06</div>
                        <h4 class="report-card-title">⏰ Peak Hours & Session Utilization</h4>
                        <p class="report-card-desc">Analysis of time blocks (Morning, Afternoon, Evening), token utilization rates, and peak doctor density windows.</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600; background-color: #2563EB; border-color: #1D4ED8;" onclick="launchReport('SESSION_UTILIZATION')">🖨️ Launch Session Utilization</button>
                </div>

                <!-- 7. Outstanding Dues -->
                <div class="report-select-card">
                    <div>
                        <div class="report-card-badge" style="color: #BE185D;">REPORT 07</div>
                        <h4 class="report-card-title">💳 Outstanding & Unpaid Dues Statement</h4>
                        <p class="report-card-desc">Roster of accounts with un-settled procedure balances, contact details, appointment dates, and net balance due.</p>
                    </div>
                    <button type="button" class="btn-primary" style="width: 100%; font-size: 0.85rem; font-weight: 600; background-color: #BE185D; border-color: #9D174D;" onclick="launchReport('OUTSTANDING_DUES')">🖨️ Launch Outstanding Dues</button>
                </div>

            </div>
        </div>

        <script>
            function launchReport(type) {
                const startDate = document.getElementById("globalReportStartDate").value || "";
                const endDate = document.getElementById("globalReportEndDate").value || "";
                let url = "${pageContext.request.contextPath}/admin/reports/view?type=" + type;
                if (startDate) url += "&startDate=" + startDate;
                if (endDate) url += "&endDate=" + endDate;
                window.open(url, '_blank');
            }
        </script>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem;">
            <div class="card-panel">
                <h4 style="color: var(--primary); margin-bottom: 1rem;">Revenue & Financial Breakdown</h4>
                <ul style="list-style: none; padding: 0; line-height: 2rem; font-size: 0.95rem;">
                    <li><strong>Est. Total Daily Revenue:</strong> LKR 12,500.00</li>
                    <li><strong>Consultation Revenue:</strong> LKR 7,500.00</li>
                    <li><strong>Procedure Base Revenue:</strong> LKR 5,000.00</li>
                    <li><strong>Active Billing Accounts:</strong> <c:out value="${patients.size()}"/> Active Patients</li>
                </ul>
            </div>
            <div class="card-panel">
                <h4 style="color: var(--primary); margin-bottom: 1rem;">Clinic Staff & Resource Allocation</h4>
                <ul style="list-style: none; padding: 0; line-height: 2rem; font-size: 0.95rem;">
                    <li><strong>Active Dentists on Duty:</strong> <c:out value="${dentists.size()}"/> Specialists</li>
                    <li><strong>Today's Total Bookings:</strong> <c:out value="${todayAppointments.size()}"/> Appointments</li>
                    <li><strong>System Security Mode:</strong> BCrypt Hashed Passwords</li>
                    <li><strong>Database Provider:</strong> Spring Data JPA / Hibernate</li>
                </ul>
            </div>
        </div>

        <!-- Staff & Receptionist Provisioning Panel -->
        <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 1.5rem; margin-top: 1.5rem;">
            
            <!-- Col 1: Register New Staff Member / Receptionist Form -->
            <div class="card-panel">
                <h3 style="margin-bottom: 1rem; color: var(--primary);">👤 Register New Staff / Receptionist</h3>
                <p style="color: var(--text-muted); font-size: 0.85rem; margin-bottom: 1.25rem;">Provision staff credentials. An automated email with password setup instructions will be sent to the user.</p>

                <form action="${pageContext.request.contextPath}/admin/users/save" method="post">
                    <div class="form-group">
                        <label>Full Name</label>
                        <input type="text" name="fullName" class="form-control" placeholder="e.g. Kavindi Perera" required>
                    </div>

                    <div class="form-group">
                        <label>Login Username</label>
                        <input type="text" name="username" class="form-control" placeholder="e.g. recept2" required>
                    </div>

                    <div class="form-group">
                        <label>Email Address</label>
                        <input type="email" name="email" class="form-control" placeholder="e.g. receptionist2@sunrisedental.com" required>
                    </div>

                    <div class="form-group">
                        <label>System Access Role</label>
                        <select name="role" class="form-control">
                            <option value="Receptionist" selected>Receptionist</option>
                            <option value="Admin">System Administrator</option>
                        </select>
                    </div>

                    <button type="submit" class="btn-primary" style="width: 100%; margin-top: 0.5rem;">+ Add Staff Account</button>
                </form>
            </div>

            <!-- Col 2: Active Staff Users Roster Table -->
            <div class="card-panel">
                <h3 style="margin-bottom: 1rem; color: var(--primary);">👥 Active Staff Directory & Receptionist Accounts</h3>
                <div class="table-responsive">
                    <table class="data-table" id="staffUsersTable">
                        <thead>
                            <tr>
                                <th>User ID & Username</th>
                                <th>Full Name</th>
                                <th>System Role</th>
                                <th>Email</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${staffUsers}" var="u">
                                <tr>
                                    <td>
                                        <strong><c:out value="${u.username}"/></strong>
                                        <br><small style="color: var(--text-muted);">ID #<c:out value="${u.userId}"/></small>
                                    </td>
                                    <td><c:out value="${u.fullName}"/></td>
                                    <td>
                                        <span class="badge ${u.role == 'Admin' ? 'badge-admin' : 'badge-receptionist'}">
                                            <c:out value="${u.role}"/>
                                        </span>
                                    </td>
                                    <td><c:out value="${u.email}" default="N/A"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.isActive}">
                                                <span style="color: #059669; font-weight: 600;">● Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #DC2626; font-weight: 600;">○ Disabled</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="display: flex; gap: 0.35rem; align-items: center;">
                                            <form action="${pageContext.request.contextPath}/admin/users/reset-password/${u.userId}" method="post" style="display: inline;">
                                                <button type="submit" class="btn-secondary" style="padding: 0.25rem 0.55rem; font-size: 0.78rem; background-color: #0284C7; border-color: #0369A1; color: white; font-weight: 600; cursor: pointer;" title="Trigger password reset link email">🔑 Reset Password</button>
                                            </form>

                                            <form action="${pageContext.request.contextPath}/admin/users/toggle/${u.userId}" method="post" style="display: inline;">
                                                <c:choose>
                                                    <c:when test="${u.isActive}">
                                                        <button type="submit" class="btn-logout" style="padding: 0.25rem 0.55rem; font-size: 0.78rem; background-color: #EF4444; color: #FFFFFF !important; font-weight: 700; border: none; border-radius: 4px; cursor: pointer;">🚫 Disable</button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="submit" class="btn-primary" style="padding: 0.25rem 0.55rem; font-size: 0.78rem; background-color: #059669; border-color: #047857; font-weight: 700; cursor: pointer;">✅ Activate</button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty staffUsers}">
                                <tr>
                                    <td colspan="6" style="text-align: center; color: var(--text-muted);">No staff users registered yet.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</c:if>
