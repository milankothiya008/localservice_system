package com.example.localservice.service;

import com.example.localservice.dto.BookingRequestDto;
import com.example.localservice.dto.BookingResponseDto;
import com.example.localservice.dto.BookingStatusUpdateDto;
import com.example.localservice.entity.Booking;
import com.example.localservice.entity.BookingStatus;
import com.example.localservice.entity.ServiceItem;
import com.example.localservice.entity.ServiceProvider;
import com.example.localservice.entity.User;
import com.example.localservice.exception.BadRequestException;
import com.example.localservice.exception.ResourceNotFoundException;
import com.example.localservice.repository.BookingRepository;
import com.example.localservice.repository.ServiceItemRepository;
import com.example.localservice.repository.ServiceProviderRepository;
import com.example.localservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final ServiceItemRepository serviceItemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceProvider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        ServiceItem serviceItem = serviceItemRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (!provider.getServiceItem().getId().equals(request.getServiceId())) {
            throw new BadRequestException("This provider does not offer the requested service");
        }

        if (bookingRepository.existsByUserIdAndProviderIdAndServiceDate(
                user.getId(), provider.getId(), request.getServiceDate())) {
            throw new BadRequestException("Duplicate booking exists for this user, provider, and date");
        }

        Booking booking = Booking.builder()
                .user(user)
                .provider(provider)
                .serviceItem(serviceItem)
                .status(BookingStatus.PENDING)
                .serviceDate(request.getServiceDate())
                .build();

        booking = bookingRepository.save(booking);

        return mapToDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByProvider(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Provider not found");
        }
        return bookingRepository.findByProviderId(providerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, BookingStatusUpdateDto updateDto) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(updateDto.getStatus());
        booking = bookingRepository.save(booking);

        return mapToDto(booking);
    }

    private BookingResponseDto mapToDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .providerId(booking.getProvider().getId())
                .providerName(booking.getProvider().getUser().getName())
                .serviceId(booking.getServiceItem().getId())
                .serviceName(booking.getServiceItem().getName())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .serviceDate(booking.getServiceDate())
                .build();
    }
}
