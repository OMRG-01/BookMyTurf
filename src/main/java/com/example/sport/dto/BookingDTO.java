package com.example.sport.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {
    private Long id;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String groundName;
    private int paymentStatus;
    private Integer rating;
    private String review;
    private String address;

    // ✅ Add this constructor
    public BookingDTO(Long id, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, 
                      String groundName, int paymentStatus, Integer rating, String review, String address) {
        this.id = id;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.groundName = groundName;
        this.paymentStatus = paymentStatus;
        this.rating = rating;
        this.review = review;
        this.address =address;
    }

    // ✅ Getters & Setters (Ensure these exist)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getAddress() { return address; }
    public void setAddresse(String address) { this.groundName = address; }
    
    public String getGroundName() { return groundName; }
    public void setGroundName(String groundName) { this.groundName = groundName; }

    public int getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(int paymentStatus) { this.paymentStatus = paymentStatus; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
}
