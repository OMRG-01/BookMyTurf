package com.example.sport.controller;

import com.example.sport.model.Game;
import com.example.sport.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/admin/game")
    public String showGameForm(Model model) {
        model.addAttribute("games", gameRepository.findAll());
        return "admin/game"; // Points to game.html
    }

    @PostMapping("/admin/addGame")
    public String addGame(@RequestParam String gameName, 
                          @RequestParam int playerCapacity, 
                          @RequestParam String type, 
                          Model model) {

        if (gameRepository.existsByGameName(gameName)) {
            model.addAttribute("error", "Game already exists!");
            return "admin/game";
        }

        Game game = new Game(gameName, playerCapacity, type);
        gameRepository.save(game);
        return "redirect:/admin/game"; // Reload the page after saving
    }
}
