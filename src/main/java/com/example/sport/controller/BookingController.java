package com.example.sport.controller;

import com.example.sport.model.Booking;
import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.model.User;
import com.example.sport.service.BookingService;
import com.example.sport.service.GameService;
import com.example.sport.service.GroundService;
import com.example.sport.service.SlotService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
    public List<String> getGamesByCity(@RequestParam String city) {
        List<Ground> grounds = groundService.getAllGrounds();

        // Get unique game names where the ground location matches the selected city
        Set<String> gamesInCity = grounds.stream()
            .filter(ground -> ground.getLocation().equalsIgnoreCase(city))
            .map(ground -> ground.getGame().getGameName()) // Extract game name
            .collect(Collectors.toSet());

        return new ArrayList<>(gamesInCity); // Convert Set to List and return
    }
    
    
    @GetMapping("/getGroundsByCityAndGame")
    @ResponseBody
    public List<Map<String, String>> getGroundsByCityAndGame(@RequestParam String city, @RequestParam String game) {
        List<Ground> grounds = groundService.getAllGrounds();

        return grounds.stream()
            .filter(ground -> ground.getLocation().equalsIgnoreCase(city) &&
                              ground.getGame().getGameName().equalsIgnoreCase(game))
            .map(ground -> {
                Map<String, String> groundData = new HashMap<>();
                groundData.put("id", String.valueOf(ground.getId())); // Send ID as String
                groundData.put("name", ground.getName()); // Send Name
                return groundData;
            })
            .collect(Collectors.toList());
    }

    
    @GetMapping("/getSlotsByGround")
    @ResponseBody
    public List<Map<String, Object>> getSlotsByGround(@RequestParam Long groundId) {
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
            return slotData;
        }).collect(Collectors.toList());
        
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(
            @RequestParam Long gameId,
            @RequestParam Long groundId,
            @RequestParam Long slotId,
            HttpSession session) {

        // Get the logged-in user ID from the session (Assuming user is logged in)
        Long userId = (Long) session.getAttribute("userId");  
        if (userId == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }

        // Create a new booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setGameId(gameId);
        booking.setGroundId(groundId);
        booking.setSlotId(slotId);
        booking.setPaymentStatus(0); // Unpaid

        bookingService.saveBooking(booking);

        // Redirect user to payment page with booking details
        return ResponseEntity.ok("/gateway.html?bookingId=" + booking.getId());
    }
}
