package com.example.localservice.service;

import com.example.localservice.dto.ServiceDto;
import java.util.List;

public interface ServiceItemService {
    List<ServiceDto> getAllServices(String keyword);
}
