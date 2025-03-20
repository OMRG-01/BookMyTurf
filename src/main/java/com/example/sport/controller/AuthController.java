package com.example.sport.controller;

import com.example.sport.model.*;
import com.example.sport.model.User;
import com.example.sport.repository.*;
import com.example.sport.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/login1")
    public String showLoginPage() {
        return "login"; // This remains the same
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpServletRequest request, Model model) {
        User user = userRepository.findByEmailAndPassword(email, password);

        if (user != null) {
            HttpSession session = request.getSession(true); // Create session
            session.setAttribute("loggedInUser", user);
            return user.getRole().getRoleName().equals("ADMIN") ? "redirect:/admin/dashboard" : "redirect:/user/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
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
        List<Ground> userCityGrounds = groundService.getGroundsByCity(loggedInUser.getCity());
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("games", games);
        model.addAttribute("grounds", grounds);
        model.addAttribute("grounds", userCityGrounds);
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
