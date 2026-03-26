package com.example.localservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponseDto {
    private Long providerId;
    private String providerName;
    private String serviceName;
    private Integer experience;
}
