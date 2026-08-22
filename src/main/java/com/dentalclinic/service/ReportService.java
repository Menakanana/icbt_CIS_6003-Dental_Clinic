package com.dentalclinic.service;

import com.dentalclinic.dto.AdminReportDTO;
import com.dentalclinic.entity.Appointment;
import com.dentalclinic.entity.Dentist;
import com.dentalclinic.entity.Patient;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.repository.AppointmentRepository;
import com.dentalclinic.repository.DentistRepository;
import com.dentalclinic.repository.PatientRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service generating 7 executive printable administrative reports with KPI metrics,
 * data aggregations, and tabular summaries.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public ReportService(AppointmentRepository appointmentRepository,
                         DentistRepository dentistRepository,
                         TreatmentTypeRepository treatmentTypeRepository,
                         PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Dispatcher method to generate report DTO based on report type enum.
     */
    @Transactional(readOnly = true)
    public AdminReportDTO generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate.isAfter(endDate)) {
            LocalDate temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        String type = (reportType != null) ? reportType.toUpperCase() : "DOCTOR_DEMAND";

        switch (type) {
            case "DOCTOR_DEMAND":
                return generateDoctorDemandReport(startDate, endDate);
            case "PROCEDURE_USAGE":
                return generateProcedureUsageReport(startDate, endDate);
            case "FINANCIAL_SUMMARY":
                return generateFinancialSummaryReport(startDate, endDate);
            case "PATIENT_DEMOGRAPHICS":
                return generatePatientDemographicsReport(startDate, endDate);
            case "CANCELLATION_AUDIT":
                return generateCancellationAuditReport(startDate, endDate);
            case "SESSION_UTILIZATION":
                return generateSessionUtilizationReport(startDate, endDate);
            case "OUTSTANDING_DUES":
                return generateOutstandingDuesReport(startDate, endDate);
            default:
                return generateDoctorDemandReport(startDate, endDate);
        }
    }

    // 1. DOCTOR DEMAND & REVENUE REPORT
    private AdminReportDTO generateDoctorDemandReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("DOCTOR_DEMAND",
                "Doctor Booking Demand & Revenue Analysis",
                "Executive Administrative Suite", startDate, endDate);

        List<Appointment> allAppts = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null &&
                        !a.getAppointmentDate().isBefore(startDate) &&
                        !a.getAppointmentDate().isAfter(endDate))
                .collect(Collectors.toList());

        List<Dentist> dentists = dentistRepository.findAll();

        int totalBookings = allAppts.size();
        long completedTotal = allAppts.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count();

        Map<Integer, List<Appointment>> apptsByDentist = allAppts.stream()
                .filter(a -> a.getDentist() != null)
                .collect(Collectors.groupingBy(a -> a.getDentist().getDentistId()));

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();
        int rank = 1;

        // Sort dentists by total bookings descending
        List<Dentist> sortedDentists = new ArrayList<>(dentists);
        sortedDentists.sort((d1, d2) -> {
            int c1 = apptsByDentist.getOrDefault(d1.getDentistId(), Collections.emptyList()).size();
            int c2 = apptsByDentist.getOrDefault(d2.getDentistId(), Collections.emptyList()).size();
            return Integer.compare(c2, c1);
        });

        BigDecimal grossRevSum = BigDecimal.ZERO;
        int sumBookings = 0;
        int sumCompleted = 0;
        int sumCancelled = 0;

        for (Dentist d : sortedDentists) {
            List<Appointment> list = apptsByDentist.getOrDefault(d.getDentistId(), Collections.emptyList());
            int count = list.size();
            long completed = list.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count();
            long cancelled = list.stream().filter(a -> "CANCELLED".equalsIgnoreCase(a.getStatus())).count();

            BigDecimal fee = (d.getConsultationFee() != null) ? d.getConsultationFee() : BigDecimal.ZERO;
            BigDecimal revenue = fee.multiply(BigDecimal.valueOf(completed));
            grossRevSum = grossRevSum.add(revenue);

            sumBookings += count;
            sumCompleted += completed;
            sumCancelled += cancelled;

            double compRate = (count > 0) ? ((double) completed / count) * 100.0 : 0.0;

            List<String> cols = Arrays.asList(
                    String.valueOf(rank++),
                    "Dr. " + d.getDentistName(),
                    (d.getSpecialization() != null) ? d.getSpecialization() : "General Dentist",
                    "LKR " + String.format("%.2f", fee),
                    String.valueOf(count),
                    String.valueOf(completed),
                    String.valueOf(cancelled),
                    "LKR " + String.format("%.2f", revenue),
                    String.format("%.1f%%", compRate)
            );
            rows.add(new AdminReportDTO.ReportRow(cols, rank == 2));
        }

        report.setTableHeaders(Arrays.asList("Rank", "Doctor Name", "Specialization", "Consultation Fee", "Total Bookings", "Completed", "Cancelled", "Total Revenue", "Completion Rate"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("TOTALS", "-", "-", "-", String.valueOf(sumBookings), String.valueOf(sumCompleted), String.valueOf(sumCancelled), "LKR " + String.format("%.2f", grossRevSum), "-"));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Active Dentists", String.valueOf(dentists.size()), "Registered Practitioners", "blue"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Bookings", String.valueOf(totalBookings), "In Date Window", "purple"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Completed Consults", String.valueOf(completedTotal), "Fulfilled Appointments", "green"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Consultation Rev.", "LKR " + String.format("%.2f", grossRevSum), "Direct Doctor Fees", "amber"));

        return report;
    }

    // 2. MOST FREQUENTLY PERFORMED TREATMENT PROCEDURES REPORT
    private AdminReportDTO generateProcedureUsageReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("PROCEDURE_USAGE",
                "Most Frequently Performed Treatment Procedures Report",
                "Executive Administrative Suite", startDate, endDate);

        List<Appointment> appts = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null &&
                        !a.getAppointmentDate().isBefore(startDate) &&
                        !a.getAppointmentDate().isAfter(endDate) &&
                        !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        List<TreatmentType> treatmentTypes = treatmentTypeRepository.findAll();

        Map<Integer, Long> countMap = new HashMap<>();
        Map<Integer, BigDecimal> revMap = new HashMap<>();

        for (Appointment a : appts) {
            if (a.getTreatmentType() != null) {
                Integer tid = a.getTreatmentType().getTreatmentTypeId();
                countMap.put(tid, countMap.getOrDefault(tid, 0L) + 1);
                BigDecimal base = a.getTreatmentType().getBaseCost() != null ? a.getTreatmentType().getBaseCost() : BigDecimal.ZERO;
                revMap.put(tid, revMap.getOrDefault(tid, BigDecimal.ZERO).add(base));
            }
        }

        long totalProcCount = appts.size();
        BigDecimal totalProcRev = revMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Sort treatment types by usage frequency descending
        treatmentTypes.sort((t1, t2) -> Long.compare(
                countMap.getOrDefault(t2.getTreatmentTypeId(), 0L),
                countMap.getOrDefault(t1.getTreatmentTypeId(), 0L)));

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();
        int rank = 1;
        String topProcName = treatmentTypes.isEmpty() ? "N/A" : treatmentTypes.get(0).getTreatmentName();

        for (TreatmentType tt : treatmentTypes) {
            long usage = countMap.getOrDefault(tt.getTreatmentTypeId(), 0L);
            BigDecimal rev = revMap.getOrDefault(tt.getTreatmentTypeId(), BigDecimal.ZERO);
            BigDecimal cost = tt.getBaseCost() != null ? tt.getBaseCost() : BigDecimal.ZERO;
            double share = (totalProcCount > 0) ? ((double) usage / totalProcCount) * 100.0 : 0.0;

            String category = (tt.getDescription() != null && !tt.getDescription().trim().isEmpty()) ? tt.getDescription() : "General Dentistry";

            List<String> cols = Arrays.asList(
                    String.valueOf(rank++),
                    tt.getTreatmentName(),
                    category,
                    "LKR " + String.format("%.2f", cost),
                    String.valueOf(usage),
                    "LKR " + String.format("%.2f", rev),
                    String.format("%.1f%%", share)
            );
            rows.add(new AdminReportDTO.ReportRow(cols, rank == 2));
        }

        BigDecimal avgCost = (totalProcCount > 0) ? totalProcRev.divide(BigDecimal.valueOf(totalProcCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        report.setTableHeaders(Arrays.asList("Rank", "Procedure Name", "Description / Category", "Base Cost", "Times Performed", "Total Revenue", "Usage Share"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("TOTALS", "-", "-", "-", String.valueOf(totalProcCount), "LKR " + String.format("%.2f", totalProcRev), "100.0%"));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Procedures", String.valueOf(totalProcCount), "Executed Treatments", "blue"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Top Treatment", topProcName, "Highest Volume", "green"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Procedure Revenue", "LKR " + String.format("%.2f", totalProcRev), "Gross Procedure Income", "purple"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Avg Cost / Procedure", "LKR " + String.format("%.2f", avgCost), "Mean Yield per Unit", "amber"));

        return report;
    }

    // 3. DAILY & PERIODICAL FINANCIAL SETTLEMENT STATEMENT
    private AdminReportDTO generateFinancialSummaryReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("FINANCIAL_SUMMARY",
                "Daily & Periodical Financial Settlement Statement",
                "Executive Administrative Suite", startDate, endDate);

        List<Appointment> appts = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null &&
                        !a.getAppointmentDate().isBefore(startDate) &&
                        !a.getAppointmentDate().isAfter(endDate) &&
                        !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalDiscounts = BigDecimal.ZERO;

        Map<String, List<Appointment>> byMethod = appts.stream()
                .collect(Collectors.groupingBy(a -> (a.getPaymentMethod() != null && !a.getPaymentMethod().trim().isEmpty()) ? a.getPaymentMethod().trim() : "Cash"));

        Set<String> methods = new LinkedHashSet<>(Arrays.asList("Cash", "Credit/Debit Card", "Bank Transfer", "Online Gateway"));
        methods.addAll(byMethod.keySet());

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();
        BigDecimal cashRev = BigDecimal.ZERO;
        BigDecimal cardRev = BigDecimal.ZERO;
        BigDecimal onlineRev = BigDecimal.ZERO;

        for (String m : methods) {
            List<Appointment> list = byMethod.getOrDefault(m, Collections.emptyList());
            int count = list.size();
            BigDecimal netMethodPaid = BigDecimal.ZERO;
            BigDecimal grossMethod = BigDecimal.ZERO;

            for (Appointment a : list) {
                BigDecimal paid = a.getPaidAmount() != null ? a.getPaidAmount() : BigDecimal.ZERO;
                netMethodPaid = netMethodPaid.add(paid);

                BigDecimal fee = a.getDentist() != null && a.getDentist().getConsultationFee() != null ? a.getDentist().getConsultationFee() : BigDecimal.ZERO;
                BigDecimal tCost = a.getTreatmentType() != null && a.getTreatmentType().getBaseCost() != null ? a.getTreatmentType().getBaseCost() : BigDecimal.ZERO;
                BigDecimal clinicChg = BillingService.DEFAULT_CLINIC_CHARGE;
                grossMethod = grossMethod.add(fee.add(tCost).add(clinicChg));
            }

            totalGross = totalGross.add(grossMethod);
            totalPaid = totalPaid.add(netMethodPaid);

            if ("Cash".equalsIgnoreCase(m)) cashRev = cashRev.add(netMethodPaid);
            else if (m.toLowerCase().contains("card")) cardRev = cardRev.add(netMethodPaid);
            else onlineRev = onlineRev.add(netMethodPaid);

            double share = (totalPaid.compareTo(BigDecimal.ZERO) > 0)
                    ? netMethodPaid.multiply(BigDecimal.valueOf(100)).divide(totalPaid, 1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;

            List<String> cols = Arrays.asList(
                    m,
                    "Stage 1 Deposit & Stage 2 Settlement",
                    String.valueOf(count),
                    "LKR " + String.format("%.2f", grossMethod),
                    "LKR 0.00",
                    "LKR " + String.format("%.2f", netMethodPaid),
                    String.format("%.1f%%", share)
            );
            rows.add(new AdminReportDTO.ReportRow(cols));
        }

        report.setTableHeaders(Arrays.asList("Payment Method", "Transaction Type", "Txn Count", "Gross Amount", "Discounts", "Net Revenue", "Revenue Share"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("TOTAL FINANCIAL SETTLEMENT", "-", String.valueOf(appts.size()), "LKR " + String.format("%.2f", totalGross), "LKR " + String.format("%.2f", totalDiscounts), "LKR " + String.format("%.2f", totalPaid), "100.0%"));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Gross Billed Total", "LKR " + String.format("%.2f", totalGross), "Before Settlements", "blue"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Net Revenue Collected", "LKR " + String.format("%.2f", totalPaid), "Actual Received Funds", "green"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Cash Collections", "LKR " + String.format("%.2f", cashRev), "Over-the-Counter Cash", "purple"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Card & Bank Settlements", "LKR " + String.format("%.2f", cardRev.add(onlineRev)), "POS & Bank Transfers", "amber"));

        return report;
    }

    // 4. PATIENT DEMOGRAPHICS & REGISTRATION GROWTH REPORT
    private AdminReportDTO generatePatientDemographicsReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("PATIENT_DEMOGRAPHICS",
                "Patient Demographics & Registration Growth Report",
                "Executive Administrative Suite", startDate, endDate);

        List<Patient> allPatients = patientRepository.findAll();
        List<Appointment> allAppts = appointmentRepository.findAll();

        Map<Integer, Long> visitMap = allAppts.stream()
                .filter(a -> a.getPatient() != null && !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.groupingBy(a -> a.getPatient().getPatientId(), Collectors.counting()));

        int totalPatients = allPatients.size();
        int newRegs = 0;
        int children = 0;
        int adults = 0;
        int seniors = 0;
        int maleCount = 0;
        int femaleCount = 0;

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();
        Patient topPatient = null;
        long maxVisits = 0;

        for (Patient p : allPatients) {
            LocalDate regDate = (p.getRegisteredDate() != null) ? p.getRegisteredDate().toLocalDate() : LocalDate.now();
            if (!regDate.isBefore(startDate) && !regDate.isAfter(endDate)) {
                newRegs++;
            }

            int age = (p.getDateOfBirth() != null) ? Period.between(p.getDateOfBirth(), LocalDate.now()).getYears() : 30;
            if (age < 18) children++;
            else if (age <= 59) adults++;
            else seniors++;

            String gender = (p.getGender() != null) ? p.getGender() : "Other";
            if ("Male".equalsIgnoreCase(gender) || "M".equalsIgnoreCase(gender)) maleCount++;
            else if ("Female".equalsIgnoreCase(gender) || "F".equalsIgnoreCase(gender)) femaleCount++;

            long visits = visitMap.getOrDefault(p.getPatientId(), 0L);
            if (visits > maxVisits) {
                maxVisits = visits;
                topPatient = p;
            }

            List<String> cols = Arrays.asList(
                    "PAT-" + (p.getPatientId() + 1000),
                    p.getPatientName(),
                    (p.getNic() != null) ? p.getNic() : "N/A",
                    String.valueOf(age),
                    gender,
                    p.getContactNumber(),
                    String.valueOf(visits),
                    regDate.toString()
            );
            rows.add(new AdminReportDTO.ReportRow(cols));
        }

        String topPatientStr = (topPatient != null) ? topPatient.getPatientName() + " (" + maxVisits + " visits)" : "N/A";

        report.setTableHeaders(Arrays.asList("Patient ID", "Patient Name", "NIC / Passport", "Age", "Gender", "Contact Number", "Total Visits", "Registration Date"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("TOTAL PATIENTS", String.valueOf(totalPatients), "-", "Adults: " + adults, "Seniors: " + seniors, "-", "Visits: " + allAppts.size(), "-"));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Registered Patients", String.valueOf(totalPatients), "Master Database Count", "blue"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("New Registrations", String.valueOf(newRegs), "In Selected Period", "green"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Adult & Senior Share", (adults + seniors) + " / " + totalPatients, "Age 18+ Patients", "purple"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Top Recurring Patient", topPatientStr, "Highest Clinic Visits", "amber"));

        return report;
    }

    // 5. APPOINTMENT CANCELLATION, RESCHEDULE & DEPOSIT TRANSFER AUDIT
    private AdminReportDTO generateCancellationAuditReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("CANCELLATION_AUDIT",
                "Appointment Cancellation, Reschedule & Deposit Audit",
                "Executive Administrative Suite", startDate, endDate);

        List<Appointment> appts = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null &&
                        !a.getAppointmentDate().isBefore(startDate) &&
                        !a.getAppointmentDate().isAfter(endDate))
                .collect(Collectors.toList());

        long booked = appts.stream().filter(a -> "BOOKED".equalsIgnoreCase(a.getStatus())).count();
        long completed = appts.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count();
        long rescheduled = appts.stream().filter(a -> "RESCHEDULED".equalsIgnoreCase(a.getStatus())).count();
        long cancelled = appts.stream().filter(a -> "CANCELLED".equalsIgnoreCase(a.getStatus())).count();
        int total = appts.size();

        double cancelRate = (total > 0) ? ((double) cancelled / total) * 100.0 : 0.0;

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();
        for (Appointment a : appts) {
            String statusStr = (a.getStatus() != null) ? a.getStatus().toUpperCase() : "BOOKED";
            String notes = (a.getNotes() != null && !a.getNotes().trim().isEmpty()) ? a.getNotes() : "Standard Booking";

            String depositCarryOver = "CANCELLED".equalsIgnoreCase(statusStr) ? "Non-refundable Deposit" :
                    ("RESCHEDULED".equalsIgnoreCase(statusStr) ? "Deposit Retained & Transferred" : "Applied to Visit");

            List<String> cols = Arrays.asList(
                    "APP-" + (a.getAppointmentId() + 2000),
                    a.getAppointmentDate().toString() + " " + ((a.getStartTime() != null) ? a.getStartTime().toString() : ""),
                    a.getPatient() != null ? a.getPatient().getPatientName() : "Unknown",
                    a.getDentist() != null ? "Dr. " + a.getDentist().getDentistName() : "Unassigned",
                    statusStr,
                    notes,
                    depositCarryOver
            );
            AdminReportDTO.ReportRow row = new AdminReportDTO.ReportRow(cols);
            row.setBadgeStatus(statusStr);
            rows.add(row);
        }

        report.setTableHeaders(Arrays.asList("Appointment ID", "Date & Time", "Patient Name", "Doctor Name", "Status", "Notes / Reschedule Reason", "Deposit Carry-Over Status"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("SUMMARY", "-", "-", "-", "Booked: " + booked, "Completed: " + completed, "Cancelled: " + cancelled + " | Rescheduled: " + rescheduled));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Bookings", String.valueOf(total), "Period Volume", "blue"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Completed Visits", String.valueOf(completed), "Successfully Consulted", "green"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Cancelled Bookings", String.valueOf(cancelled), "Missed / Withdrawn", "red"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Cancellation Rate", String.format("%.1f%%", cancelRate), "Percentage of Total", "amber"));

        return report;
    }

    // 6. PEAK CHANNELING HOURS & DOCTOR SESSION UTILIZATION REPORT
    private AdminReportDTO generateSessionUtilizationReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("SESSION_UTILIZATION",
                "Peak Channeling Hours & Doctor Session Utilization Report",
                "Executive Administrative Suite", startDate, endDate);

        List<Appointment> appts = appointmentRepository.findAll().stream()
                .filter(a -> a.getAppointmentDate() != null &&
                        !a.getAppointmentDate().isBefore(startDate) &&
                        !a.getAppointmentDate().isAfter(endDate))
                .collect(Collectors.toList());

        long morningCount = 0;
        long afternoonCount = 0;
        long eveningCount = 0;

        long morningComp = 0;
        long afternoonComp = 0;
        long eveningComp = 0;

        for (Appointment a : appts) {
            LocalTime st = a.getStartTime();
            boolean isComp = "COMPLETED".equalsIgnoreCase(a.getStatus());
            if (st != null) {
                if (st.isBefore(LocalTime.of(12, 0))) {
                    morningCount++;
                    if (isComp) morningComp++;
                } else if (st.isBefore(LocalTime.of(17, 0))) {
                    afternoonCount++;
                    if (isComp) afternoonComp++;
                } else {
                    eveningCount++;
                    if (isComp) eveningComp++;
                }
            } else {
                afternoonCount++;
                if (isComp) afternoonComp++;
            }
        }

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();

        double mUtil = (morningCount > 0) ? ((double) morningComp / morningCount) * 100.0 : 0.0;
        double aUtil = (afternoonCount > 0) ? ((double) afternoonComp / afternoonCount) * 100.0 : 0.0;
        double eUtil = (eveningCount > 0) ? ((double) eveningComp / eveningCount) * 100.0 : 0.0;

        rows.add(new AdminReportDTO.ReportRow(Arrays.asList("Morning Session", "08:00 AM - 12:00 PM", String.valueOf(morningCount), String.valueOf(morningCount), String.valueOf(morningComp), String.format("%.1f%%", mUtil), "High Demand Morning")));
        rows.add(new AdminReportDTO.ReportRow(Arrays.asList("Afternoon Session", "12:00 PM - 05:00 PM", String.valueOf(afternoonCount), String.valueOf(afternoonCount), String.valueOf(afternoonComp), String.format("%.1f%%", aUtil), "Peak Channeling Window")));
        rows.add(new AdminReportDTO.ReportRow(Arrays.asList("Evening Session", "05:00 PM - 09:00 PM", String.valueOf(eveningCount), String.valueOf(eveningCount), String.valueOf(eveningComp), String.format("%.1f%%", eUtil), "After-Hours Consults")));

        String peakSession = "Afternoon (12 PM - 5 PM)";
        if (morningCount > afternoonCount && morningCount > eveningCount) peakSession = "Morning (8 AM - 12 PM)";
        else if (eveningCount > afternoonCount && eveningCount > morningCount) peakSession = "Evening (5 PM - 9 PM)";

        report.setTableHeaders(Arrays.asList("Session Window", "Time Band", "Scheduled Sessions", "Tokens Issued", "Completed Consults", "Utilization Rate", "Demand Category"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("TOTALS", "-", String.valueOf(appts.size()), String.valueOf(appts.size()), String.valueOf(morningComp + afternoonComp + eveningComp), "-", "-"));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Sessions Tracked", String.valueOf(appts.size()), "Total Appointment Slots", "blue"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Peak Channeling Window", peakSession, "Highest Patient Density", "amber"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Morning Session Volume", String.valueOf(morningCount), "8:00 AM - 12:00 PM", "green"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Afternoon / Evening Volume", String.valueOf(afternoonCount + eveningCount), "12:00 PM - 9:00 PM", "purple"));

        return report;
    }

    // 7. OUTSTANDING & UNPAID DUES STATEMENT
    private AdminReportDTO generateOutstandingDuesReport(LocalDate startDate, LocalDate endDate) {
        AdminReportDTO report = new AdminReportDTO("OUTSTANDING_DUES",
                "Outstanding & Unpaid Dues Executive Statement",
                "Executive Administrative Suite", startDate, endDate);

        List<Appointment> appts = appointmentRepository.findAll().stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        List<AdminReportDTO.ReportRow> rows = new ArrayList<>();
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        int countDues = 0;
        int overdue30Days = 0;

        for (Appointment a : appts) {
            BigDecimal fee = a.getDentist() != null && a.getDentist().getConsultationFee() != null ? a.getDentist().getConsultationFee() : BigDecimal.ZERO;
            BigDecimal tCost = a.getTreatmentType() != null && a.getTreatmentType().getBaseCost() != null ? a.getTreatmentType().getBaseCost() : BigDecimal.ZERO;
            BigDecimal gross = fee.add(tCost).add(BillingService.DEFAULT_CLINIC_CHARGE);

            BigDecimal paid = a.getPaidAmount() != null ? a.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal due = gross.subtract(paid);

            if (due.compareTo(BigDecimal.ZERO) > 0 || "UNPAID".equalsIgnoreCase(a.getPaymentStatus()) || "PARTIAL".equalsIgnoreCase(a.getPaymentStatus())) {
                countDues++;
                totalOutstanding = totalOutstanding.add(due);

                if (a.getAppointmentDate() != null && a.getAppointmentDate().isBefore(LocalDate.now().minusDays(30))) {
                    overdue30Days++;
                }

                List<String> cols = Arrays.asList(
                        "PAT-" + (a.getPatient() != null ? (a.getPatient().getPatientId() + 1000) : "0000"),
                        a.getPatient() != null ? a.getPatient().getPatientName() : "Unknown Patient",
                        a.getPatient() != null ? a.getPatient().getContactNumber() : "N/A",
                        "APP-" + (a.getAppointmentId() + 2000),
                        a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "N/A",
                        a.getDentist() != null ? "Dr. " + a.getDentist().getDentistName() : "N/A",
                        "LKR " + String.format("%.2f", gross),
                        "LKR " + String.format("%.2f", paid),
                        "LKR " + String.format("%.2f", due)
                );
                rows.add(new AdminReportDTO.ReportRow(cols, true));
            }
        }

        BigDecimal avgDue = (countDues > 0) ? totalOutstanding.divide(BigDecimal.valueOf(countDues), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        report.setTableHeaders(Arrays.asList("Patient ID", "Patient Name", "Contact Number", "Appointment ID", "Date", "Doctor", "Gross Fee", "Paid Amount", "Balance Due"));
        report.setTableRows(rows);
        report.setFooterTotals(Arrays.asList("TOTAL OUTSTANDING DUES", "-", "-", "-", "-", "-", "-", "-", "LKR " + String.format("%.2f", totalOutstanding)));

        report.getKpiCards().add(new AdminReportDTO.KpiCard("Pending Accounts", String.valueOf(countDues), "Patients with Dues", "red"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Total Balance Due", "LKR " + String.format("%.2f", totalOutstanding), "Uncollected Clinic Receivables", "amber"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Average Due / Patient", "LKR " + String.format("%.2f", avgDue), "Mean Outstanding Balance", "purple"));
        report.getKpiCards().add(new AdminReportDTO.KpiCard("Overdue > 30 Days", String.valueOf(overdue30Days), "High Priority Recoveries", "blue"));

        return report;
    }
}
