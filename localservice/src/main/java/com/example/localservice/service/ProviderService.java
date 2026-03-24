package com.example.localservice.service;

import com.example.localservice.dto.ServiceProviderDto;

import java.util.List;

public interface ProviderService {
    List<ServiceProviderDto> getProvidersByServiceId(Long serviceId);
}
