<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
            </head>

            <body class="ticket-page-body">

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
                        <div style="font-size: 0.85rem; font-weight: 600; text-transform: uppercase; color: #047857;">
                            Queue Token Number
                        </div>
                        <div class="token-num">#<c:out value="${ticket.tokenNumber}" /></div>
                        
                        <div style="font-size: 0.9rem; font-weight: 600; color: #047857; margin-top: 0.25rem;">
                            🏥 Doctor Session Time: <c:out value="${ticket.sessionTimeWindow != null ? ticket.sessionTimeWindow : '09:00 AM - 01:00 PM'}" />
                        </div>

                        <div style="font-size: 0.95rem; font-weight: 800; color: #92400E; background-color: #FEF3C7; border: 1.5px solid #FCD34D; padding: 6px 14px; border-radius: 6px; display: inline-block; margin-top: 0.5rem;">
                            ⏰ Estimated Arrival: Please report by <c:out value="${ticket.estimatedArrivalTime}" /> (15 mins prior)
                        </div>

                        <div style="margin-top: 0.65rem; font-size: 0.85rem; font-weight: 700;">
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
                            <div class="info-value">
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
                </div>

                <div id="ticketStatusToast" class="status-toast" style="max-width: 520px; width: 100%; margin: 1rem auto 0 auto;"></div>

                <div class="actions" style="max-width: 520px; width: 100%; margin-top: 1rem;">
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

                <script>
                    function downloadPDFTicket() {
                        const element = document.querySelector(".ticket-container");
                        const opt = {
                            margin: [0.3, 0.3, 0.3, 0.3],
                            filename: 'Ticket_APT_${ticket.appointmentId}.pdf',
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

                    function sendEmailTicket() {
                        let recipientEmail = "${ticket.patientEmail}";

                        if (!recipientEmail || recipientEmail.trim() === "") {
                            showCustomPrompt(
                                "Email Appointment Ticket",
                                "Patient has no registered email. Enter email address to receive ticket:",
                                "",
                                function(inputEmail) {
                                    if (inputEmail === null || inputEmail.trim() === "") {
                                        const toast = document.getElementById("ticketStatusToast");
                                        if (toast) {
                                            toast.className = "status-toast status-info";
                                            toast.style.display = "block";
                                            toast.innerText = "ℹ️ Email dispatch skipped.";
                                        }
                                        return;
                                    }
                                    doSendTicketEmail(inputEmail.trim());
                                }
                            );
                        } else {
                            doSendTicketEmail(recipientEmail.trim());
                        }
                    }

                    function doSendTicketEmail(recipientEmail) {
                        const toast = document.getElementById("ticketStatusToast");
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
                                    showToast("✓ " + data.message, "success");
                                } else {
                                    if (toast) {
                                        toast.className = "status-toast status-error";
                                        toast.innerText = "✕ Error: " + (data.message || "Failed to dispatch email.");
                                    }
                                    showToast("✕ Error: " + (data.message || "Failed to dispatch email."), "error");
                                }
                            })
                            .catch(err => {
                                if (toast) {
                                    toast.className = "status-toast status-error";
                                    toast.innerText = "✕ Failed to send ticket email.";
                                }
                                showToast("✕ Failed to send ticket email", "error");
                            });
                    }
                </script>

                <jsp:include page="components/ui-dialogs.jsp"/>

            </body>

            </html>