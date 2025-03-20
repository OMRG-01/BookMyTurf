package com.example.sport.controller;

import com.example.sport.model.User;
import com.example.sport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    // Show all users with role 'USER'
    @GetMapping("/admin/users")
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/userList";
    }

    // View user details
    @GetMapping("/admin/user/{id}")
    public String viewUserDetails(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/userDetails :: userDetails"; // Use a fragment for user details
    }


    // Delete user by ID
    @PostMapping("/admin/user/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/user";  // Redirect to the user list page after deletion
    }

}
