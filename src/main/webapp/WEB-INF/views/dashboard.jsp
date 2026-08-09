<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic - Staff Dashboard</title>
    <!-- External Shared Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="app-header">
    <div>
        <h1>Sunrise Dental Clinic Management System</h1>
        <p class="user-info">Welcome back, <strong><c:out value="${user.fullName}"/></strong>!</p>
    </div>
    <div>
        <span class="badge"><c:out value="${user.role}"/></span>
        <a href="${pageContext.request.contextPath}/login" class="btn-logout">Logout</a>
    </div>
</div>

<div class="content-container">
    <h2>Staff Operations Panel (JSP View)</h2>
    <p style="margin-top: 1rem; color: var(--muted);">
        Authentication successful! Spring Boot & JSP view rendering engine is active.
    </p>
</div>

</body>
</html>
