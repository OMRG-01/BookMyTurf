package com.example.sport.repository;

import com.example.sport.dto.FinanceReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.sport.model.Booking;
import java.time.LocalDate;
import java.util.List;

public interface FinanceReportRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT new com.example.sport.dto.FinanceReportDTO( " +
           "b.bookingDate, SUM(b.price), " +
           "SUM(CASE WHEN b.paymentStatus = 1 THEN b.price ELSE 0 END), " +
           "SUM(CASE WHEN b.paymentStatus = 0 THEN b.price ELSE 0 END) ) " +
           "FROM Booking b " +
           "WHERE (:gameId IS NULL OR b.gameId = :gameId) " +
           "AND (:groundId IS NULL OR b.groundId = :groundId) " +
           "AND (b.bookingDate BETWEEN :startDate AND :endDate) " +
           "GROUP BY b.bookingDate")
    List<FinanceReportDTO> getFinanceReport(
        @Param("gameId") Long gameId, 
        @Param("groundId") Long groundId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate);
}
