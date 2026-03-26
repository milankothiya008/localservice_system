package com.example.localservice.repository;

import com.example.localservice.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findByNameContainingIgnoreCase(String keyword);
}
