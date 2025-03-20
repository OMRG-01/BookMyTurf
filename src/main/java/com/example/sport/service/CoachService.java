package com.example.sport.service;

import com.example.sport.model.Coach;
import com.example.sport.model.Game;
import com.example.sport.repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {
	@Autowired
    private CoachRepository coachRepository;

    @Autowired
    private GameService gameService;  // ✅ Now using GameService

    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    public Optional<Coach> getCoachById(Long id) {
        return coachRepository.findById(id);
    }

    public Coach saveCoach(Coach coach) {
        return coachRepository.save(coach);
    }

    public List<Game> getAllGames() {
        return gameService.getAllGames();  // ✅ Fetching games via GameService
    }

    public void deleteCoach(Long id) {
        coachRepository.deleteById(id);
    }
    
    public List<Coach> getCoachesByGame(Long gameId) {
        return coachRepository.findByGameId(gameId);
    }

}
