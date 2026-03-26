package com.example.localservice.dto;

import com.example.localservice.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long bookingId;
    private String serviceName;
    private String providerName;
    private BookingStatus status;
    private LocalDate serviceDate;
}
