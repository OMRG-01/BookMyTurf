package com.example.sport.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sport.dto.BookingAnalysisDTO;
import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.model.Slot;
import com.example.sport.service.BookingService;
import com.example.sport.service.GameService;
import com.example.sport.service.GroundService;
import com.example.sport.service.SlotService;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {


    @Autowired
    private GameService gameService;
    
    @Autowired
    private SlotService slotService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private GroundService groundService;

    @GetMapping("/ground")
    public List<Object> getGrounds() {
        return groundService.getAllGrounds().stream()
                .map(ground -> {
                    return new Object() {
                        public Long id = ground.getId();
                        public String name = ground.getName();
                    };
                })
                .collect(Collectors.toList());
    }
    
    @GetMapping("/booking-analysis")
    public List<BookingAnalysisDTO> getBookingAnalysis() {
        List<Game> games = gameService.getAllGames();
        List<BookingAnalysisDTO> bookingAnalysis = new ArrayList<>();

        for (Game game : games) {
            Long gameId = game.getId();
            String gameName = game.getGameName();

            // 1️⃣ Total Bookings for this Game
            int totalBookings = bookingService.countBookingsByGameId(gameId);

            // 2️⃣ Total Revenue for this Game (sum of price)
            double totalRevenue = bookingService.getTotalRevenueByGameId(gameId);

            // 3️⃣ Average Price for Slots in Grounds that Host this Game

            List<Ground> gameGrounds = groundService.getGroundsByGameId(gameId);
            // Fetch all slots for these grounds
            List<Slot> slots = slotService.getSlotsByGroundIds(
                gameGrounds.stream().map(Ground::getId).collect(Collectors.toList())
            );

            // Calculate the average price
            double avgPrice = slots.stream()
                .mapToDouble(Slot::getPrice)
                .average()
                .orElse(0.0);

         // Convert Long to String explicitly
            String popularGround = String.valueOf(bookingService.getMostPopularGroundByGameId(gameId));


            // Add data to DTO
            bookingAnalysis.add(new BookingAnalysisDTO(gameName, totalBookings, totalRevenue, avgPrice, popularGround));
        }

        return bookingAnalysis; // API returns JSON data
    }
}
