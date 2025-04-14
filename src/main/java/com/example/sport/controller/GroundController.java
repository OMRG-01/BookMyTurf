package com.example.sport.controller;

import com.example.sport.model.*;
import com.example.sport.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/ground")
public class GroundController {

    @Autowired
    private GroundService groundService;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private CoachService coachService;
    // Show Add Ground Form
    @GetMapping("/add")
    public String showAddGroundForm(Model model) {
        model.addAttribute("ground", new Ground());
        model.addAttribute("games", gameService.getAllGames());
        return "admin/ground";
    }

    // Save Ground
    @PostMapping("/save")
    public String saveGround(@ModelAttribute Ground ground, 
                             @RequestParam("game.id") Long gameId,
                             @RequestParam(value = "coach.id", required = false) Long coachId,
                             @RequestParam("openingTime") String openingTimeStr,
                             @RequestParam("closingTime") String closingTimeStr,
                             @RequestParam("image") MultipartFile image,
                             RedirectAttributes redirectAttributes) throws IOException {

        try {
            // Step 1: Handle Game
            Game existingGame = gameService.getGameById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));
            ground.setGame(existingGame);

            // Step 2: Handle Coach (Allow Null)
            if (coachId != null) {
                Coach existingCoach = coachService.getCoachById(coachId)
                    .orElseThrow(() -> new RuntimeException("Coach not found with ID: " + coachId));
                ground.setCoach(existingCoach);
            } else {
                ground.setCoach(null);
            }

            // Step 3: Convert time input
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            ground.setOpeningTime(LocalTime.parse(openingTimeStr, formatter));
            ground.setClosingTime(LocalTime.parse(closingTimeStr, formatter));

            // Step 4: Save ground
            groundService.saveGround(ground);

            // Step 5: Save image
            if (!image.isEmpty()) {
                String fileName = image.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/GroundImg/" + fileName);
                Files.copy(image.getInputStream(), path);
                ground.setImageFileName(fileName);
                groundService.saveGround(ground);
            }

            // Add success message
            redirectAttributes.addFlashAttribute("success", "Ground added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong while adding the ground.");
        }

        return "redirect:/admin/ground/add";
    }


    @GetMapping("/viewGrounds")
    public String viewGrounds(Model model) {
        List<Ground> grounds = groundService.getAllGrounds();
        model.addAttribute("grounds", grounds);
        return "admin/viewGround";
    }

    // Show List of Grounds
    @GetMapping("/list")
    public String showGroundList(Model model) {
        model.addAttribute("grounds", groundService.getAllGrounds());
        return "admin/groundList"; // Page to display all grounds
    }

    // Show Edit Form
    @GetMapping("/edit/{id}")
    public String editGround(@PathVariable Long id, Model model) {
        Ground ground = groundService.getGroundById(id);
        List<Game> games = gameService.getAllGames();
        List<Coach> coaches = coachService.getAllCoaches();

        model.addAttribute("ground", ground);
        model.addAttribute("games", games);
        model.addAttribute("coaches", coaches);

        return "admin/editGround"; // Load editGround.html
    }


    // Update Ground
    @PostMapping("/update")
    public String saveGroundWithCoach(
            @ModelAttribute Ground ground,
            @RequestParam("image") MultipartFile file,
            @RequestParam("coachId") Long coachId,
            @RequestParam("gameId") Long gameId) {  // ✅ Accept Game ID from form
        
        if (!file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            ground.setImageFileName(fileName);
            String uploadDir = "src/main/resources/static/GroundImg/";

            try {
                Path path = Paths.get(uploadDir + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ✅ Fetch the Coach object
        Coach coach = coachService.getCoachById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found with ID: " + coachId));
        ground.setCoach(coach);

        // ✅ Fetch the Game object
        Game game = gameService.getGameById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));
        ground.setGame(game);  // ✅ Assign the fetched game

        groundService.saveGround(ground);
        return "redirect:/admin/ground/viewGrounds";
    }


    // Delete Ground
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteGround(@PathVariable Long id) {
        // Fetch the ground before deleting
    	Ground ground = groundService.getGroundById(id);
    	if (ground == null) {
    	    throw new RuntimeException("Ground not found with ID: " + id);
    	}

        // Get the image filename
        String imageFileName = ground.getImageFileName();

        // Delete the image file if it exists
        if (imageFileName != null && !imageFileName.isEmpty()) {
            String imagePath = "src/main/resources/static/GroundImg/" + imageFileName;
            File imageFile = new File(imagePath);
            
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    System.out.println("Image deleted: " + imagePath);
                } else {
                    System.out.println("Failed to delete image: " + imagePath);
                }
            }
        }

        // Delete the ground record from the database
        groundService.deleteGround(id);

        return ResponseEntity.ok("Ground and its image deleted successfully");
    }

}
