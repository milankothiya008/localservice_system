package com.example.localservice.service;

import com.example.localservice.dto.BookingRequestDto;
import com.example.localservice.dto.BookingResponseDto;
import com.example.localservice.dto.BookingStatusUpdateDto;
import com.example.localservice.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request);
    Page<BookingResponseDto> getBookingsByProvider(Long providerId, BookingStatus status, Pageable pageable);
    List<BookingResponseDto> getBookingsByUser(Long userId);
    BookingResponseDto updateBookingStatus(Long bookingId, BookingStatusUpdateDto updateDto);
    BookingResponseDto completeBooking(Long bookingId);
    BookingResponseDto cancelBooking(Long bookingId);
    
    com.example.localservice.dto.UserDashboardResponse getUserDashboard(Long userId);
    com.example.localservice.dto.ProviderDashboardResponse getProviderDashboard(Long providerId);
}
