package com.example.sport.service;

import com.example.sport.model.Booking;
import com.example.sport.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
    
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    }
    public boolean isSlotPaid(Long slotId) {
        // Check if any booking exists for this slot with payment status = 1
        return bookingRepository.existsBySlotIdAndPaymentStatus(slotId, 1);
    }
}
