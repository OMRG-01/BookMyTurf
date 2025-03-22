package com.example.sport.controller;

import com.example.sport.model.Booking;
import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.model.User;
import com.example.sport.service.BookingService;
import com.example.sport.service.EmailService;
import com.example.sport.service.GameService;
import com.example.sport.service.GroundService;
import com.example.sport.service.OTPService;
import com.example.sport.service.SlotService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BookingController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GroundService groundService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private OTPService otpService;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/booking")
    public String userDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login1"; // Redirect if not logged in
        }

        List<Game> games = gameService.getAllGames();
        List<Ground> grounds = groundService.getAllGrounds();

        // Extract unique city names using Set
        Set<String> uniqueCities = grounds.stream()
                                          .map(Ground::getLocation)
                                          .collect(Collectors.toSet());

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("games", games);
        model.addAttribute("grounds", grounds);
        model.addAttribute("uniqueCities", uniqueCities); // Pass unique cities

        return "user/booking";
    }
    
 // ✅ API to get games based on selected city
    @GetMapping("/getGamesByCity")
    @ResponseBody
    public List<Map<String, Object>> getGamesByCity(@RequestParam String city) {
        List<Ground> grounds = groundService.getAllGrounds();
        
        return grounds.stream()
            .filter(ground -> ground.getLocation().equalsIgnoreCase(city))
            .map(ground -> {
                Map<String, Object> gameData = new HashMap<>();
                gameData.put("id", ground.getGame().getId());
                gameData.put("name", ground.getGame().getGameName());
                return gameData;
            })
            .distinct()
            .collect(Collectors.toList());
    }
    
    
    @GetMapping("/getGroundsByCityAndGame")
    @ResponseBody
    public List<Map<String, String>> getGroundsByCityAndGame(
            @RequestParam String city,
            @RequestParam Long gameId) { // Changed to receive gameId
        
        List<Ground> grounds = groundService.getAllGrounds();

        return grounds.stream()
            .filter(ground -> ground.getLocation().equalsIgnoreCase(city) &&
                              ground.getGame().getId().equals(gameId)) // Filter by game ID
            .map(ground -> {
                Map<String, String> groundData = new HashMap<>();
                groundData.put("id", String.valueOf(ground.getId()));
                groundData.put("name", ground.getName());
                return groundData;
            })
            .collect(Collectors.toList());
    }

    
    @GetMapping("/getSlotsByGround")
    @ResponseBody
    public List<Map<String, Object>> getSlotsByGround(@RequestParam Long groundId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date bookingDate) {
    	
        List<Slot> slots = slotService.getSlotsByGroundId(groundId);
       
        return slots.stream().map(slot -> {
            Map<String, Object> slotData = new HashMap<>();
            slotData.put("id", slot.getId());
            slotData.put("startTime", slot.getStartTime().toString());
            slotData.put("endTime", slot.getEndTime().toString());
            slotData.put("availability", slot.isAvailability() ? "Available" : "Booked");
            slotData.put("price", slot.getPrice());
            slotData.put("weekendPrice", slot.getWeekendPrice());
            slotData.put("breakTime", slot.getBreakTime());
            
            // Add payment status (1 = Paid, 0 = Unpaid)
            boolean isPaid = bookingService.isSlotPaid(slot.getId());
            slotData.put("isPaid", isPaid);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(bookingDate);
            boolean isWeekend = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY 
                             || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            
            slotData.put("currentPrice", isWeekend ? slot.getWeekendPrice() : slot.getPrice());
            return slotData;
        }).collect(Collectors.toList());
    }
    @PostMapping("/generate-otp")
    @ResponseBody
    public ResponseEntity<?> generateOTP(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        
        try {
            String otp = otpService.generateOTP(user.getEmail());
            emailService.sendOTPEmail(user.getEmail(), otp);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send OTP");
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(
            @RequestParam Long gameId,
            @RequestParam Long groundId,
            @RequestParam Long slotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
            @RequestParam Double price,
            @RequestParam String otp,
            HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return ResponseEntity.badRequest().body("User not logged in");
        
        if (!otpService.validateOTP(user.getEmail(), otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setGameId(gameId);
        booking.setGroundId(groundId);
        booking.setSlotId(slotId);
        booking.setPaymentStatus(0);
        booking.setBookingDate(bookingDate);
        booking.setPrice(price);
        
        bookingService.saveBooking(booking);
        
        return ResponseEntity.ok("/user/gateway?bookingId=" + booking.getId());
    }    
    @GetMapping("/user/gateway")
    public String showGateway(@RequestParam Long bookingId, Model model) {
        // Add booking details to model if needed
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "user/gateway"; // This will resolve to templates/user/gateway.html
    }
    @PostMapping("/update-payment-status")
    public ResponseEntity<?> updatePaymentStatus(
            @RequestParam Long bookingId,
            @RequestParam int status) {
        
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            booking.setPaymentStatus(status);
            bookingService.saveBooking(booking);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}