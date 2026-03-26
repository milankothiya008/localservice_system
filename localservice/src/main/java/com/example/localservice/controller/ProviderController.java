package com.example.localservice.controller;

import com.example.localservice.dto.ProviderResponseDto;
import com.example.localservice.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public ResponseEntity<List<ProviderResponseDto>> searchProviders(
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name) {
        String searchKey = keyword != null ? keyword : name;
        return ResponseEntity.ok(providerService.searchProviders(serviceId, searchKey));
    }
}
