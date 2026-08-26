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

    <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm" onsubmit="return validateLoginForm()">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" class="form-control" required placeholder="Enter username" onblur="validateUsernameInput()" oninput="clearFieldError(this, document.getElementById('usernameErrorMsg'))">
            <span class="field-error-msg" id="usernameErrorMsg" style="display:none;"></span>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <div style="position: relative; display: flex; align-items: center;">
                <input type="password" id="password" name="password" class="form-control" required placeholder="Enter password" style="padding-right: 42px;" onblur="validatePasswordInput()" oninput="clearFieldError(this, document.getElementById('passwordErrorMsg'))">
                <button type="button" id="togglePasswordBtn" onclick="togglePasswordVisibility()" title="Show/Hide Password" style="position: absolute; right: 10px; background: none; border: none; cursor: pointer; font-size: 1.1rem; color: var(--text-muted); padding: 2px;">👁️</button>
            </div>
            <span class="field-error-msg" id="passwordErrorMsg" style="display:none;"></span>
        </div>

        <button type="submit" class="btn-submit">Sign In to Dashboard</button>

        <div class="forgot-link-container">
            <a href="javascript:void(0)" onclick="openForgotPasswordModal()">Forgot Password?</a>
        </div>
    </form>
</div>

<!-- Password Reset Modal -->
<div id="forgotPasswordModal" class="modal-overlay" style="display: none;">
    <div class="modal-card">
        <button class="modal-close-btn" onclick="closeForgotPasswordModal()">&times;</button>
        <h3>Reset Your Password</h3>
        <p>Enter your registered email address or staff username. We will send a password reset link to your email.</p>
        
        <div id="resetFeedback" class="alert-info-custom"></div>

        <div class="form-group">
            <label for="resetEmailInput">Registered Email / Username</label>
            <input type="text" id="resetEmailInput" class="form-control" placeholder="e.g. admin@sunrisedental.com or admin" onblur="validateResetInput()" oninput="clearFieldError(this, document.getElementById('resetEmailErrorMsg'))">
            <span class="field-error-msg" id="resetEmailErrorMsg" style="display:none;"></span>
        </div>

        <div style="display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px;">
            <button type="button" onclick="closeForgotPasswordModal()" style="padding: 8px 16px; border: 1px solid #ccc; background: #f5f5f5; border-radius: 6px; cursor: pointer;">Cancel</button>
            <button type="button" id="sendResetBtn" onclick="submitPasswordReset()" class="btn-submit" style="width: auto; padding: 8px 20px;">Send Reset Link</button>
        </div>
    </div>
</div>

<script>
    function showFieldError(inputEl, errorEl, message) {
        if (inputEl) {
            inputEl.classList.add('is-invalid');
            inputEl.classList.remove('is-valid');
        }
        if (errorEl) {
            errorEl.innerText = message;
            errorEl.style.display = 'block';
        }
    }

    function clearFieldError(inputEl, errorEl) {
        if (inputEl) {
            inputEl.classList.remove('is-invalid');
            if (inputEl.value && inputEl.value.trim() !== '') {
                inputEl.classList.add('is-valid');
            } else {
                inputEl.classList.remove('is-valid');
            }
        }
        if (errorEl) {
            errorEl.innerText = '';
            errorEl.style.display = 'none';
        }
    }

    function validateUsernameInput() {
        const input = document.getElementById('username');
        const err = document.getElementById('usernameErrorMsg');
        if (!input) return true;
        if (!input.value.trim()) {
            showFieldError(input, err, "⚠️ Username is required.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validatePasswordInput() {
        const input = document.getElementById('password');
        const err = document.getElementById('passwordErrorMsg');
        if (!input) return true;
        if (!input.value) {
            showFieldError(input, err, "⚠️ Password is required.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function validateLoginForm() {
        let valid = true;
        if (!validateUsernameInput()) valid = false;
        if (!validatePasswordInput()) valid = false;
        return valid;
    }

    function validateResetInput() {
        const input = document.getElementById('resetEmailInput');
        const err = document.getElementById('resetEmailErrorMsg');
        if (!input) return true;
        if (!input.value.trim()) {
            showFieldError(input, err, "⚠️ Registered email address or username is required.");
            return false;
        }
        clearFieldError(input, err);
        return true;
    }

    function togglePasswordVisibility() {
        const pwdInput = document.getElementById('password');
        const toggleBtn = document.getElementById('togglePasswordBtn');
        if (pwdInput.type === 'password') {
            pwdInput.type = 'text';
            toggleBtn.innerText = '🙈';
        } else {
            pwdInput.type = 'password';
            toggleBtn.innerText = '👁️';
        }
    }

    function openForgotPasswordModal() {
        document.getElementById('forgotPasswordModal').style.display = 'flex';
        document.getElementById('resetFeedback').style.display = 'none';
        document.getElementById('resetEmailInput').value = '';
    }

    function closeForgotPasswordModal() {
        document.getElementById('forgotPasswordModal').style.display = 'none';
    }

    async function submitPasswordReset() {
        const emailInput = document.getElementById('resetEmailInput').value.trim();
        const feedbackDiv = document.getElementById('resetFeedback');
        const sendBtn = document.getElementById('sendResetBtn');

        if (!emailInput) {
            feedbackDiv.style.display = 'block';
            feedbackDiv.style.backgroundColor = '#ffebee';
            feedbackDiv.style.color = '#c62828';
            feedbackDiv.style.borderColor = '#ffcdd2';
            feedbackDiv.innerText = 'Please enter your registered email address or username.';
            return;
        }

        sendBtn.disabled = true;
        sendBtn.innerText = 'Sending...';

        try {
            const response = await fetch('${pageContext.request.contextPath}/api/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailInput })
            });

            const data = await response.json();
            feedbackDiv.style.display = 'block';
            feedbackDiv.style.backgroundColor = '#e8f5e9';
            feedbackDiv.style.color = '#2e7d32';
            feedbackDiv.style.borderColor = '#c8e6c9';
            feedbackDiv.innerText = data.message || 'Password reset link has been dispatched to your email.';
        } catch (err) {
            feedbackDiv.style.display = 'block';
            feedbackDiv.style.backgroundColor = '#ffebee';
            feedbackDiv.style.color = '#c62828';
            feedbackDiv.style.borderColor = '#ffcdd2';
            feedbackDiv.innerText = 'An error occurred while dispatching reset request. Please try again.';
        } finally {
            sendBtn.disabled = false;
            sendBtn.innerText = 'Send Reset Link';
        }
    }
</script>

</body>
</html>
