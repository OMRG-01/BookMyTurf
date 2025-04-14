package com.example.sport.controller;

import com.example.sport.dto.SlotDTO;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.repository.GroundRepository;
import com.example.sport.repository.SlotRepository;
import com.example.sport.service.GroundService;
import com.example.sport.service.SlotService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/slots")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @Autowired
    private GroundService groundService;
    
    @Autowired
    private GroundRepository groundRepository;
    
    @Autowired
    private SlotRepository slotRepository;

    // ✅ Load Slot Management Page
    @GetMapping
    public String showSlotPage(Model model) {
        List<Ground> grounds = groundService.getAllGrounds(); // Fetch grounds for dropdown
        model.addAttribute("grounds", grounds);
        return "admin/slot"; // Redirect to slot.html in templates/admin/
    }
    
    @GetMapping("/grounds")
    @ResponseBody
    public List<Ground> getAllGrounds() {
        return groundService.getAllGrounds();
    }


    // ✅ Fetch all slots for a selected ground (JSON Response)
//    @GetMapping("/view/{groundId}")
//    @ResponseBody
//    public List<Slot> getSlotsByGround(@PathVariable Long groundId) {
//        return slotService.getSlotsByGround(groundId);
//    }
    
   
//    @PostMapping("/submit")
//    public ResponseEntity<?> createSlot(@RequestBody SlotDTO slotDTO) {
//        if (slotDTO.getGroundId() == null) {
//            return ResponseEntity.badRequest().body("Ground ID is required.");
//        }
//
//        Ground ground = groundRepository.findById(slotDTO.getGroundId())
//                .orElseThrow(() -> new RuntimeException("Ground not found"));
//
//        // Parse LocalTime from String
//        LocalTime startTime = LocalTime.parse(slotDTO.getStartTime());
//        LocalTime endTime = LocalTime.parse(slotDTO.getEndTime());
//
//        // Check if a slot already exists with the same ground, startTime, and endTime
//        boolean exists = slotRepository.existsByGroundIdAndStartTimeAndEndTime(
//                slotDTO.getGroundId(), startTime, endTime);
//
//        if (exists) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Slot from " + startTime + " to " + endTime + " already exists for this ground.");
//        }
//
//        // Create Slot object
//        Slot slot = new Slot();
//        slot.setGround(ground);
//        slot.setAvailability(slotDTO.isAvailability());
//        slot.setBreakTime(slotDTO.getBreakTime());
//        slot.setStartTime(startTime);
//        slot.setEndTime(endTime);
//        slot.setPrice(slotDTO.getPrice());
//        slot.setWeekendPrice(slotDTO.getWeekendPrice());
//
//        // Log the slot object before saving
//        System.out.println("Saving slot: " + slot.toString());
//
//        // Save the slot
//        slotRepository.save(slot);
//
//        return ResponseEntity.ok("Slot added successfully.");
//    }
    @PostMapping("/submit")
    public ResponseEntity<?> createSlot(@RequestBody SlotDTO slotDTO) {
        if (slotDTO.getGroundId() == null) {
            return ResponseEntity.badRequest().body("Ground ID is required.");
        }

        Ground ground = groundRepository.findById(slotDTO.getGroundId())
                .orElseThrow(() -> new RuntimeException("Ground not found"));

        // Parse LocalTime from String
        LocalTime startTime = LocalTime.parse(slotDTO.getStartTime());
        LocalTime endTime = LocalTime.parse(slotDTO.getEndTime());

        // Check if a non-soft-deleted slot already exists with the same ground, startTime, and endTime
        boolean exists = slotRepository.existsByGroundIdAndStartTimeAndEndTimeAndIsDeletedFalse(
                slotDTO.getGroundId(), startTime, endTime);

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Slot from " + startTime + " to " + endTime + " already exists for this ground.");
        }

        // Create Slot object
        Slot slot = new Slot();
        slot.setGround(ground);
        slot.setAvailability(slotDTO.isAvailability());
        slot.setBreakTime(slotDTO.getBreakTime());
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setPrice(slotDTO.getPrice());
        slot.setWeekendPrice(slotDTO.getWeekendPrice());

        // Log the slot object before saving
        System.out.println("Saving slot: " + slot.toString());

        // Save the slot
        slotRepository.save(slot);

        return ResponseEntity.ok("Slot added successfully.");
    }




    public SlotController(GroundService groundService) {
        this.groundService = groundService;
    }

    @GetMapping("/used-grounds")
    public List<Long> getUsedGrounds() {
        return groundService.getUsedGroundIds();
    }
    

    @GetMapping("/all")
    @ResponseBody
    public List<Slot> getAllSlots() {
        // Fetch only non-deleted slots using a custom repository method
        return slotRepository.findAllByIsDeletedFalseWithGround();
    }

    
 // Page Navigation to viewSlot.html
    @GetMapping("/viewSlot")
    public String showViewSlotPage(Model model) {
        List<Slot> slots = slotRepository.findByIsDeletedFalse(); // fetch only active slots
        model.addAttribute("slots", slots);
        return "admin/viewSlot";
    }

 // ✅ Fetch slots for a specific ground and load edit page
    @GetMapping("/editSlot")
    public String editSlotPage(@RequestParam("groundId") Long groundId, Model model) {
        System.out.println("Inside editSlotPage - Received groundId: " + groundId);

        // Fetch ground details
        Ground ground = groundService.getGroundById(groundId);
        if (ground == null) {
            System.out.println("Ground not found for ID: " + groundId);
            model.addAttribute("error", "Ground not found.");
            return "redirect:/slotManagement";
        }

        // Fetch all non-deleted slots for the selected ground
        List<Slot> slots = slotService.getSlotsByGroundIdAndIsDeletedFalse(groundId);
        System.out.println("Slots fetched: " + slots);

        // Add attributes to model for Thymeleaf template
        model.addAttribute("ground", ground);
        model.addAttribute("slots", slots);

        return "admin/editSlot"; // Loads editSlot.html
    }



    // ✅ Fetch a single slot by ID (For Editing)
    @GetMapping("/{slotId}")
    @ResponseBody
    public ResponseEntity<?> getSlotById(@PathVariable Long slotId) {
        Optional<Slot> slot = slotRepository.findById(slotId);
        return slot.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/grounds/{groundId}")
    public ResponseEntity<Ground> getGroundById(@PathVariable Long groundId) {
        Ground ground = groundService.getGroundById(groundId);
        if (ground == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ground);
    }

    @GetMapping("/ground/{groundId}")
    public ResponseEntity<?> getSlotsByGround(@PathVariable Long groundId) {
        List<Slot> slots = slotService.getSlotsByGroundId(groundId);

        if (slots.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No slots found for this ground");
        }

        Map<String, Object> response = new HashMap<>();
        Slot firstSlot = slots.get(0); // Assuming all slots have the same breakTime, slotHours, etc.

        response.put("openingTime", firstSlot.getStartTime().toString());
        response.put("closingTime", firstSlot.getEndTime().toString());
        response.put("breakTime", firstSlot.getBreakTime());
        response.put("basePrice", firstSlot.getPrice());
        response.put("weekendExtra", firstSlot.getWeekendPrice());
        response.put("slots", slots.stream().map(slot -> Map.of(
            "id", slot.getId(),
            "startTime", slot.getStartTime().toString(),
            "endTime", slot.getEndTime().toString(),
            "price", slot.getPrice(),
            "weekendPrice", slot.getWeekendPrice(),
            "availability", slot.isAvailability()
        )).toList());

        return ResponseEntity.ok(response);
    }

    
    @PutMapping("/update/{groundId}")
    public ResponseEntity<?> updateSlotsByGround(
            @PathVariable Long groundId,
            @RequestBody SlotDTO slotDTO) {
        
        List<Slot> slots = slotRepository.findByGroundId(groundId);  // Fetch slots by groundId

        if (slots.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        for (Slot slot : slots) {
            slot.setBreakTime(slotDTO.getBreakTime());
            slot.setPrice(slotDTO.getPrice());
            slot.setWeekendPrice(slotDTO.getWeekendPrice());
        }

        slotRepository.saveAll(slots);  // Save all updated slots
        return ResponseEntity.ok(Collections.singletonMap("message", "Update successful"));
    }


//    // ✅ Delete Slot
//    @DeleteMapping("/delete/{slotId}")
//    public ResponseEntity<?> deleteSlot(@PathVariable Long slotId) {
//        if (slotRepository.existsById(slotId)) {
//            slotRepository.deleteById(slotId);
//            return ResponseEntity.ok("Slot deleted successfully.");
//        }
//        return ResponseEntity.notFound().build();
//    }
    @DeleteMapping("/delete/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long slotId) {
        Optional<Slot> optionalSlot = slotRepository.findById(slotId);
        if (optionalSlot.isPresent()) {
            Slot slot = optionalSlot.get();
            slot.setDeleted(true); // Soft delete
            slotRepository.save(slot);
            return ResponseEntity.ok("Slot marked as deleted.");
        }
        return ResponseEntity.notFound().build();
    }


    
//    @DeleteMapping("/slotdelete/{groundId}")
//    @Transactional
//    public ResponseEntity<String> deleteSlotsByGroundId(@PathVariable Long groundId) {
//        List<Slot> slots = slotRepository.findByGroundId(groundId);
//
//        // 🔍 Debug Log: Check if Slots Exist
//        System.out.println("🔍 Checking slots for ground ID: " + groundId);
//        System.out.println("Found Slots: " + slots.size());
//
//        if (slots.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No slots found for this ground.");
//        }
//
//        slotRepository.deleteByGroundId(groundId);
//        return ResponseEntity.ok("All slots for Ground ID " + groundId + " have been deleted.");
//    }
    @DeleteMapping("/slotdelete/{groundId}")
    @Transactional
    public ResponseEntity<String> deleteSlotsByGroundId(@PathVariable Long groundId) {
        List<Slot> slots = slotRepository.findByGroundId(groundId);

        // 🔍 Debug Log
        System.out.println("🔍 Checking slots for ground ID: " + groundId);
        System.out.println("Found Slots: " + slots.size());

        if (slots.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No slots found for this ground.");
        }

        // ✅ Soft delete all slots
        for (Slot slot : slots) {
            slot.setDeleted(true);  // mark as soft-deleted
        }
        slotRepository.saveAll(slots); // batch save

        return ResponseEntity.ok("All slots for Ground ID " + groundId + " have been marked as deleted.");
    }





}
