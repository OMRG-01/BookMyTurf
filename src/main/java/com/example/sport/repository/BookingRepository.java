package com.example.sport.repository;

import com.example.sport.model.Booking;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	boolean existsBySlotIdAndPaymentStatus(Long slotId, int paymentStatus);
	
	 boolean existsBySlotIdAndBookingDate(Long slotId, LocalDate bookingDate);
	 
	 @Query("SELECT b.slotId FROM Booking b WHERE b.bookingDate = :bookingDate")
	 List<Long> findBookedSlotIdsByDate(@Param("bookingDate") LocalDate bookingDate);

	 @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.bookingDate < :currentDate")
	    List<Booking> findPastBookings(Long userId, LocalDate currentDate);  // 🔥 Should accept userId & LocalDate

	    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.bookingDate >= :currentDate")
	    List<Booking> findUpcomingBookings(Long userId, LocalDate currentDate);  // 🔥 Should accept userId & LocalDate

	    List<Booking> findByUserId(Long userId);
}