package com.example.sport.controller;

import com.example.sport.model.Coach;
import com.example.sport.model.Game;
import com.example.sport.service.CoachService;
import com.example.sport.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CoachController {

    @Autowired
    private CoachService coachService;

    @Autowired
    private GameService gameService;

    @GetMapping("/admin/coach")
    public String showCoachPage(Model model) {
        List<Game> games = gameService.getAllGames();  // ✅ Fetching games
        model.addAttribute("games", games);
        return "admin/coach";
    }

    @PostMapping("/admin/coach/add")
    public String addCoach(@RequestParam String name, 
                           @RequestParam String email, 
                           @RequestParam Long gameId,
                           @RequestParam double salary,
                           @RequestParam int experience,
                           @RequestParam String specialization,
                           @RequestParam String phoneNumber) {
        Game game = gameService.getGameById(gameId).orElse(null);

        if (game != null) {
            Coach coach = new Coach();
            coach.setName(name);
            coach.setEmail(email);
            coach.setGame(game);
            coach.setSalary(salary);
            coach.setExperience(experience);
            coach.setSpecialization(specialization);
            coach.setPhoneNumber(phoneNumber);
            coachService.saveCoach(coach);
        }

        return "redirect:/admin/coach";
    }
}
