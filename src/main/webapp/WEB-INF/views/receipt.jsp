<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Bill / Payment Receipt - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
</head>
<body class="receipt-page-body">

<div class="receipt-card" id="receiptContent">
    <div class="receipt-header">
        <h1>Sunrise Dental Clinic</h1>
        <p>45 Galle Road, Colombo 03 | Tel: +94 11 234 5678</p>
        <c:choose>
            <c:when test="${bill.billStage == 'INITIAL_DEPOSIT'}">
                <span class="badge badge-admin" style="font-size: 0.85rem; padding: 0.4rem 0.8rem; background-color: #0284C7; border-color: #0369A1;">🎟️ STAGE 1: INITIAL BOOKING DEPOSIT RECEIPT</span>
            </c:when>
            <c:otherwise>
                <span class="badge badge-admin" style="font-size: 0.85rem; padding: 0.4rem 0.8rem; background-color: #059669; border-color: #047857;">🧾 STAGE 2: FINAL SETTLED TREATMENT INVOICE</span>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${not empty bill.medicalHistory}">
        <div style="background-color: #FEE2E2; border: 1px solid #FECACA; color: #991B1B; padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1.25rem; font-size: 0.85rem;">
            <strong>⚠️ Patient Medical Alert / Allergy Notes:</strong> <c:out value="${bill.medicalHistory}"/>
        </div>
    </c:if>

    <div class="invoice-meta">
        <div>
            <strong>Invoice #:</strong> INV-<c:out value="${bill.invoiceNumber}"/><br>
            <strong>Appointment No.:</strong> <c:out value="${bill.appointmentId}"/><br>
            <strong>Queue Token #:</strong> #<c:out value="${bill.tokenNumber}"/><br>
            <strong>Payment Method:</strong> <c:out value="${bill.paymentMethod != null ? bill.paymentMethod : 'Cash'}"/>
        </div>
        <div style="text-align: right;">
            <strong>Date:</strong> <c:out value="${bill.formattedAppointmentDate}"/><br>
            <strong>Patient Name:</strong> <c:out value="${bill.patientName}"/><br>
            <strong>Contact:</strong> <c:out value="${bill.patientContact}"/>
        </div>
    </div>

    <table class="itemized-table">
        <thead>
            <tr>
                <th>Description / Service Item</th>
                <th>Doctor / Specialist</th>
                <th style="text-align: right;">Amount (LKR)</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Dentist Consultation Fee</td>
                <td><c:out value="${bill.dentistName}"/> (<c:out value="${bill.dentistSpecialization}"/>)</td>
                <td style="text-align: right;"><fmt:formatNumber value="${bill.consultationFee}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
            <tr>
                <td>Clinic Charge</td>
                <td>Facility & Administrative Fee</td>
                <td style="text-align: right;"><fmt:formatNumber value="${bill.clinicCharge}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
            <c:if test="${bill.billStage != 'INITIAL_DEPOSIT'}">
                <c:choose>
                    <c:when test="${not empty bill.treatmentItems}">
                        <c:forEach items="${bill.treatmentItems}" var="item">
                            <tr>
                                <td><c:out value="${item.name}"/></td>
                                <td>Treatment / Procedure Item</td>
                                <td style="text-align: right;"><fmt:formatNumber value="${item.cost}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:when test="${not empty bill.treatmentName}">
                        <tr>
                            <td><c:out value="${bill.treatmentName}"/></td>
                            <td>Procedure / Treatment Base Charge</td>
                            <td style="text-align: right;"><fmt:formatNumber value="${bill.treatmentBaseCost}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        </tr>
                    </c:when>
                </c:choose>
            </c:if>
            <c:if test="${bill.previousPaidAmount > 0}">
                <tr style="color: #0284C7; font-weight: 600;">
                    <td colspan="2">Less: Previous Deposit Paid (Initial Booking Ticket)</td>
                    <td style="text-align: right;">- <fmt:formatNumber value="${bill.previousPaidAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:if>
            <c:if test="${bill.discountAmount > 0}">
                <tr style="color: var(--accent-green);">
                    <td colspan="2">Less: Special Discount / Adjustment</td>
                    <td style="text-align: right;">- <fmt:formatNumber value="${bill.discountAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:if>
            <tr class="total-row">
                <td colspan="2" style="text-align: right;">
                    <c:choose>
                        <c:when test="${bill.billStage == 'INITIAL_DEPOSIT'}">INITIAL DEPOSIT TOTAL PAID:</c:when>
                        <c:otherwise>NET BALANCE PAID & SETTLED:</c:otherwise>
                    </c:choose>
                </td>
                <td style="text-align: right; color: #047857;">LKR
                    <c:choose>
                        <c:when test="${bill.billStage == 'INITIAL_DEPOSIT'}"><fmt:formatNumber value="${bill.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></c:when>
                        <c:otherwise><fmt:formatNumber value="${bill.netBalanceDue}" minFractionDigits="2" maxFractionDigits="2"/></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </tbody>
    </table>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.85rem; color: var(--text-muted);">
        <p>Thank you for visiting Sunrise Dental Clinic!</p>
        <p>Wish you a healthy smile. Keep this receipt for claim purposes.</p>
    </div>
