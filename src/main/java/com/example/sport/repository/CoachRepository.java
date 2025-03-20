package com.example.sport.repository;

import com.example.sport.model.Coach;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoachRepository extends JpaRepository<Coach, Long> {
	@Modifying
    @Transactional
    @Query("DELETE FROM Coach c WHERE c.game.id = :gameId")
    void deleteByGameId(@Param("gameId") Long gameId);
	
	List<Coach> findByGameId(Long gameId);
}
