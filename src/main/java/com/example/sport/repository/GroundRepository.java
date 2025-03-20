package com.example.sport.repository;

import com.example.sport.model.Ground;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroundRepository extends JpaRepository<Ground, Long> {
	 @Query("SELECT DISTINCT s.ground.id FROM Slot s")
	    List<Long> findUsedGroundIds();
	 
	 	@Modifying
	    @Transactional
	    @Query("DELETE FROM Ground g WHERE g.game.id = :gameId")
	    void deleteByGameId(@Param("gameId") Long gameId);
	 	
	 	@Query("SELECT g FROM Ground g WHERE g.location = :city")
	 	List<Ground> findByLocation(@Param("city") String city);

}
