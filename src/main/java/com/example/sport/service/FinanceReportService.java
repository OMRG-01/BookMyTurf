package com.example.sport.service;

import com.example.sport.dto.FinanceReportDTO;
import com.example.sport.repository.FinanceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceReportService {

    @Autowired
    private FinanceReportRepository financeReportRepository;

    public List<FinanceReportDTO> getFinanceReport(Long gameId, Long groundId, LocalDate startDate, LocalDate endDate) {
        return financeReportRepository.getFinanceReport(gameId, groundId, startDate, endDate);
    }
}
