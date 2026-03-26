package com.example.localservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDashboardResponse {
    private long totalRequests;
    private long pendingRequests;
    private long acceptedRequests;
    private long completedRequests;
    private long cancelledRequests;
    private long rejectedRequests;
}
