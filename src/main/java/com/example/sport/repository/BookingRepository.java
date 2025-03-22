package com.example.sport.repository;

import com.example.sport.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	boolean existsBySlotIdAndPaymentStatus(Long slotId, int paymentStatus);
}
