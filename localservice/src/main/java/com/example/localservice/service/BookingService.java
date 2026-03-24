package com.example.localservice.service;

import com.example.localservice.dto.BookingRequestDto;
import com.example.localservice.dto.BookingResponseDto;
import com.example.localservice.dto.BookingStatusUpdateDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto request);
    List<BookingResponseDto> getBookingsByProvider(Long providerId);
    List<BookingResponseDto> getBookingsByUser(Long userId);
    BookingResponseDto updateBookingStatus(Long bookingId, BookingStatusUpdateDto updateDto);
}
