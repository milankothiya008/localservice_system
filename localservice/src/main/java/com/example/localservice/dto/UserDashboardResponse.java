package com.example.localservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {
    private long totalBookings;
    private long pendingBookings;
    private long acceptedBookings;
    private long completedBookings;
    private long cancelledBookings;
    private long rejectedBookings;
}
