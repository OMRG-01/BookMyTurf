package com.example.sport.controller;

import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.model.Role;
import com.example.sport.model.User;
import com.example.sport.repository.UserRepository;
import com.example.sport.service.EmailService;
import com.example.sport.service.GameService;
import com.example.sport.service.GroundService;
import com.example.sport.service.OTPService;
import com.example.sport.service.RoleService;
import com.example.sport.service.UserService;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroundService groundService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private OTPService otpService;
    
    @Autowired
    private GameService gameService;
    
    @Autowired 
    private UserRepository userRepository;
    
 // Load Profile Page
    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user/profile"; // Redirects to profile.html
    }

    // Update User
 // 👉 Handle Profile Update
    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute("user") User user, Model model) {
        userService.updateUser(user); // Update user in DB
        
        // Fetch updated user details
        User updatedUser = userService.getUserById(user.getId());
        model.addAttribute("loggedInUser", updatedUser);

        // Redirect to user dashboard with updated user data
        return "user/userDash"; // user/userDash.html
    }

    // Show the registration form
    @GetMapping("/register1")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user/register";  // The path to your register.html page
    }

    // Handle the registration form submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {

        // Fetch the "USER" role (role_id = 2)
        Role userRole = roleService.findById(2L);

        // Set the role to "USER"
        user.setRole(userRole);

        // Save the user to the database
        userService.save(user);

        return "redirect:/register1";  // Redirect to the login page after successful registration
    }
    
    @GetMapping("/viewGrounds")
    public String viewGrounds(Model model) {
        List<Ground> grounds = groundService.getAllGrounds();
        model.addAttribute("grounds", grounds);
        return "admin/viewGround";
    }
    
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        User user = userOptional.get();
        String otp = otpService.generateOTP(email);
        emailService.sendOTPEmail(email, otp);
        System.out.println("OTP generated: " + otp);

        return ResponseEntity.ok("OTP sent successfully to " + email);
    }

    
    @PostMapping("/verify-otp")
    @ResponseBody
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOTP(email, otp)) {
            return ResponseEntity.ok("OTP Verified. Proceed to reset password.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }
    }
    
    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestParam String email, 
                                           @RequestParam String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        user.setPassword(newPassword); // Ideally, hash the password before saving
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful. You can now log in.");
    }




}
