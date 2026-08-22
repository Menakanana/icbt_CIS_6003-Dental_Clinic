package com.dentalclinic.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Administrative Printable Reports.
 * Holds aggregated metrics, KPI cards, table structures, and date ranges.
 * 
 * Layer: DTO Layer
 */
public class AdminReportDTO {

    private String reportType;
    private String reportTitle;
    private String reportCategory;
    private LocalDate startDate;
    private LocalDate endDate;
    private String generatedAtFormatted;
    private List<KpiCard> kpiCards = new ArrayList<>();
    private List<String> tableHeaders = new ArrayList<>();
    private List<ReportRow> tableRows = new ArrayList<>();
    private List<String> footerTotals = new ArrayList<>();
    private String notes;

    public AdminReportDTO() {
        this.generatedAtFormatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public AdminReportDTO(String reportType, String reportTitle, String reportCategory, LocalDate startDate, LocalDate endDate) {
        this();
        this.reportType = reportType;
        this.reportTitle = reportTitle;
        this.reportCategory = reportCategory;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Inner DTO for KPI Cards
    public static class KpiCard {
        private String title;
        private String value;
        private String subtext;
        private String badgeColor; // e.g., "blue", "green", "purple", "amber", "red"

        public KpiCard() {}

        public KpiCard(String title, String value, String subtext, String badgeColor) {
            this.title = title;
            this.value = value;
            this.subtext = subtext;
            this.badgeColor = badgeColor;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getSubtext() { return subtext; }
        public void setSubtext(String subtext) { this.subtext = subtext; }

        public String getBadgeColor() { return badgeColor; }
        public void setBadgeColor(String badgeColor) { this.badgeColor = badgeColor; }
    }

    // Inner DTO for Data Rows
    public static class ReportRow {
        private List<String> columns = new ArrayList<>();
        private boolean highlight;
        private String badgeStatus; // e.g., "COMPLETED", "CANCELLED", "PAID"

        public ReportRow() {}

        public ReportRow(List<String> columns) {
            this.columns = columns;
        }

        public ReportRow(List<String> columns, boolean highlight) {
            this.columns = columns;
            this.highlight = highlight;
        }

        public List<String> getColumns() { return columns; }
        public void setColumns(List<String> columns) { this.columns = columns; }

        public boolean isHighlight() { return highlight; }
        public void setHighlight(boolean highlight) { this.highlight = highlight; }

        public String getBadgeStatus() { return badgeStatus; }
        public void setBadgeStatus(String badgeStatus) { this.badgeStatus = badgeStatus; }
    }

    // Getters & Setters
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }

    public String getReportCategory() { return reportCategory; }
    public void setReportCategory(String reportCategory) { this.reportCategory = reportCategory; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getGeneratedAtFormatted() { return generatedAtFormatted; }
    public void setGeneratedAtFormatted(String generatedAtFormatted) { this.generatedAtFormatted = generatedAtFormatted; }

    public List<KpiCard> getKpiCards() { return kpiCards; }
    public void setKpiCards(List<KpiCard> kpiCards) { this.kpiCards = kpiCards; }

    public List<String> getTableHeaders() { return tableHeaders; }
    public void setTableHeaders(List<String> tableHeaders) { this.tableHeaders = tableHeaders; }

    public List<ReportRow> getTableRows() { return tableRows; }
    public void setTableRows(List<ReportRow> tableRows) { this.tableRows = tableRows; }

    public List<String> getFooterTotals() { return footerTotals; }
    public void setFooterTotals(List<String> footerTotals) { this.footerTotals = footerTotals; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
