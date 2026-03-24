package com.example.localservice.repository;

import com.example.localservice.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findByServiceItemId(Long serviceId);
    Optional<ServiceProvider> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
