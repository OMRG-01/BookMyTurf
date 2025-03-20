package com.example.sport.service;

import com.example.sport.model.*;
import com.example.sport.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GroundService {

    @Autowired
    private GroundRepository groundRepository;

    @Autowired
    private GameRepository gameRepository;

    // Save ground
    public void saveGround(Ground ground) {
        groundRepository.save(ground);
    }

    // Fetch all grounds
    public List<Ground> getAllGrounds() {
        return groundRepository.findAll();
    }
    public List<Ground> getGroundsByCity(String city) {
        return groundRepository.findByLocation(city);
    }

    // Fetch ground by ID
    public Ground getGroundById(Long id) {
        return groundRepository.findById(id).orElseThrow(() -> new RuntimeException("Ground not found"));
    }
    

    public GroundService(GroundRepository groundRepository) {
        this.groundRepository = groundRepository;
    }

    public List<Long> getUsedGroundIds() {
        return groundRepository.findUsedGroundIds();
    }

    // Update ground details
    public Ground updateGround(Long id, Ground updatedGround) {
        Optional<Ground> existingGround = groundRepository.findById(id);
        if (existingGround.isPresent()) {
            Ground ground = existingGround.get();
            ground.setName(updatedGround.getName());
            ground.setLocation(updatedGround.getLocation());
            ground.setPlayerCapacity(updatedGround.getPlayerCapacity());
            ground.setSurfaceType(updatedGround.getSurfaceType());
            ground.setFloodlightsAvailable(updatedGround.isFloodlightsAvailable());
            ground.setChangingRooms(updatedGround.isChangingRooms());
            ground.setOpeningTime(updatedGround.getOpeningTime());
            ground.setClosingTime(updatedGround.getClosingTime());
            return groundRepository.save(ground);
        }
        return null;
    }

    // Delete ground
    public void deleteGround(Long id) {
        groundRepository.deleteById(id);
    }

    // Get all games (For dropdown)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
}