package com.example.localservice.controller;

import com.example.localservice.dto.UserDashboardResponse;
import com.example.localservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final BookingService bookingService;

    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<UserDashboardResponse> getUserDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserDashboard(userId));
    }
}