</div>

<div id="statusToast" class="status-toast"></div>

<div class="print-actions">
    <a href="${pageContext.request.contextPath}/booking/ticket/${bill.appointmentId}" class="btn-secondary" style="text-decoration: none; display: inline-block;">🎟️ Template 1: Booking Pass</a>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn-logout" style="text-decoration: none; margin-left: 0.5rem;">← Dashboard</a>
    <div>
        <button type="button" class="btn-secondary" onclick="downloadPDF()">📄 Download PDF (Template 2)</button>
        <button type="button" class="btn-secondary" onclick="sendEmailReceipt()">✉️ Send Email</button>
        <button type="button" class="btn-primary" onclick="window.print()">🖨️ Print Receipt</button>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get("autoprint") === "true") {
            setTimeout(function () {
                window.print();
            }, 400);
        }
    });

    function downloadPDF() {
        const element = document.getElementById("receiptContent");
        const opt = {
            margin: [0.3, 0.3, 0.3, 0.3],
            filename: 'Receipt_INV_${bill.invoiceNumber}.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { 
                scale: 2, 
                scrollY: 0, 
                scrollX: 0, 
                useCORS: true,
                windowWidth: document.documentElement.offsetWidth
            },
            jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' },
            pagebreak: { mode: ['avoid-all', 'css', 'legacy'] }
        };
        html2pdf().set(opt).from(element).save();
    }

    function sendEmailReceipt() {
        showCustomPrompt(
            "Email Payment Invoice",
            "Enter patient email address to receive final invoice (Leave blank to use registered email or skip):",
            "${bill.patientEmail}",
            function(inputEmail) {
                if (inputEmail === null) return;
                doSendEmailReceipt(inputEmail ? inputEmail.trim() : "");
            }
        );
    }

    function doSendEmailReceipt(recipientEmail) {
        const toast = document.getElementById("statusToast");
        if (toast) {
            toast.className = "status-toast";
            toast.style.display = "block";
            toast.innerText = "Dispatching payment receipt email...";
        }

        let url = "${pageContext.request.contextPath}/api/bills/send-email/${bill.appointmentId}";
        if (recipientEmail) {
            url += "?email=" + encodeURIComponent(recipientEmail);
        }

        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                if (toast) {
                    toast.className = "status-toast status-success";
                    toast.innerText = "✓ " + data.message;
                }
                showToast("✓ " + data.message, "success");
            } else {
                if (toast) {
                    toast.className = "status-toast status-error";
                    toast.innerText = "✕ Error: " + (data.message || "Failed to send email.");
                }
                showToast("✕ Error: " + (data.message || "Failed to send email."), "error");
            }
        })
        .catch(err => {
            if (toast) {
                toast.className = "status-toast status-error";
                toast.innerText = "✕ Failed to send receipt email.";
            }
            showToast("✕ Failed to send receipt email", "error");
        });
    }
</script>

<jsp:include page="components/ui-dialogs.jsp"/>

</body>
</html>
