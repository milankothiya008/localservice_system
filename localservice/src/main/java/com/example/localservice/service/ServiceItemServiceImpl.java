package com.example.localservice.service;

import com.example.localservice.dto.ServiceDto;
import com.example.localservice.repository.ServiceItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    @Override
    public List<ServiceDto> getAllServices() {
        return serviceItemRepository.findAll().stream()
                .map(s -> ServiceDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
