package com.example.sport.controller;

import com.example.sport.model.User;
import com.example.sport.repository.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

        model.addAttribute("loggedInUser", loggedInUser); // Pass user to view
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
