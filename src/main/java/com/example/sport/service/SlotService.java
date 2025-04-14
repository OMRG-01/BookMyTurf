package com.example.sport.service;

import com.example.sport.model.Slot;
import com.example.sport.repository.SlotRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    // ✅ Save or Update a Slot
    public Slot saveSlot(Slot slot) {
        return slotRepository.save(slot);
    }
    @Transactional
    public List<Slot> saveAllSlots(List<Slot> slots) {
        List<Slot> savedSlots = slotRepository.saveAll(slots);
        return savedSlots;
    }
    
    public List<Slot> getSlotsByGroundId(Long groundId) {
        return slotRepository.findByGroundIdAndIsDeletedFalse(groundId);
    }

    
    public Slot findById(Long id) {
        return slotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Slot not found with ID: " + id));
    }
    // ✅ Fetch all slots
    public List<Slot> getAllSlots() {
        return slotRepository.findAll();
    }

    // ✅ Fetch slots by Ground ID
    public List<Slot> getSlotsByGround(Long groundId) {
        return slotRepository.findByGroundId(groundId);
    }

    public List<Slot> getSlotsByGroundIds(List<Long> groundIds) {
        return slotRepository.findByGroundIds(groundIds);
    }
    
    public List<Slot> getSlotsByGroundIdAndIsDeletedFalse(Long groundId) {
        return slotRepository.findByGroundIdAndIsDeletedFalse(groundId);
    }
    
    
    // ✅ Delete a Slot
    public void deleteSlot(Long id) {
        slotRepository.deleteById(id);
    }
    
    public void deleteSlotByGroundId(Long groundId) {
        slotRepository.deleteByGroundId(groundId);
    }
    
    public List<Slot> getAvailableSlotsForGround(Long groundId) {
        return slotRepository.findByGroundIdAndAvailabilityTrue(groundId);
    }

}
