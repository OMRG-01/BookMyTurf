package com.example.sport.service;

import com.example.sport.dto.BookingDTO;
import com.example.sport.model.Booking;
import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.model.User;
import com.example.sport.repository.BookingRepository;
import com.example.sport.repository.GameRepository;
import com.example.sport.repository.GroundRepository;
import com.example.sport.repository.SlotRepository;
import com.example.sport.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private GroundRepository groundRepository;
    
    @Autowired
    private UserService userService;

    @Autowired
    private SlotService slotService;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private SlotRepository slotRepository;

    
    /**
     * Fetches bookings for a user and maps the results to a list of BookingDTO.
     * @param userId The ID of the user for whom bookings need to be fetched.
     * @return A list of BookingDTO containing booking details along with ground name.
     */
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        // Fetch data from the repository using the custom query
        List<Object[]> bookingsData = bookingRepository.findBookingWithGroundName(userId);

        // Create a list of BookingDTOs to return
        List<BookingDTO> bookings = new ArrayList<>();

        for (Object[] data : bookingsData) {
            Long bookingId = (Long) data[0];
            Long groundId = (Long) data[1]; // Ground ID from the query
            LocalDate bookingDate = (LocalDate) data[2];
            LocalTime startTime = (LocalTime) data[3];
            LocalTime endTime = (LocalTime) data[4];
            int paymentStatus = (int) data[5];

            // Fetch Ground name using the groundId
            String groundName = groundRepository.findById(groundId)
                                                .map(ground -> ground.getName())
                                                .orElse("Unknown Ground");

            // Assuming review and rating might be available, else set them as null or default values.
            Integer rating = null; // You can fetch it if needed from your booking data.
            String review = null;  // Similarly, fetch the review if you have it in your data.
            String address = "Ground Address"; // You can fetch the address similarly if available.

            // Map the data to BookingDTO
            BookingDTO booking = new BookingDTO(bookingId, bookingDate, startTime, endTime, 
                                                groundName, paymentStatus, rating, review, address);
            bookings.add(booking);
        }

        return bookings;  // Return the mapped list of BookingDTOs
    }

    
    
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
    
    public List<Map<String, Object>> getTodayBookings() {
        List<Booking> bookings = bookingRepository.findTodayBookings();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Booking booking : bookings) {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("id", booking.getId());
            bookingData.put("bookingDate", booking.getBookingDate().toString()); // ✅ Add Booking Date

            
            // Fetch user details
            userRepository.findById(booking.getUserId()).ifPresent(user -> 
                bookingData.put("userName", user.getName())
            );

            // Fetch game details
            gameRepository.findById(booking.getGameId()).ifPresent(game -> 
                bookingData.put("gameName", game.getGameName())
            );

            // Fetch ground details
            groundRepository.findById(booking.getGroundId()).ifPresent(ground -> 
                bookingData.put("groundName", ground.getName())
            );

            // Fetch slot details
            slotRepository.findById(booking.getSlotId()).ifPresent(slot -> {
                bookingData.put("startTime", slot.getStartTime());
                bookingData.put("endTime", slot.getEndTime());
            });

            bookingData.put("price", booking.getPrice());
            bookingData.put("paymentStatus", booking.getPaymentStatus() == 1 ? "Paid" : "Unpaid");

            result.add(bookingData);
        }
        return result;
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
    
    public List<Map<String, Object>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Booking booking : bookings) {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("id", booking.getId());
            bookingData.put("bookingDate", booking.getBookingDate().toString());

            userRepository.findById(booking.getUserId()).ifPresent(user -> 
                bookingData.put("userName", user.getName())
            );

            gameRepository.findById(booking.getGameId()).ifPresent(game -> 
                bookingData.put("gameName", game.getGameName())
            );

            groundRepository.findById(booking.getGroundId()).ifPresent(ground -> 
                bookingData.put("groundName", ground.getName())
            );

            slotRepository.findById(booking.getSlotId()).ifPresent(slot -> {
                bookingData.put("startTime", slot.getStartTime());
                bookingData.put("endTime", slot.getEndTime());
            });

            bookingData.put("price", booking.getPrice());
            bookingData.put("paymentStatus", booking.getPaymentStatus() == 1 ? "Paid" : "Unpaid");

            result.add(bookingData);
        }
        return result;
    }
    
    public List<Map<String, Object>> filterBookings(String groundName, String userName, String gameName, LocalDate bookingDate) {
        List<Booking> bookings = bookingRepository.findAll();
        
        if (groundName != null && !groundName.isEmpty()) {
            bookings = bookings.stream()
                .filter(b -> groundRepository.findById(b.getGroundId())
                    .map(Ground::getName).orElse("").equalsIgnoreCase(groundName))
                .collect(Collectors.toList());
        }

        if (userName != null && !userName.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> userRepository.findById(b.getUserId())
                        .map(User::getName).orElse("").equalsIgnoreCase(userName))
                    .collect(Collectors.toList());
            }

        if (gameName != null && !gameName.isEmpty()) {
            bookings = bookings.stream()
                .filter(b -> gameRepository.findById(b.getGameId())
                    .map(Game::getGameName).orElse("").equalsIgnoreCase(gameName))
                .collect(Collectors.toList());
        }

        if (bookingDate != null) {
            bookings = bookings.stream()
                .filter(b -> b.getBookingDate().equals(bookingDate))
                .collect(Collectors.toList());
        }

        return bookings.stream().map(b -> {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("id", b.getId());
            bookingData.put("bookingDate", b.getBookingDate().toString());
            bookingData.put("userName", userRepository.findById(b.getUserId()).map(User::getName).orElse(""));
            bookingData.put("gameName", gameRepository.findById(b.getGameId()).map(Game::getGameName).orElse(""));
            slotRepository.findById(b.getSlotId()).ifPresent(slot -> {
                bookingData.put("startTime", slot.getStartTime().toString());
                bookingData.put("endTime", slot.getEndTime().toString());
            });
            bookingData.put("groundName", groundRepository.findById(b.getGroundId()).map(Ground::getName).orElse(""));
            bookingData.put("price", b.getPrice());
            bookingData.put("paymentStatus", b.getPaymentStatus() == 1 ? "Paid" : "Unpaid");
            return bookingData;
        }).collect(Collectors.toList());
    }


    
    public Long getUserIdByName(String name) {
        User user = userRepository.findByName(name);
        return (user != null) ? user.getId() : null;
    }
    
    
    public int countBookingsByGameId(Long gameId) {
        return bookingRepository.countByGameId(gameId);
    }

    public double getTotalRevenueByGameId(Long gameId) {
        return bookingRepository.findTotalRevenueByGameId(gameId).orElse(0.0);
    }

    public Long getMostPopularGroundByGameId(Long gameId) {
        return bookingRepository.findMostPopularGroundByGameId(gameId).orElse(-1L);
    }


}
