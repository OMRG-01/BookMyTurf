package com.example.sport.controller;

import com.example.sport.dto.FinanceReportDTO;
import com.example.sport.model.Game;
import com.example.sport.model.Ground;
import com.example.sport.service.FinanceReportService;
import com.example.sport.service.GameService;
import com.example.sport.service.GroundService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FinanceReportController {

	 @Autowired
	    private FinanceReportService financeReportService;

	    @Autowired
	    private GameService gameService;

	    @Autowired
	    private GroundService groundService;

	    @GetMapping("/finance-report")
	    public String getFinanceReport(
	        @RequestParam(required = false) Long gameId,
	        @RequestParam(required = false) Long groundId,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
	        Model model) {

	        // Fetch finance report data
	        List<FinanceReportDTO> financeReport = financeReportService.getFinanceReport(gameId, groundId, startDate, endDate);

	        // Fetch games and grounds
	        List<Game> games = gameService.getAllGames();
	        List<Ground> grounds = groundService.getAllGrounds();

	        // Pass everything to the Thymeleaf template
	        model.addAttribute("financeReport", financeReport);
	        model.addAttribute("games", games);
	        model.addAttribute("grounds", grounds);

	        return "admin/finance"; // Renders finance_report.html
	    }
}