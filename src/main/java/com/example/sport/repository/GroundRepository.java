package com.example.sport.repository;

import com.example.sport.model.Ground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroundRepository extends JpaRepository<Ground, Long> {
	 @Query("SELECT DISTINCT s.ground.id FROM Slot s")
	    List<Long> findUsedGroundIds();
}
