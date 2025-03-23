package com.example.sport.dto;

public class DashboardStats {
    private Long totalBookings;
    private Double occupancyRate;
    private Long activeUsers;
    private Double averageRating;

    // Default Constructor (No-argument constructor)
    public DashboardStats() {
    }

    // Parameterized Constructor
    public DashboardStats(Long totalBookings, Double occupancyRate, Long activeUsers, Double averageRating) {
        this.totalBookings = totalBookings;
        this.occupancyRate = occupancyRate;
        this.activeUsers = activeUsers;
        this.averageRating = averageRating;
    }

    // Getters
    public Long getTotalBookings() {
        return totalBookings;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    // Setters
    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}