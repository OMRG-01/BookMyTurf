package com.example.sport.controller;

import com.example.sport.model.Coach;
import com.example.sport.model.Game;
import com.example.sport.service.CoachService;
import com.example.sport.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CoachController {

    @Autowired
    private CoachService coachService;

    @Autowired
    private GameService gameService;
    @GetMapping("/admin/coach/byGame/{gameId}")
    @ResponseBody
    public List<Coach> getCoachesByGame(@PathVariable Long gameId) {
        return coachService.getCoachesByGame(gameId);
    }
    
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
    @GetMapping("/admin/coaches")
    public String viewAllCoaches(Model model) {
        List<Coach> coaches = coachService.getAllCoaches();
        model.addAttribute("coaches", coaches);
        return "admin/viewCoach";
    }
    
    @GetMapping("/admin/coach/delete/{id}")
    public String deleteCoach(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            coachService.deleteCoach(id);
            redirectAttributes.addFlashAttribute("success", "Coach deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete coach. It is linked to another record.");
        }

        return "redirect:/admin/coaches";
    }


    // ✅ Redirect to the Update Coach form
    @GetMapping("/admin/coach/update/{id}")
    public String updateCoachForm(@PathVariable Long id, Model model) {
        Optional<Coach> coach = coachService.getCoachById(id);
        if (coach.isPresent()) {
            model.addAttribute("coach", coach.get());
            model.addAttribute("games", gameService.getAllGames());
            return "admin/updateCoach";
        }
        return "redirect:/admin/coaches";
    }

    // ✅ Handle Coach update
    @PostMapping("/admin/coach/update")
    public String updateCoach(@RequestParam Long id,
                              @RequestParam String name,
                              @RequestParam int experience,
                              @RequestParam String specialization,
                              @RequestParam String phoneNumber,
                              @RequestParam String email,
                              @RequestParam double salary,
                              @RequestParam Long gameId) {

        Optional<Game> gameOpt = gameService.getGameById(gameId);
        Optional<Coach> coachOpt = coachService.getCoachById(id);

        if (coachOpt.isPresent() && gameOpt.isPresent()) {
            Coach coach = coachOpt.get();
            coach.setName(name);
            coach.setExperience(experience);
            coach.setSpecialization(specialization);
            coach.setPhoneNumber(phoneNumber);
            coach.setEmail(email);
            coach.setSalary(salary);
            coach.setGame(gameOpt.get());

            coachService.saveCoach(coach);
        }
        return "redirect:/admin/coaches";
    }
}
