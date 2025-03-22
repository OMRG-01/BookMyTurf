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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            
            // Add payment status (1 = Paid, 0 = Unpaid)
            boolean isPaid = bookingService.isSlotPaid(slot.getId());
            slotData.put("isPaid", isPaid);
            
            return slotData;
        }).collect(Collectors.toList());
    }
    
    @PostMapping("/create")
    public String createBooking(
            @RequestParam Long gameId,
            @RequestParam Long groundId,
            @RequestParam Long slotId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Get the complete user object from session
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not logged in");
            return "redirect:/login1";
        }

        // Create a new booking using the ID from the user object
        Booking booking = new Booking();
        booking.setUserId(loggedInUser.getId());
        booking.setGameId(gameId);
        booking.setGroundId(groundId);
        booking.setSlotId(slotId);
        booking.setPaymentStatus(0);

        bookingService.saveBooking(booking);

        // Add booking ID as path variable
        redirectAttributes.addAttribute("bookingId", booking.getId());
        
        // Redirect to gateway page
        return "redirect:/user/gateway";
    }
    
    @GetMapping("/user/gateway")
    public String showGateway(@RequestParam Long bookingId, Model model) {
        // Add booking details to model if needed
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("booking", booking);
        return "user/gateway"; // This will resolve to templates/user/gateway.html
    }
    
}
