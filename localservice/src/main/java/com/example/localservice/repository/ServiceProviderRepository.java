package com.example.localservice.repository;

import com.example.localservice.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findByServiceItemId(Long serviceId);
    Optional<ServiceProvider> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Query("SELECT sp FROM ServiceProvider sp JOIN sp.user u JOIN sp.serviceItem s WHERE " +
           "(:serviceId IS NULL OR s.id = :serviceId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ServiceProvider> searchProviders(@Param("serviceId") Long serviceId, @Param("keyword") String keyword);
}
