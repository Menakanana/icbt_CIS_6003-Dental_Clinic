<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic - Reset Password</title>
    <!-- External Shared Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .password-toggle-btn {
            position: absolute; right: 10px; background: none; border: none;
            cursor: pointer; font-size: 1.1rem; color: var(--text-muted); padding: 2px;
        }
        .alert-custom {
            padding: 12px 16px;
            border-radius: 6px;
            font-size: 0.9rem;
            margin-bottom: 18px;
            display: none;
        }
        .rule-item {
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.2s ease;
        }
        .rule-item.valid {
            color: #059669;
            font-weight: 600;
        }
        .rule-item.invalid {
            color: #94a3b8;
        }
        .rule-icon {
            font-size: 0.9rem;
        }
    </style>
</head>
<body class="center-container">

<div class="login-card" style="max-width: 460px;">
    <div class="brand-header">
        <h1>Sunrise Dental Clinic</h1>
        <p>Set Your New Account Password</p>
    </div>

    <c:if test="${not empty errorMessage or not isValidToken}">
        <div class="error-alert" style="display: block; margin-bottom: 20px;">
            <c:out value="${errorMessage}"/>
        </div>
        <div style="text-align: center; margin-top: 15px;">
            <a href="${pageContext.request.contextPath}/login" class="btn-submit" style="display: inline-block; text-decoration: none;">Return to Login</a>
        </div>
    </c:if>

    <c:if test="${isValidToken}">
        <div id="resetFeedbackAlert" class="alert-custom"></div>

        <form id="resetPasswordForm" onsubmit="handleResetSubmit(event)">
            <input type="hidden" id="tokenInput" value="${token}">

            <div class="form-group">
                <label for="newPassword">New Password</label>
                <div style="position: relative; display: flex; align-items: center;">
                    <input type="password" id="newPassword" class="form-control" required placeholder="Enter new password" style="padding-right: 42px;" oninput="checkComplexityLive()">
                    <button type="button" onclick="toggleVisibility('newPassword', this)" class="password-toggle-btn" title="Show/Hide">👁️</button>
                </div>

                <!-- Real-time Password Complexity Checklist -->
                <div class="password-complexity-card" style="background: #f8fafc; border: 1px solid #cbd5e1; border-radius: 8px; padding: 12px 14px; margin-top: 10px; font-size: 0.85rem; color: #475569;">
                    <div style="font-weight: 600; margin-bottom: 6px; color: #334155;">Password Complexity Requirements:</div>
                    <ul id="complexityChecklist" style="list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 5px;">
                        <li id="rule-length" class="rule-item invalid"><span class="rule-icon">❌</span> Minimum 8 characters</li>
                        <li id="rule-upper" class="rule-item invalid"><span class="rule-icon">❌</span> At least 1 Uppercase letter (A-Z)</li>
                        <li id="rule-lower" class="rule-item invalid"><span class="rule-icon">❌</span> At least 1 Lowercase letter (a-z)</li>
                        <li id="rule-number" class="rule-item invalid"><span class="rule-icon">❌</span> At least 1 Number (0-9)</li>
                        <li id="rule-symbol" class="rule-item invalid"><span class="rule-icon">❌</span> At least 1 Special Symbol (@ # $ % ^ & * ! _ ~ + = - ? .)</li>
                    </ul>
                </div>
            </div>

            <div class="form-group" style="margin-top: 16px;">
                <label for="confirmPassword">Confirm New Password</label>
                <div style="position: relative; display: flex; align-items: center;">
                    <input type="password" id="confirmPassword" class="form-control" required placeholder="Re-enter new password" style="padding-right: 42px;">
                    <button type="button" onclick="toggleVisibility('confirmPassword', this)" class="password-toggle-btn" title="Show/Hide">👁️</button>
                </div>
            </div>

            <button type="submit" id="submitResetBtn" class="btn-submit">Reset & Update Password</button>
        </form>

        <div id="successActionContainer" style="display: none; text-align: center; margin-top: 20px;">
            <a href="${pageContext.request.contextPath}/login" class="btn-submit" style="display: block; text-decoration: none; background-color: #2e7d32;">Proceed to Sign In</a>
        </div>
    </c:if>
</div>

<script>
    const ALLOWED_SYMBOLS_REGEX = /[@#$%^&*!_~+=\-?.]/;

    function checkComplexityLive() {
        const pwd = document.getElementById('newPassword').value;
        const isLengthValid = pwd.length >= 8;
        const isUpperValid = /[A-Z]/.test(pwd);
        const isLowerValid = /[a-z]/.test(pwd);
        const isNumberValid = /[0-9]/.test(pwd);
        const isSymbolValid = ALLOWED_SYMBOLS_REGEX.test(pwd);

        updateRuleUI('rule-length', isLengthValid);
        updateRuleUI('rule-upper', isUpperValid);
        updateRuleUI('rule-lower', isLowerValid);
        updateRuleUI('rule-number', isNumberValid);
        updateRuleUI('rule-symbol', isSymbolValid);

        return isLengthValid && isUpperValid && isLowerValid && isNumberValid && isSymbolValid;
    }

    function updateRuleUI(elementId, isValid) {
        const el = document.getElementById(elementId);
        if (!el) return;
        const icon = el.querySelector('.rule-icon');
        if (isValid) {
            el.classList.remove('invalid');
            el.classList.add('valid');
            if (icon) icon.innerText = '✓';
        } else {
            el.classList.remove('valid');
            el.classList.add('invalid');
            if (icon) icon.innerText = '❌';
        }
    }

    function toggleVisibility(inputId, btn) {
        const input = document.getElementById(inputId);
        if (input.type === 'password') {
            input.type = 'text';
            btn.innerText = '🙈';
        } else {
            input.type = 'password';
            btn.innerText = '👁️';
        }
    }

    async function handleResetSubmit(e) {
        e.preventDefault();

        const token = document.getElementById('tokenInput').value;
        const newPassword = document.getElementById('newPassword').value.trim();
        const confirmPassword = document.getElementById('confirmPassword').value.trim();
        const alertDiv = document.getElementById('resetFeedbackAlert');
        const submitBtn = document.getElementById('submitResetBtn');

        if (!checkComplexityLive()) {
            showAlert('Please satisfy all password complexity requirements (marked with ✓) before proceeding.', false);
            return;
        }

        if (newPassword !== confirmPassword) {
            showAlert('New password and confirmation password do not match.', false);
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerText = 'Updating Password...';

        try {
            const response = await fetch('${pageContext.request.contextPath}/api/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ token: token, newPassword: newPassword })
            });

            const data = await response.json();

            if (response.ok && data.success) {
                showAlert(data.message, true);
                document.getElementById('resetPasswordForm').style.display = 'none';
                document.getElementById('successActionContainer').style.display = 'block';
            } else {
                showAlert(data.message || 'Failed to reset password.', false);
                submitBtn.disabled = false;
                submitBtn.innerText = 'Reset & Update Password';
            }
        } catch (err) {
            showAlert('Network error occurred. Please try again.', false);
            submitBtn.disabled = false;
            submitBtn.innerText = 'Reset & Update Password';
        }
    }

    function showAlert(msg, isSuccess) {
        const alertDiv = document.getElementById('resetFeedbackAlert');
        alertDiv.style.display = 'block';
        if (isSuccess) {
            alertDiv.style.backgroundColor = '#e8f5e9';
            alertDiv.style.color = '#2e7d32';
            alertDiv.style.border = '1px solid #c8e6c9';
        } else {
            alertDiv.style.backgroundColor = '#ffebee';
            alertDiv.style.color = '#c62828';
            alertDiv.style.border = '1px solid #ffcdd2';
        }
        alertDiv.innerText = msg;
    }
</script>

</body>
</html>
