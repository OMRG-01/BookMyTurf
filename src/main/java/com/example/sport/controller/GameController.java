package com.example.sport.controller;

import com.example.sport.model.Game;
import com.example.sport.repository.CoachRepository;
import com.example.sport.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private CoachRepository coachRepository;
   @Autowired
    private GroundRepository groundRepository;

    // Show the game form (View all games and add new game)
    @GetMapping("/admin/game")
    public String showGameForm(Model model) {
        model.addAttribute("games", gameRepository.findAll());
        return "admin/game"; // Points to game.html
    }

    // Add a new game
    @PostMapping("/admin/addGame")
    public String addGame(@RequestParam String gameName, 
                          @RequestParam int playerCapacity, 
                          @RequestParam String type, 
                          Model model) {

        // Check if the game already exists
        if (gameRepository.existsByGameName(gameName)) {
            model.addAttribute("error", "Game already exists!");
            return "admin/game";
        }

        // Save the new game
        Game game = new Game(gameName, playerCapacity, type);
        gameRepository.save(game);
        return "redirect:/admin/game"; // Reload the page after saving
    }

    @GetMapping("/admin/deleteGame/{id}")
    public String deleteGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Delete related coaches first
            coachRepository.deleteByGameId(id);

            // Delete related ground records
            groundRepository.deleteByGameId(id);

            // Attempt to delete the game
            gameRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("success", "Game deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete game. It is linked to another record.");
        }

        return "redirect:/admin/game"; // Redirect to games list
    }



    // Edit a game by its ID (For updating the game details)
    @GetMapping("/admin/editGame/{id}")
    public String editGame(@PathVariable Long id, Model model) {
        // Fetch the game object from the database
        Game game = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid game Id: " + id));
        
        // Add the game object to the model
        model.addAttribute("game", game);
        
        // Return the view for editing the game
        return "admin/editGame";  // Make sure the path corresponds to your template's name
    }



    // Update the game details
    @PostMapping("/admin/updateGame")
    public String updateGame(@RequestParam Long id, 
                             @RequestParam String gameName, 
                             @RequestParam int playerCapacity, 
                             @RequestParam String type) {

        Game game = gameRepository.findById(id).orElse(null);
        if (game != null) {
            game.setGameName(gameName);
            game.setPlayerCapacity(playerCapacity);
            game.setType(type);
            gameRepository.save(game); // Save the updated game
        }
        return "redirect:/admin/game"; // Redirect to the game list after update
    }
}
