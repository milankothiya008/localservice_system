package com.example.localservice.controller;

import com.example.localservice.dto.ServiceDto;
import com.example.localservice.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceItemService serviceItemService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices(@org.springframework.web.bind.annotation.RequestParam(required = false) String name) {
        return ResponseEntity.ok(serviceItemService.getAllServices(name));
    }
}
