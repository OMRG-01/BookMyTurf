package com.example.sport.controller;

import com.example.sport.dto.SlotDTO;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.repository.GroundRepository;
import com.example.sport.repository.SlotRepository;
import com.example.sport.service.GroundService;
import com.example.sport.service.SlotService;
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
import java.util.List;

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
    @GetMapping("/view/{groundId}")
    @ResponseBody
    public List<Slot> getSlotsByGround(@PathVariable Long groundId) {
        return slotService.getSlotsByGround(groundId);
    }
    
   
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

        // Check if a slot already exists with the same ground, startTime, and endTime
        boolean exists = slotRepository.existsByGroundIdAndStartTimeAndEndTime(
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
    





    // ✅ Delete a slot by ID (JSON Response)
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return "Slot deleted successfully!";
    }
}
