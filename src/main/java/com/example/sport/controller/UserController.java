package com.example.sport.controller;

import com.example.sport.model.Ground;
import com.example.sport.model.Role;
import com.example.sport.model.User;
import com.example.sport.service.GroundService;
import com.example.sport.service.RoleService;
import com.example.sport.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GroundService groundService;
    
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
}
