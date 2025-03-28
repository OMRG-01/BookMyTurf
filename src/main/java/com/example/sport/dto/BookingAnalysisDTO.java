package com.example.sport.dto;

public class BookingAnalysisDTO {
    private String gameName;
    private int totalBookings;
    private double totalRevenue;
    private double avgPrice;
    private String popularGround;

    public BookingAnalysisDTO(String gameName, int totalBookings, double totalRevenue, double avgPrice, String popularGround) {
        this.gameName = gameName;
        this.totalBookings = totalBookings;
        this.totalRevenue = totalRevenue;
        this.avgPrice = avgPrice;
        this.popularGround = popularGround;
    }

    public String getGameName() { return gameName; }
    public int getTotalBookings() { return totalBookings; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getAvgPrice() { return avgPrice; }
    public String getPopularGround() { return popularGround; }
}
