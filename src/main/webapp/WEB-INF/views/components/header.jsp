<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Application Header Component -->
<div class="app-header">
    <div class="brand-title">
        <h1>Sunrise Dental Clinic</h1>
        <p>Staff Operations & Management Portal</p>
    </div>
    <div class="user-profile">
        <span>Welcome, <strong><c:out value="${user.fullName != null ? user.fullName : 'Staff Member'}"/></strong></span>
        <span class="badge ${user.role == 'Admin' ? 'badge-admin' : 'badge-receptionist'}">
            <c:out value="${user.role != null ? user.role : 'Receptionist'}"/>
        </span>
        <button type="button" class="btn-logout" onclick="toggleHelpModal()" style="margin-right: 0.5rem; background: #3B82F6; color: #FFF; border-color: #2563EB;">❓ Help Guide</button>
        <a href="${pageContext.request.contextPath}/login" class="btn-logout">Exit / Logout</a>
    </div>
</div>
