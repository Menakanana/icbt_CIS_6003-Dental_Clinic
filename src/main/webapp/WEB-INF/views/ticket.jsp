<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Appointment Ticket - Sunrise Dental Clinic</title>
                <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
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
                        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
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

                    .fee-table th,
                    .fee-table td {
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
                        display: flex;
                        flex-wrap: wrap;
                        gap: 0.5rem;
                        justify-content: center;
                        align-items: center;
                    }

                    .btn-action {
                        background-color: var(--primary);
                        color: #FFFFFF;
                        border: none;
                        padding: 0.6rem 1.2rem;
                        border-radius: 6px;
                        font-weight: 600;
                        cursor: pointer;
                        font-size: 0.88rem;
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

                    .status-info {
                        background-color: #E0F2FE;
                        color: #0369A1;
                        border: 1px solid #BAE6FD;
                    }

                    @media print {
                        body {
                            background: #FFF;
                            padding: 0;
                        }

                        .ticket-container {
                            border: 2px solid #000;
                            box-shadow: none;
                        }

                        .actions,
                        .status-toast {
                            display: none !important;
                        }
                    }
                </style>
            </head>

            <body>

                <div class="ticket-container">
                    <div class="header">
                        <span
                            style="font-size: 0.75rem; font-weight: 700; background-color: #CCFBF1; color: #0F766E; padding: 4px 10px; border-radius: 4px; text-transform: uppercase; letter-spacing: 0.5px; display: inline-block; margin-bottom: 0.4rem;">
                            🎟️ TEMPLATE 1: INITIAL BOOKING TICKET & QUEUE PASS
                        </span>
                        <h1 style="margin-top: 0.2rem;">Sunrise Dental Clinic</h1>
                        <p>Pre-Consultation Appointment Ticket & Queue Pass</p>
                        <p>123 Galle Road, Colombo 03 | Tel: 011-2345678</p>
                    </div>

                    <div class="token-badge">
                        <div style="font-size: 0.85rem; font-weight: 600; text-transform: uppercase;">Queue Token Number
                        </div>
                        <div class="token-num">#
                            <c:out value="${ticket.tokenNumber}" />
                        </div>
                        <div style="font-size: 0.85rem; font-weight: 600;">
                            <c:out value="${ticket.displayTimeRange}" />
                        </div>
                        <div style="margin-top: 0.5rem; font-size: 0.85rem; font-weight: 700;">
                            <c:choose>
                                <c:when
                                    test="${ticket.paymentStatus eq 'PAID_DEPOSIT' or ticket.paymentStatus eq 'FULL_PAID'}">
                                    <span
                                        style="color: #065F46; background-color: #A7F3D0; padding: 4px 10px; border-radius: 4px; display: inline-block;">
                                        🎟️ PAYMENT: PAID (LKR
                                        <fmt:formatNumber value="${ticket.paidAmount}" type="currency"
                                            currencySymbol="" /> -
                                        <c:out value="${ticket.paymentMethod}" />)
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span
                                        style="color: #991B1B; background-color: #FECACA; padding: 4px 10px; border-radius: 4px; display: inline-block;">
                                        ⚠️ PAYMENT: UNPAID (Pay Deposit Later)
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="info-grid">
                        <div>
                            <div class="info-label">Appointment No.</div>
                            <div class="info-value">APT-
                                <c:out
                                    value="${ticket.appointmentNumber != null ? ticket.appointmentNumber : ticket.appointmentId}" />
                            </div>
                        </div>
                        <div>
                            <div class="info-label">Date</div>
                            <div class="info-value">
                                <c:out value="${ticket.formattedAppointmentDate}" />
                            </div>
                        </div>
                        <div>
                            <div class="info-label">Patient Name</div>
                            <div class="info-value">
                                <c:out value="${ticket.patientName}" />
                            </div>
                        </div>
                        <div>
                            <div class="info-label">Contact Phone</div>
                            <div class="info-value">
                                <c:out value="${ticket.contactNumber}" />
                            </div>
                        </div>
                        <div style="grid-column: 1 / -1;">
                            <div class="info-label">Assigned Dentist</div>
                            <div class="info-value">
                                <c:out value="${ticket.dentistName}" /> (
                                <c:out value="${ticket.specialization}" />)
                            </div>
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
                                <td>LKR
                                    <fmt:formatNumber value="${ticket.consultationFee}" type="currency"
                                        currencySymbol="" />
                                </td>
                            </tr>
                            <tr>
                                <td>Clinic Charge (Static Facility Fee)</td>
                                <td>LKR 500.00</td>
                            </tr>
                            <tr class="total-row">
                                <td>Initial Pre-Consultation Deposit</td>
                                <td>LKR
                                    <fmt:formatNumber value="${ticket.consultationFee.add(500)}" type="currency"
                                        currencySymbol="" />
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div style="text-align: center; font-size: 0.8rem; color: #64748B;">
                        <p>Please present this ticket to the clinic receptionist upon entry.</p>
                        <p>Thank you for choosing Sunrise Dental Clinic!</p>
                    </div>

                    <div id="ticketStatusToast" class="status-toast"></div>

                    <div class="actions">
                        <button class="btn-action" style="background-color: #0284C7;" onclick="downloadPDFTicket()">📄
                            PDF Ticket </button>
                        <button class="btn-action" style="background-color: #059669;" onclick="sendEmailTicket()">✉️
                            Send Email</button>
                        <button class="btn-action" onclick="window.print()">🖨️ Print Ticket</button>
                        <a href="${pageContext.request.contextPath}/billing/receipt/${ticket.appointmentId}"
                            class="btn-action"
                            style="background-color: #6366F1; text-decoration: none; display: inline-block;">🧾
                            Procedure Receipt →</a>
                        <a href="${pageContext.request.contextPath}/dashboard"
                            style="margin-left: 0.5rem; color: var(--primary); font-size: 0.85rem; text-decoration: none;">←
                            Dashboard</a>
                    </div>
                </div>

                <script>
                    function downloadPDFTicket() {
                        const element = document.querySelector(".ticket-container");
                        const opt = {
                            margin: 0.3,
                            filename: 'Ticket_APT_${ticket.appointmentId}.pdf',
                            image: { type: 'jpeg', quality: 0.98 },
                            html2canvas: { scale: 2 },
                            jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
                        };
                        html2pdf().set(opt).from(element).save();
                    }

                    function sendEmailTicket() {
                        const toast = document.getElementById("ticketStatusToast");
                        let recipientEmail = "${ticket.patientEmail}";

                        if (!recipientEmail || recipientEmail.trim() === "") {
                            const input = prompt("Patient has no registered email. Enter email address to receive ticket (Leave blank to skip):", "");
                            if (input === null) {
                                return; // Cancelled
                            }
                            recipientEmail = input.trim();
                        }

                        if (!recipientEmail) {
                            if (toast) {
                                toast.className = "status-toast status-info";
                                toast.style.display = "block";
                                toast.innerText = "ℹ️ Email dispatch skipped.";
                            }
                            return;
                        }

                        if (toast) {
                            toast.className = "status-toast";
                            toast.style.display = "block";
                            toast.innerText = "Dispatching appointment ticket email to " + recipientEmail + "...";
                        }

                        fetch("${pageContext.request.contextPath}/api/appointments/${ticket.appointmentId}/send-email?email=" + encodeURIComponent(recipientEmail), {
                            method: "POST"
                        })
                            .then(res => res.json())
                            .then(data => {
                                if (data.success) {
                                    if (toast) {
                                        toast.className = "status-toast status-success";
                                        toast.innerText = "✓ " + data.message;
                                    }
                                } else {
                                    if (toast) {
                                        toast.className = "status-toast status-error";
                                        toast.innerText = "✕ Error: " + (data.message || "Failed to dispatch email.");
                                    }
                                }
                            })
                            .catch(err => {
                                if (toast) {
                                    toast.className = "status-toast status-error";
                                    toast.innerText = "✕ Failed to send ticket email.";
                                }
                            });
                    }
                </script>

            </body>

            </html>