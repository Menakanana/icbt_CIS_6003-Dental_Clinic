<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- TAB 4: ADMIN REPORTS & ANALYTICS COMPONENT -->
<c:if test="${user.role == 'Admin'}">
    <div id="admin-tab" class="tab-content">
        <div class="card-panel" style="margin-bottom: 1.5rem;">
            <h3 style="margin-bottom: 0.5rem; color: var(--primary);">📊 Executive Analytics & Performance Summary</h3>
            <p style="color: var(--text-muted); font-size: 0.9rem;">Operational metrics, daily revenue overview, and system audit log status.</p>
        </div>

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
                        <label>Account Password</label>
                        <input type="text" class="form-control" value="✉️ Setup link sent to staff email" readonly style="background: #F8FAFC; color: #475569; font-size: 0.9rem; font-style: italic;">
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
                                </tr>
                            </c:forEach>
                            <c:if test="${empty staffUsers}">
                                <tr>
                                    <td colspan="5" style="text-align: center; color: var(--text-muted);">No staff users registered yet.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</c:if>
