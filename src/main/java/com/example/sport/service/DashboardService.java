package com.example.sport.service;

import org.springframework.stereotype.Service;

import com.example.sport.dto.DashboardStats;
import com.example.sport.repository.BookingRepository;
import com.example.sport.repository.UserRepository;

@Service
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public DashboardService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public DashboardStats getDashboardStats() {
    	DashboardStats stats = new DashboardStats();
        
        // Total Bookings
        stats.setTotalBookings(bookingRepository.countTotalBookings());
        
        // Occupancy Rate (Paid vs Total Bookings)
        Long total = bookingRepository.countTotalBookings();
        Long paid = bookingRepository.countPaidBookings();
        stats.setOccupancyRate(total > 0 ? (paid.doubleValue() / total.doubleValue()) * 100 : 0.0);
        
        // Active Users (Role = USER)
        stats.setActiveUsers(userRepository.countByRoleId(2L));
        
        // Average Rating
        Double avgRating = bookingRepository.findAverageRating();
        stats.setAverageRating(avgRating != null ? avgRating : 0.0);
        
        return stats;
    }
}