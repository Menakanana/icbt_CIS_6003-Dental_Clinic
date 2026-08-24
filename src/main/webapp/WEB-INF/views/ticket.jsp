<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Appointment Ticket - Sunrise Dental Clinic</title>
    <style>
        :root {
            --primary: #0F766E;
            --text-main: #1E293B;
            --border: #E2E8F0;
        }

        body {
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
            background-color: #F8FAFC;
            color: var(--text-main);
            padding: 2rem;
            display: flex;
            justify-content: center;
        }

        .ticket-container {
            background: #FFFFFF;
            border: 2px dashed var(--primary);
            border-radius: 12px;
            padding: 2.5rem;
            max-width: 520px;
            width: 100%;
            box-shadow: 0 10px 25px rgba(0,0,0,0.05);
        }

        .header {
            text-align: center;
            border-bottom: 2px solid var(--border);
            padding-bottom: 1rem;
            margin-bottom: 1.5rem;
        }

        .header h1 {
            color: var(--primary);
            margin: 0;
            font-size: 1.6rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .header p {
            margin: 0.25rem 0 0 0;
            font-size: 0.85rem;
            color: #64748B;
        }

        .token-badge {
            background-color: #ECFDF5;
            border: 2px solid #A7F3D0;
            color: #065F46;
            text-align: center;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }

        .token-badge .token-num {
            font-size: 2.5rem;
            font-weight: 800;
            line-height: 1;
            margin: 0.25rem 0;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
            margin-bottom: 1.5rem;
            font-size: 0.9rem;
        }

        .info-label {
            font-size: 0.75rem;
            text-transform: uppercase;
            color: #64748B;
            font-weight: 600;
        }

        .info-value {
            font-weight: 700;
            color: var(--text-main);
            margin-top: 0.15rem;
        }

        .fee-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 1.5rem;
            font-size: 0.9rem;
        }

        .fee-table th, .fee-table td {
            padding: 0.6rem;
            text-align: left;
            border-bottom: 1px solid var(--border);
        }

        .fee-table td:last-child {
            text-align: right;
            font-weight: 600;
        }

        .total-row td {
            font-size: 1.1rem;
            font-weight: 800;
            color: var(--primary);
            border-top: 2px solid var(--primary);
            border-bottom: none;
        }

        .actions {
            text-align: center;
            margin-top: 1.5rem;
        }

        .btn-print {
            background-color: var(--primary);
            color: #FFFFFF;
            border: none;
            padding: 0.75rem 2rem;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            font-size: 0.95rem;
        }

        @media print {
            body { background: #FFF; padding: 0; }
            .ticket-container { border: 2px solid #000; box-shadow: none; }
            .actions { display: none; }
        }
    </style>
</head>
<body>

<div class="ticket-container">
    <div class="header">
        <h1>Sunrise Dental Clinic</h1>
        <p>Pre-Consultation Appointment Ticket & Queue Pass</p>
        <p>123 Galle Road, Colombo 03 | Tel: 011-2345678</p>
    </div>

    <div class="token-badge">
        <div style="font-size: 0.85rem; font-weight: 600; text-transform: uppercase;">Queue Token Number</div>
        <div class="token-num">#<c:out value="${ticket.tokenNumber}"/></div>
        <div style="font-size: 0.85rem; font-weight: 600;"><c:out value="${ticket.displayTimeRange}"/></div>
    </div>

    <div class="info-grid">
        <div>
            <div class="info-label">Appointment ID</div>
            <div class="info-value">APT-<c:out value="${ticket.appointmentId}"/></div>
        </div>
        <div>
            <div class="info-label">Date</div>
            <div class="info-value"><c:out value="${ticket.appointmentDate}"/></div>
        </div>
        <div>
            <div class="info-label">Patient Name</div>
            <div class="info-value"><c:out value="${ticket.patientName}"/></div>
        </div>
        <div>
            <div class="info-label">Contact Phone</div>
            <div class="info-value"><c:out value="${ticket.contactNumber}"/></div>
        </div>
        <div style="grid-column: 1 / -1;">
            <div class="info-label">Assigned Dentist</div>
            <div class="info-value"><c:out value="${ticket.dentistName}"/> (<c:out value="${ticket.specialization}"/>)</div>
        </div>
    </div>

    <table class="fee-table">
        <thead>
            <tr>
                <th>Item Description</th>
                <th style="text-align: right;">Amount</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Doctor Consultation Fee</td>
                <td>LKR <fmt:formatNumber value="${ticket.consultationFee}" type="currency" currencySymbol=""/></td>
            </tr>
            <tr>
                <td>Clinic Charge (Static Facility Fee)</td>
                <td>LKR 500.00</td>
            </tr>
            <tr class="total-row">
                <td>Initial Pre-Consultation Deposit</td>
                <td>LKR <fmt:formatNumber value="${ticket.consultationFee.add(500)}" type="currency" currencySymbol=""/></td>
            </tr>
        </tbody>
    </table>

    <div style="text-align: center; font-size: 0.8rem; color: #64748B;">
        <p>Please present this ticket to the clinic receptionist upon entry.</p>
        <p>Thank you for choosing Sunrise Dental Clinic!</p>
    </div>

    <div class="actions">
        <button class="btn-print" onclick="window.print()">🖨️ Print Appointment Ticket</button>
        <a href="${pageContext.request.contextPath}/dashboard" style="margin-left: 1rem; color: var(--primary); font-size: 0.9rem; text-decoration: none;">← Return to Dashboard</a>
    </div>
</div>

<script>
    // Auto trigger print dialog on page load
    window.onload = function() {
        setTimeout(function() {
            window.print();
        }, 400);
    };
</script>

</body>
</html>
