package com.example.sport.model;

public class ReviewRequest {
    private Long bookingId;
    private Integer rating;
    private String review;

    // Default Constructor (No-argument constructor)
    public ReviewRequest() {
    }

    // Parameterized Constructor
    public ReviewRequest(Long bookingId, Integer rating, String review) {
        this.bookingId = bookingId;
        this.rating = rating;
        this.review = review;
    }

    // Getters
    public Long getBookingId() {
        return bookingId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    // Setters
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setReview(String review) {
        this.review = review;
    }
}