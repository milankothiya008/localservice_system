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
            throw new com.example.localservice.exception.DuplicateBookingException(
                    "Duplicate booking exists for this user, provider, and date");
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
    public org.springframework.data.domain.Page<BookingResponseDto> getBookingsByProvider(Long providerId,
            com.example.localservice.entity.BookingStatus status, org.springframework.data.domain.Pageable pageable) {
        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Provider not found");
        }
        org.springframework.data.domain.Page<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByProviderIdAndStatus(providerId, status, pageable);
        } else {
            bookings = bookingRepository.findByProviderId(providerId, pageable);
        }
        return bookings.map(this::mapToDto);
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

        BookingStatus current = booking.getStatus();
        BookingStatus target = updateDto.getStatus();

        if (current == BookingStatus.PENDING) {
            if (target != BookingStatus.ACCEPTED && target != BookingStatus.REJECTED
                    && target != BookingStatus.CANCELLED) {
                throw new com.example.localservice.exception.InvalidBookingStateException(
                        "Invalid transition from PENDING to " + target);
            }
        } else if (current == BookingStatus.ACCEPTED) {
            if (target != BookingStatus.COMPLETED) {
                throw new com.example.localservice.exception.InvalidBookingStateException(
                        "Invalid transition from ACCEPTED to " + target);
            }
        } else {
            throw new com.example.localservice.exception.InvalidBookingStateException(
                    "Cannot change status from " + current);
        }

        if (target == BookingStatus.ACCEPTED) {
            if (bookingRepository.existsByProviderIdAndServiceDateAndStatus(
                    booking.getProvider().getId(), booking.getServiceDate(), BookingStatus.ACCEPTED)) {
                throw new com.example.localservice.exception.ProviderNotAvailableException(
                        "Provider already has an ACCEPTED booking on this date");
            }
        }

        booking.setStatus(target);
        booking = bookingRepository.save(booking);

        return mapToDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto completeBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new com.example.localservice.exception.InvalidBookingStateException(
                    "Only ACCEPTED bookings can be marked as COMPLETED");
        }
        booking.setStatus(BookingStatus.COMPLETED);
        return mapToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new com.example.localservice.exception.InvalidBookingStateException("Only PENDING bookings can be CANCELLED");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return mapToDto(bookingRepository.save(booking));
    }

    @Override
    public com.example.localservice.dto.UserDashboardResponse getUserDashboard(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return com.example.localservice.dto.UserDashboardResponse.builder()
                .totalBookings(bookingRepository.countByUserId(userId))
                .pendingBookings(bookingRepository.countByUserIdAndStatus(userId, BookingStatus.PENDING))
                .acceptedBookings(bookingRepository.countByUserIdAndStatus(userId, BookingStatus.ACCEPTED))
                .completedBookings(bookingRepository.countByUserIdAndStatus(userId, BookingStatus.COMPLETED))
                .cancelledBookings(bookingRepository.countByUserIdAndStatus(userId, BookingStatus.CANCELLED))
                .rejectedBookings(bookingRepository.countByUserIdAndStatus(userId, BookingStatus.REJECTED))
                .build();
    }

    @Override
    public com.example.localservice.dto.ProviderDashboardResponse getProviderDashboard(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Provider not found");
        }
        return com.example.localservice.dto.ProviderDashboardResponse.builder()
                .totalRequests(bookingRepository.countByProviderId(providerId))
                .pendingRequests(bookingRepository.countByProviderIdAndStatus(providerId, BookingStatus.PENDING))
                .acceptedRequests(bookingRepository.countByProviderIdAndStatus(providerId, BookingStatus.ACCEPTED))
                .completedRequests(bookingRepository.countByProviderIdAndStatus(providerId, BookingStatus.COMPLETED))
                .cancelledRequests(bookingRepository.countByProviderIdAndStatus(providerId, BookingStatus.CANCELLED))
                .rejectedRequests(bookingRepository.countByProviderIdAndStatus(providerId, BookingStatus.REJECTED))
                .build();
    }

    private BookingResponseDto mapToDto(Booking booking) {
        return BookingResponseDto.builder()
                .bookingId(booking.getId())
                .serviceName(booking.getServiceItem().getName())
                .providerName(booking.getProvider().getUser().getName())
                .status(booking.getStatus())
                .serviceDate(booking.getServiceDate())
                .build();
    }
}
