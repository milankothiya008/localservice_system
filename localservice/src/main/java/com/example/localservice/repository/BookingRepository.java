package com.example.localservice.repository;

import com.example.localservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByProviderId(Long providerId);
    List<Booking> findByUserId(Long userId);
    boolean existsByUserIdAndProviderIdAndServiceDate(Long userId, Long providerId, LocalDate serviceDate);
}
