package com.example.localservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderDto {
    private Long id;
    private Long userId;
    private String name;
    private ServiceDto service;
    private Integer experience;
}
