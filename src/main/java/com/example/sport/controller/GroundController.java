package com.example.sport.controller;

import com.example.sport.model.*;
import com.example.sport.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/ground")
public class GroundController {

    @Autowired
    private GroundService groundService;
    
    @Autowired
    private GameService gameService;
    // Show Add Ground Form
    @GetMapping("/add")
    public String showAddGroundForm(Model model) {
        model.addAttribute("ground", new Ground());
        model.addAttribute("games", gameService.getAllGames());
        return "admin/ground";
    }

    // Save Ground
    @PostMapping("/save")
    public String saveGround(@ModelAttribute Ground ground, @RequestParam("game.id") Long gameId,
                             @RequestParam("openingTime") String openingTimeStr,
                             @RequestParam("closingTime") String closingTimeStr) {
        Game existingGame = gameService.getGameById(gameId)
                            .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));
        ground.setGame(existingGame);

        // Convert time input to LocalTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        ground.setOpeningTime(LocalTime.parse(openingTimeStr, formatter));
        ground.setClosingTime(LocalTime.parse(closingTimeStr, formatter));

        groundService.saveGround(ground);
        return "redirect:/admin/dashboard";
    }



    // Show List of Grounds
    @GetMapping("/list")
    public String showGroundList(Model model) {
        model.addAttribute("grounds", groundService.getAllGrounds());
        return "admin/groundList"; // Page to display all grounds
    }

    // Show Edit Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Ground ground = groundService.getGroundById(id);
        model.addAttribute("ground", ground);
        model.addAttribute("games", groundService.getAllGames()); // Fetch all games
        return "admin/ground"; // Reuse same form for editing
    }

    // Update Ground
    @PostMapping("/update/{id}")
    public String updateGround(@PathVariable Long id, @ModelAttribute Ground ground) {
        groundService.updateGround(id, ground);
        return "redirect:/admin/ground/list";
    }

    // Delete Ground
    @GetMapping("/delete/{id}")
    public String deleteGround(@PathVariable Long id) {
        groundService.deleteGround(id);
        return "redirect:/admin/ground/list";
    }
}
