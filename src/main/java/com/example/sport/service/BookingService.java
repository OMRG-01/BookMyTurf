package com.example.sport.service;

import com.example.sport.dto.BookingDTO;
import com.example.sport.model.Booking;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.model.User;
import com.example.sport.repository.BookingRepository;
import com.example.sport.repository.UserRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

	@Autowired
    private UserRepository userRepository; 
	
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private GroundService groundService;
    
 
    @Autowired
    private UserService userService;

    @Autowired
    private SlotService slotService;

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
    
    public boolean isSlotBooked(Long slotId, LocalDate bookingDate) {
        return bookingRepository.existsBySlotIdAndBookingDate(slotId, bookingDate);
    }
    
    public List<Long> getBookedSlotsByDate(LocalDate bookingDate) {
        return bookingRepository.findBookedSlotIdsByDate(bookingDate);
    }
    
 // Fix for past bookings
    public List<BookingDTO> getPastBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findPastBookings(userId, LocalDate.now()); // 🔥 Fix: Pass LocalDate.now()

        return bookings.stream().map(booking -> {
            Ground ground = groundService.findById(booking.getGroundId());
            Slot slot = slotService.findById(booking.getSlotId());

            return new BookingDTO(
                booking.getId(),
                booking.getBookingDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                ground.getName(),
                booking.getPaymentStatus(),
                booking.getRating(),
                booking.getReview(),
                ground.getAddress()
            );
        }).collect(Collectors.toList());
    }

    // Fix for upcoming bookings
    public List<BookingDTO> getUpcomingBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findUpcomingBookings(userId, LocalDate.now()); // 🔥 Fix: Pass LocalDate.now()

        return bookings.stream().map(booking -> {
            Ground ground = groundService.findById(booking.getGroundId());
            Slot slot = slotService.findById(booking.getSlotId());

            return new BookingDTO(
                booking.getId(),
                booking.getBookingDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                ground.getName(),
                booking.getPaymentStatus(),
                booking.getRating(),
                booking.getReview(),
                ground.getAddress()
            );
        }).collect(Collectors.toList());
    }
    public boolean updateBookingReview(Long bookingId, Integer rating, String review) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setRating(rating);
            booking.setReview(review);
            bookingRepository.save(booking);
            return true;
        }
        return false;
    }
    
    public List<BookingDTO> getAllBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream().map(booking -> {
            Ground ground = groundService.findById(booking.getGroundId());
            Slot slot = slotService.findById(booking.getSlotId());

            return new BookingDTO(
                booking.getId(),
                booking.getBookingDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                ground.getName(),
                booking.getPaymentStatus(),
                booking.getRating(),
                booking.getReview(),
                ground.getAddress()
            );
        }).collect(Collectors.toList());
    }

    
    public Long getUserIdByName(String name) {
        User user = userRepository.findByName(name);
        return (user != null) ? user.getId() : null;
    }

}
