package com.example.sport.repository;

import com.example.sport.model.Slot;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByGroundId(Long groundId); // Fetch slots for a specific ground
    @Query("SELECT DISTINCT s.ground.id FROM Slot s")
    List<Long> findDistinctGroundIds();
    
    @Query("SELECT DISTINCT s.ground.id FROM Slot s")
    List<Long> findUsedGroundIds();
    
    boolean existsByGroundIdAndStartTimeAndEndTime(Long groundId, LocalTime startTime, LocalTime endTime);
    
    @Query("SELECT s FROM Slot s JOIN FETCH s.ground")
    List<Slot> findAllWithGround();
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Slot s WHERE s.ground.id = :groundId")
    void deleteByGroundId(@Param("groundId") Long groundId);
    
    @Query("SELECT s FROM Slot s WHERE s.ground.id = :groundId AND s.availability = true")
    List<Slot> findByGroundIdAndAvailabilityTrue(@Param("groundId") Long groundId);
    

    @Query("SELECT s FROM Slot s WHERE s.ground.id IN :groundIds")
    List<Slot> findByGroundIds(@Param("groundIds") List<Long> groundIds);
    
    List<Slot> findByIsDeletedFalse();

    @Query("SELECT s FROM Slot s LEFT JOIN FETCH s.ground WHERE s.isDeleted = false")
    List<Slot> findAllByIsDeletedFalseWithGround();

    List<Slot> findByGroundIdAndIsDeletedFalse(Long groundId);
    
    boolean existsByGroundIdAndStartTimeAndEndTimeAndIsDeletedFalse(Long groundId, LocalTime startTime, LocalTime endTime);
}
