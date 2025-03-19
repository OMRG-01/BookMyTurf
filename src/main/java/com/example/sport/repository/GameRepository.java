package com.example.sport.repository;

import com.example.sport.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByGameName(String gameName); // To check for duplicate names
}
