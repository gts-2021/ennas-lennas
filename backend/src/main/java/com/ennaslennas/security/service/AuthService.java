package com.ennaslennas.security.service;

import com.ennaslennas.common.exception.BusinessException;
import com.ennaslennas.security.domain.AdminUser;
import com.ennaslennas.security.dto.LoginRequest;
import com.ennaslennas.security.dto.LoginResponse;
import com.ennaslennas.security.jwt.JwtTokenProvider;
import com.ennaslennas.security.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AdminUserRepository adminUserRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        AdminUser admin = adminUserRepository.findByEmailAndActiveTrue(request.getEmail().trim())
                .orElseThrow(() -> new BusinessException("Compte administrateur introuvable ou désactivé."));

        admin.setLastLoginAt(Instant.now());
        adminUserRepository.save(admin);

        log.info("Connexion réussie pour l'administrateur: {}", admin.getEmail());

        return LoginResponse.builder()
                .token(jwt)
                .id(admin.getId())
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .build();
    }
}
