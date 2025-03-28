package com.example.sport.dto;

import java.time.LocalDate;

public class FinanceReportDTO {
    private LocalDate date;
    private Double totalRevenue;
    private Double paidAmount;
    private Double unpaidAmount;

    public FinanceReportDTO(LocalDate date, Double totalRevenue, Double paidAmount, Double unpaidAmount) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.paidAmount = paidAmount;
        this.unpaidAmount = unpaidAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getUnpaidAmount() {
        return unpaidAmount;
    }

    public void setUnpaidAmount(Double unpaidAmount) {
        this.unpaidAmount = unpaidAmount;
    }
}
