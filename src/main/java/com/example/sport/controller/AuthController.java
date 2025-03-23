package com.example.sport.controller;

import com.example.sport.model.*;
import com.example.sport.repository.*;
import com.example.sport.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameService gameService;

    @Autowired
    private GroundService groundService;
    
    @Autowired
    private BookingService bookingService;


    @Autowired
    private SlotService slotService;

    @GetMapping("/login1")
    public String showLoginPage() {
        return "login"; // This remains the same
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String password, 
                        HttpServletRequest request, 
                        Model model,
                        HttpServletResponse response) {
        
        // Input validation
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Email and password are required");
            return "login1";
        }

        try {
            User user = userRepository.findByEmailAndPassword(email, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                
                // Set session attributes
                session.setAttribute("loggedInUser", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole().getRoleName());
                
                // Set session timeout (30 minutes)
                session.setMaxInactiveInterval(30 * 60);
                
                // Add security headers
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("X-Content-Type-Options", "nosniff");
                
                // Role-based redirection
                String redirectPath = user.getRole().getRoleName().equalsIgnoreCase("ADMIN") ? 
                    "redirect:/admin/dashboard" : "redirect:/user/dashboard";
                
                // Add login timestamp
                session.setAttribute("loginTime", System.currentTimeMillis());
                
                return redirectPath;
            } else {
                // Security: Delay response for invalid credentials
                Thread.sleep(2000); // 2 second delay
                model.addAttribute("error", "Invalid email or password");
                return "login1";
            }
        } catch (Exception e) {
            // Log the error
            
            model.addAttribute("error", "An error occurred during login. Please try again.");
            return "login1";
        }
    }


    


    @GetMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
        return "redirect:/login1";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login1"; // Redirect if not logged in
        }

        // Fetch all games and ground locations dynamically
        List<Game> games = gameService.getAllGames();
        List<Ground> grounds = groundService.getAllGrounds();
        List<Ground> allGrounds = groundService.getAllGrounds();
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("games", games);
        model.addAttribute("grounds", grounds);
        model.addAttribute("grounds", allGrounds); 
        return "user/userDash";
    }



    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Check if session exists
        if (loggedInUser == null) {
            return "redirect:/login1"; // Redirect if not logged in
        }

        // Ensure user has ADMIN role
        if (!"ADMIN".equals(loggedInUser.getRole().getRoleName())) {
            return "redirect:/user/dashboard"; // Redirect non-admin users
        }

        model.addAttribute("loggedInUser", loggedInUser); // Pass admin details to view
        return "admin/adminDash";
    }


}
