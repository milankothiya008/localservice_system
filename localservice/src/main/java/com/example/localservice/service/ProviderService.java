package com.example.localservice.service;

import com.example.localservice.dto.ProviderResponseDto;

import java.util.List;

public interface ProviderService {
    List<ProviderResponseDto> searchProviders(Long serviceId, String keyword);
}
