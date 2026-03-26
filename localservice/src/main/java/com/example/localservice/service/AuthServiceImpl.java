package com.example.localservice.service;

import com.example.localservice.dto.*;
import com.example.localservice.entity.Role;
import com.example.localservice.entity.ServiceItem;
import com.example.localservice.entity.ServiceProvider;
import com.example.localservice.entity.User;
import com.example.localservice.exception.BadRequestException;
import com.example.localservice.exception.ResourceNotFoundException;
import com.example.localservice.repository.ServiceItemRepository;
import com.example.localservice.repository.ServiceProviderRepository;
import com.example.localservice.repository.UserRepository;
import com.example.localservice.security.CustomUserDetails;
import com.example.localservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponseDto register(RegistrationDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        user = userRepository.save(user);

        ServiceProvider provider = null;
        if (request.getRole() == Role.PROVIDER) {
            if (request.getServiceId() == null || request.getExperience() == null) {
                throw new BadRequestException("Service ID and Experience are required for PROVIDER role");
            }
            ServiceItem serviceItem = serviceItemRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + request.getServiceId()));

            provider = ServiceProvider.builder()
                    .user(user)
                    .serviceItem(serviceItem)
                    .experience(request.getExperience())
                    .build();
            provider = providerRepository.save(provider);
        }

        return AuthResponseDto.builder()
                .message("Registration successful")
                .user(mapToUserDto(user))
                .provider(provider != null ? mapToProviderDto(provider) : null)
                .build();
    }

    @Override
    public AuthResponseDto login(LoginDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtil.generateToken(userDetails);
        User user = userDetails.getUser();

        ServiceProvider provider = null;
        if (user.getRole() == Role.PROVIDER) {
            provider = providerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider details not found for user"));
        }

        return AuthResponseDto.builder()
                .message("Login successful")
                .token(jwtToken)
                .user(mapToUserDto(user))
                .provider(provider != null ? mapToProviderDto(provider) : null)
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private ServiceProviderDto mapToProviderDto(ServiceProvider provider) {
        return ServiceProviderDto.builder()
                .id(provider.getId())
                .userId(provider.getUser().getId())
                .name(provider.getUser().getName())
                .service(ServiceDto.builder()
                        .id(provider.getServiceItem().getId())
                        .name(provider.getServiceItem().getName())
                        .build())
                .experience(provider.getExperience())
                .build();
    }
}
