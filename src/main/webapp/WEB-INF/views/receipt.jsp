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
    <style>
        body {
            background-color: #F1F5F9;
            padding: 2rem 1rem;
        }
        .receipt-card {
            max-width: 650px;
            margin: 0 auto;
            background: #FFFFFF;
            padding: 2.5rem;
            border-radius: var(--radius-md);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            border: 1px solid var(--border-color);
        }
        .receipt-header {
            text-align: center;
            border-bottom: 2px dashed var(--border-color);
            padding-bottom: 1.5rem;
            margin-bottom: 1.5rem;
        }
        .receipt-header h1 {
            color: var(--primary);
            font-size: 1.6rem;
            margin-bottom: 0.25rem;
        }
        .receipt-header p {
            color: var(--text-muted);
            font-size: 0.85rem;
        }
        .invoice-meta {
            display: flex;
            justify-content: space-between;
            margin-bottom: 1.5rem;
            font-size: 0.9rem;
        }
        .itemized-table {
            width: 100%;
            border-collapse: collapse;
            margin: 1.5rem 0;
        }
        .itemized-table th, .itemized-table td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
            font-size: 0.9rem;
        }
        .itemized-table th {
            background-color: #F8FAFC;
            color: var(--primary);
        }
        .total-row {
            font-weight: 700;
            font-size: 1.1rem;
            color: var(--primary);
        }
        .print-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 2rem;
            padding-top: 1rem;
            border-top: 1px solid var(--border-color);
        }
        .status-toast {
            margin-top: 1rem;
            padding: 0.75rem;
            border-radius: 6px;
            font-size: 0.85rem;
            display: none;
            text-align: center;
        }
        .status-success {
            background-color: #D1FAE5;
            color: #065F46;
            border: 1px solid #A7F3D0;
        }
        .status-error {
            background-color: #FEE2E2;
            color: #991B1B;
            border: 1px solid #FECACA;
        }
        @media print {
            body { background-color: #FFF; padding: 0; }
            .receipt-card { box-shadow: none; border: none; padding: 0; width: 100%; max-width: 100%; }
            .print-actions, .status-toast { display: none !important; }
        }
    </style>
</head>
<body>

<div class="receipt-card" id="receiptContent">
    <div class="receipt-header">
        <h1>Sunrise Dental Clinic</h1>
        <p>45 Galle Road, Colombo 03 | Tel: +94 11 234 5678</p>
        <c:choose>
            <c:if test="${bill.billStage == 'INITIAL_DEPOSIT'}">
                <span class="badge badge-admin" style="font-size: 0.85rem; padding: 0.4rem 0.8rem; background-color: #0284C7; border-color: #0369A1;">🎟️ STAGE 1: INITIAL BOOKING DEPOSIT RECEIPT</span>
            </c:if>
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
            <strong>Appointment ID:</strong> APT-<c:out value="${bill.appointmentId}"/><br>
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
                <td style="text-align: right;"><fmt:formatNumber value="${bill.consultationFee}" type="currency" currencySymbol=""/></td>
            </tr>
            <tr>
                <td>Clinic Charge</td>
                <td>Facility & Administrative Fee</td>
                <td style="text-align: right;"><fmt:formatNumber value="${bill.clinicCharge}" type="currency" currencySymbol=""/></td>
            </tr>
            <c:if test="${bill.billStage != 'INITIAL_DEPOSIT'}">
                <c:choose>
                    <c:when test="${not empty bill.treatmentItems}">
                        <c:forEach items="${bill.treatmentItems}" var="item">
                            <tr>
                                <td><c:out value="${item.name}"/></td>
                                <td>Treatment / Procedure Item</td>
                                <td style="text-align: right;"><fmt:formatNumber value="${item.cost}" type="currency" currencySymbol=""/></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:when test="${not empty bill.treatmentName}">
                        <tr>
                            <td><c:out value="${bill.treatmentName}"/></td>
                            <td>Procedure / Treatment Base Charge</td>
                            <td style="text-align: right;"><fmt:formatNumber value="${bill.treatmentBaseCost}" type="currency" currencySymbol=""/></td>
                        </tr>
                    </c:when>
                </c:choose>
            </c:if>
            <c:if test="${bill.previousPaidAmount > 0}">
                <tr style="color: #0284C7; font-weight: 600;">
                    <td colspan="2">Less: Previous Deposit Paid (Initial Booking Ticket)</td>
                    <td style="text-align: right;">- <fmt:formatNumber value="${bill.previousPaidAmount}" type="currency" currencySymbol=""/></td>
                </tr>
            </c:if>
            <c:if test="${bill.discountAmount > 0}">
                <tr style="color: var(--accent-green);">
                    <td colspan="2">Less: Special Discount / Adjustment</td>
                    <td style="text-align: right;">- <fmt:formatNumber value="${bill.discountAmount}" type="currency" currencySymbol=""/></td>
                </tr>
            </c:if>
            <tr class="total-row">
                <td colspan="2" style="text-align: right;">
                    <c:choose>
                        <c:when test="${bill.billStage == 'INITIAL_DEPOSIT'}">INITIAL DEPOSIT TOTAL PAID:</c:when>
                        <c:otherwise>NET BALANCE PAID & SETTLED:</c:otherwise>
                    </c:choose>
                </td>
                <td style="text-align: right; color: #047857;">LKR <fmt:formatNumber value="${bill.billStage == 'INITIAL_DEPOSIT' ? bill.totalAmount : bill.netBalanceDue}" type="currency" currencySymbol=""/></td>
            </tr>
        </tbody>
    </table>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.85rem; color: var(--text-muted);">
        <p>Thank you for visiting Sunrise Dental Clinic!</p>
        <p>Wish you a healthy smile. Keep this receipt for claim purposes.</p>
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
            margin: 0.3,
            filename: 'Receipt_INV_${bill.invoiceNumber}.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 2 },
            jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
        };
        html2pdf().set(opt).from(element).save();
    }

    function sendEmailReceipt() {
        const toast = document.getElementById("statusToast");
        let recipientEmail = "";

        // Check if prompt needed
        const promptInput = prompt("Enter patient email address to receive final invoice (Leave blank to use registered email or skip):", "");
        if (promptInput === null) {
            return; // Cancelled
        }
        recipientEmail = promptInput.trim();

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
                toast.className = "status-toast status-success";
                toast.innerText = "✓ " + data.message;
            } else {
                toast.className = "status-toast status-error";
                toast.innerText = "✕ Error: " + (data.message || "Failed to send email.");
            }
        })
        .catch(err => {
            toast.className = "status-toast status-error";
            toast.innerText = "✕ Failed to send receipt email.";
        });
    }
</script>

</body>
</html>
