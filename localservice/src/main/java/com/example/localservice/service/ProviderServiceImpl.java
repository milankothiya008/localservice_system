package com.example.localservice.service;

import com.example.localservice.dto.ServiceDto;
import com.example.localservice.dto.ServiceProviderDto;
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
    public List<ServiceProviderDto> getProvidersByServiceId(Long serviceId) {
        return providerRepository.findByServiceItemId(serviceId).stream()
                .map(provider -> ServiceProviderDto.builder()
                        .id(provider.getId())
                        .userId(provider.getUser().getId())
                        .name(provider.getUser().getName())
                        .service(ServiceDto.builder()
                                .id(provider.getServiceItem().getId())
                                .name(provider.getServiceItem().getName())
                                .build())
                        .experience(provider.getExperience())
                        .build())
                .collect(Collectors.toList());
    }
}
