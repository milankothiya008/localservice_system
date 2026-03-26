package com.example.localservice.repository;

import com.example.localservice.entity.Booking;
import com.example.localservice.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByProviderId(Long providerId);
    Page<Booking> findByProviderId(Long providerId, Pageable pageable);
    Page<Booking> findByProviderIdAndStatus(Long providerId, BookingStatus status, Pageable pageable);
    List<Booking> findByUserId(Long userId);
    boolean existsByUserIdAndProviderIdAndServiceDate(Long userId, Long providerId, LocalDate serviceDate);
    boolean existsByProviderIdAndServiceDateAndStatus(Long providerId, LocalDate serviceDate, BookingStatus status);
    
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, BookingStatus status);
    long countByProviderId(Long providerId);
    long countByProviderIdAndStatus(Long providerId, BookingStatus status);
}
