package com.example.localservice.dto;

import com.example.localservice.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long providerId;
    private String providerName;
    private Long serviceId;
    private String serviceName;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDate serviceDate;
}
