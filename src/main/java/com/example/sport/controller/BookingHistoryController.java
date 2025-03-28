package com.example.sport.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sport.dto.BookingDTO;
import com.example.sport.model.Booking;
import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.model.User;
import com.example.sport.service.BookingService;
import com.example.sport.service.GameService;
import com.example.sport.service.GroundService;
import com.example.sport.service.SlotService;
import com.example.sport.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller // ✅ Change @Controller to @RestController to return JSON data
@RequestMapping("/bookingHistory")
public class BookingHistoryController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private GroundService groundService;
    
 
    @Autowired
    private UserService userService;

    @Autowired
    private SlotService slotService;
    
    

    @GetMapping("/myBookings")
    public String showMyBookings(@RequestParam(value = "userId", required = false) Long userId, 
                                 HttpServletRequest request, Model model) {
        
        // 🔥 Check if userId is provided in request (from userDash.html)
        if (userId == null) {
            HttpSession session = request.getSession(false); // Get session if exists
            if (session == null || session.getAttribute("loggedInUser") == null) {
                return "redirect:/login1"; // Redirect to login if user is not authenticated
            }

            User loggedInUser = (User) session.getAttribute("loggedInUser");
            userId = loggedInUser.getId(); // Get User ID from session
        }

        // ✅ Fetch user details
        User loggedInUser = userService.findById(userId);
        if (loggedInUser == null) {
            return "redirect:/login1"; // Handle invalid user
        }

        // ✅ Fetch required data
        List<Ground> grounds = groundService.getAllGrounds();
        List<Slot> slots = slotService.getAllSlots();

        // ✅ Pass data to the model
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("grounds", grounds);
        model.addAttribute("slots", slots);

        return "user/myBooking"; // 🎯 Points to `myBooking.html`
    }

    
    @GetMapping("/past")
    public ResponseEntity<?> getPastBookings(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is missing! Please log in.");
        }

        List<BookingDTO> bookings = bookingService.getPastBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingBookings(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is missing! Please log in.");
        }

        List<BookingDTO> bookings = bookingService.getUpcomingBookings(userId);
        return ResponseEntity.ok(bookings);
    }


    @GetMapping("/all")
    public ResponseEntity<List<BookingDTO>> getAllBookings(@RequestParam Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<BookingDTO> bookings = bookingService.getAllBookings(userId);
        return ResponseEntity.ok(bookings);
    }

}
