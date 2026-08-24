<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!-- DOCTOR PROFILES & DIRECTORY COMPONENT -->
<div id="doctors-tab" class="tab-content">
    
    <!-- Register / Configure Doctor Profile Form -->
    <div class="card-panel" style="margin-bottom: 1.5rem; background-color: #F8FAFC; border: 1px solid var(--border-color);">
        <h3 style="margin-bottom: 1rem; color: var(--primary); font-size: 1.1rem;">🩺 Register / Add New Doctor Profile</h3>
        
        <form action="${pageContext.request.contextPath}/dentists/save" method="post">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem;">
                <div class="form-group">
                    <label>Doctor Full Name *</label>
                    <input type="text" name="dentistName" class="form-control" required placeholder="e.g. Dr. Ruwan Jayasinghe">
                </div>
                <div class="form-group">
                    <label>Specialization / Clinical Area *</label>
                    <input type="text" name="specialization" class="form-control" required placeholder="e.g. Orthodontics & Restorative">
                </div>
                <div class="form-group">
                    <label>Consultation Fee (LKR) *</label>
                    <input type="number" name="consultationFee" class="form-control" step="100" min="0.01" required placeholder="e.g. 2000.00">
                </div>
                <div class="form-group">
                    <label>Contact Phone Number *</label>
                    <input type="text" name="contactNumber" class="form-control" required placeholder="e.g. 0773334455">
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
