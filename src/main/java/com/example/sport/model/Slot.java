package com.example.sport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Entity
@Table(name = "slot")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ground_id", nullable = false)
    private Ground ground;  // Reference to Ground entity (fetch ground name)

    private boolean availability; // True if available, False if booked

    private double price; // Price for Monday to Wednesday
    private double weekendPrice; // Price for Saturday & Sunday

    private LocalTime startTime; // Slot start time
    private LocalTime endTime;    // Slot end time
    @Column(nullable = true)
    private String breakTime;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;  // default: not deleted

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }


    public String getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(String breakTime) {
        this.breakTime = breakTime;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ground getGround() {
        return ground;
    }

    public void setGround(Ground ground) {
        this.ground = ground;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeekendPrice() {
        return weekendPrice;
    }

    public void setWeekendPrice(double weekendPrice) {
        this.weekendPrice = weekendPrice;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}