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
            margin-top: 2rem;
            padding-top: 1rem;
            border-top: 1px solid var(--border-color);
        }
        @media print {
            body { background-color: #FFF; padding: 0; }
            .receipt-card { box-shadow: none; border: none; padding: 0; width: 100%; max-width: 100%; }
            .print-actions { display: none; }
        }
    </style>
</head>
<body>

<div class="receipt-card">
    <div class="receipt-header">
        <h1>Sunrise Dental Clinic</h1>
        <p>45 Galle Road, Colombo 03 | Tel: +94 11 234 5678</p>
        <p><strong>OFFICIAL PAYMENT RECEIPT</strong></p>
    </div>

    <div class="invoice-meta">
        <div>
            <strong>Invoice #:</strong> INV-<c:out value="${bill.invoiceNumber}"/><br>
            <strong>Appointment ID:</strong> APT-<c:out value="${bill.appointmentId}"/><br>
            <strong>Queue Token #:</strong> #<c:out value="${bill.tokenNumber}"/>
        </div>
        <div style="text-align: right;">
            <strong>Date:</strong> <c:out value="${bill.appointmentDate}"/><br>
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
            <tr>
                <td><c:out value="${bill.treatmentName}"/></td>
                <td>Procedure / Treatment Base Charge</td>
                <td style="text-align: right;"><fmt:formatNumber value="${bill.treatmentBaseCost}" type="currency" currencySymbol=""/></td>
            </tr>
            <c:if test="${bill.discountAmount > 0}">
                <tr style="color: var(--accent-green);">
                    <td colspan="2">Special Discount / Adjustment</td>
                    <td style="text-align: right;">- <fmt:formatNumber value="${bill.discountAmount}" type="currency" currencySymbol=""/></td>
                </tr>
            </c:if>
            <tr class="total-row">
                <td colspan="2" style="text-align: right;">TOTAL BILL AMOUNT:</td>
                <td style="text-align: right; color: #047857;">LKR <fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol=""/></td>
            </tr>
        </tbody>
    </table>

    <div style="text-align: center; margin-top: 1.5rem; font-size: 0.85rem; color: var(--text-muted);">
        <p>Thank you for visiting Sunrise Dental Clinic!</p>
        <p>Wish you a healthy smile. Keep this receipt for claim purposes.</p>
    </div>

    <div class="print-actions">
        <a href="${pageContext.request.contextPath}/dashboard" class="btn-logout">← Back to Dashboard</a>
        <button type="button" class="btn-primary" onclick="window.print()">🖨️ Print Bill / Receipt</button>
    </div>
</div>

</body>
</html>
