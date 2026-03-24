package com.example.localservice.dto;

import com.example.localservice.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdateDto {
    @NotNull(message = "Status is required")
    private BookingStatus status;
}
