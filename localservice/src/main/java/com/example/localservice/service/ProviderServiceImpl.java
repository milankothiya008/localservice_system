package com.example.localservice.service;

import com.example.localservice.dto.ProviderResponseDto;
import com.example.localservice.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ServiceProviderRepository providerRepository;

    @Override
    public List<ProviderResponseDto> searchProviders(Long serviceId, String keyword) {
        return providerRepository.searchProviders(serviceId, keyword).stream()
                .map(provider -> ProviderResponseDto.builder()
                        .providerId(provider.getId())
                        .providerName(provider.getUser().getName())
                        .serviceName(provider.getServiceItem().getName())
                        .experience(provider.getExperience())
                        .build())
                .collect(Collectors.toList());
    }
}
