<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${report.reportTitle}"/> - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
</head>
<body class="report-page-body">

<div class="report-card" id="reportPrintableContent">
    <!-- Clinic Letterhead Header -->
    <div class="report-letterhead">
        <div class="clinic-brand">
            <h1><c:out value="${clinicName}"/></h1>
            <p><c:out value="${clinicAddress}"/> | Tel: <c:out value="${clinicPhone}"/></p>
            <p>Official Dental Health & Administrative Executive Report</p>
        </div>
        <div class="report-meta-box">
            <span class="report-badge">📊 OFFICIAL ADMINISTRATIVE REPORT</span>
            <p><strong>Period:</strong> <c:out value="${report.startDate}"/> to <c:out value="${report.endDate}"/></p>
            <p><strong>Generated At:</strong> <c:out value="${report.generatedAtFormatted}"/></p>
            <p><strong>Category:</strong> <c:out value="${report.reportCategory}"/></p>
        </div>
    </div>

    <!-- Title & Subtitle -->
    <div class="report-title-section">
        <h2><c:out value="${report.reportTitle}"/></h2>
        <p>Comprehensive analytical metrics, operational data audit, and aggregated summary figures for clinic decision makers.</p>
    </div>

    <!-- KPI Summary Grid -->
    <c:if test="${not empty report.kpiCards}">
        <div class="kpi-grid">
            <c:forEach var="kpi" items="${report.kpiCards}">
                <div class="kpi-card">
                    <div class="kpi-label"><c:out value="${kpi.title}"/></div>
                    <div class="kpi-value"><c:out value="${kpi.value}"/></div>
                    <div class="kpi-subtext"><c:out value="${kpi.subtext}"/></div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <!-- Structured Data Table -->
    <div class="report-table-wrapper">
        <table class="report-table">
            <thead>
                <tr>
                    <c:forEach var="th" items="${report.tableHeaders}">
                        <th><c:out value="${th}"/></th>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty report.tableRows}">
                        <c:forEach var="row" items="${report.tableRows}">
                            <tr class="${row.highlight ? 'highlight-row' : ''}">
                                <c:forEach var="cell" items="${row.columns}" varStatus="status">
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty row.badgeStatus && status.index == 4}">
                                                <span class="status-pill ${row.badgeStatus}"><c:out value="${cell}"/></span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${cell}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="${fn:length(report.tableHeaders)}" style="text-align: center; color: #94A3B8; padding: 2rem;">
                                No records found for the selected date range (<c:out value="${report.startDate}"/> to <c:out value="${report.endDate}"/>).
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
            <c:if test="${not empty report.footerTotals}">
                <tfoot>
                    <tr>
                        <c:forEach var="foot" items="${report.footerTotals}">
                            <td><c:out value="${foot}"/></td>
                        </c:forEach>
                    </tr>
                </tfoot>
            </c:if>
        </table>
    </div>

    <!-- Letterhead Footer Signoff -->
    <div class="report-footer-notes">
        <div>
            <strong>Report Status:</strong> VERIFIED &amp; APPROVED FOR ADMINISTRATIVE REVIEW<br>
            <em>Generated automatically by Sunrise Dental Clinic Health Information System.</em>
        </div>
        <div style="text-align: right;">
            <strong>Authorized Officer Signature:</strong> ________________________<br>
            <em>Clinic Medical Director &amp; Administrator</em>
        </div>
    </div>
</div>

<!-- Screen Action Bar -->
<div class="print-actions">
    <a href="${pageContext.request.contextPath}/dashboard" class="btn-logout" style="text-decoration: none; padding: 0.6rem 1.2rem; border-radius: 6px; font-weight: 600;">← Back to Executive Dashboard</a>
    <div style="display: flex; gap: 0.75rem;">
        <button type="button" class="btn-secondary" onclick="exportPDF()" style="padding: 0.6rem 1.2rem; cursor: pointer; border-radius: 6px; font-weight: 600;">📄 Export PDF Report</button>
        <button type="button" class="btn-primary" onclick="window.print()" style="padding: 0.6rem 1.2rem; cursor: pointer; border-radius: 6px; font-weight: 600;">🖨️ Print Report</button>
    </div>
</div>

<script>
    function exportPDF() {
        const element = document.getElementById("reportPrintableContent");
        const opt = {
            margin: [0.3, 0.3, 0.3, 0.3],
            filename: '${report.reportType}_Report_${report.startDate}_to_${report.endDate}.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { 
                scale: 2, 
                scrollY: 0, 
                scrollX: 0, 
                useCORS: true,
                windowWidth: document.documentElement.offsetWidth
            },
            jsPDF: { unit: 'in', format: 'letter', orientation: 'landscape' },
            pagebreak: { mode: ['avoid-all', 'css', 'legacy'] }
        };
        html2pdf().set(opt).from(element).save();
    }
</script>

</body>
</html>
