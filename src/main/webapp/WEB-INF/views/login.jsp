<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic - Staff Login</title>
    <!-- External Shared Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="center-container">

<div class="login-card">
    <div class="brand-header">
        <h1>Sunrise Dental Clinic</h1>
        <p>Staff Portal & Management System</p>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="error-alert">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" class="form-control" required placeholder="Enter username">
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" class="form-control" required placeholder="Enter password">
        </div>

        <button type="submit" class="btn-submit">Sign In to Dashboard</button>
    </form>

    <div class="credentials-box">
        <strong>Demo Login Credentials:</strong><br>
        • Receptionist: <code>receptionist</code> / <code>recept123</code><br>
        • Administrator: <code>admin</code> / <code>admin123</code>
    </div>
</div>

</body>
</html>
