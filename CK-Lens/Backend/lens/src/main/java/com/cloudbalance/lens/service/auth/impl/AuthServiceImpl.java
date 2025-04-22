package com.cloudbalance.lens.service.auth.impl;

import com.cloudbalance.lens.config.JwtUtil;
import com.cloudbalance.lens.dto.auth.*;
import com.cloudbalance.lens.entity.BlackListedToken;
import com.cloudbalance.lens.entity.DashboardPermission;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.exception.CustomException.InvalidCredentialsException;
import com.cloudbalance.lens.exception.CustomException.TokenMissingException;
import com.cloudbalance.lens.exception.CustomException.UserNotFoundException;
import com.cloudbalance.lens.exception.KeyLoadingException;
import com.cloudbalance.lens.repository.BlackListedTokenRepository;
import com.cloudbalance.lens.repository.DashboardPermissionRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.auth.AuthService;
import com.cloudbalance.lens.utils.PasswordDecryptorUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsServiceImpl customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BlackListedTokenRepository blackListedTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DashboardPermissionRepository dashboardPermissionRepository;
    @Autowired
    private PasswordDecryptorUtil passwordDecryptorUtil;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String PUBLIC_KEY_PATH = "/keys/public_key.pem";
    private String cachedPublicKey;

    @Override
    public AuthResponseDTO login(AuthRequestDTO LoginDTO) {
        String decryptedPassword = passwordDecryptorUtil.decryptPassword(LoginDTO.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(LoginDTO.getUsername(), decryptedPassword)
            );
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials for user '{}'", LoginDTO.getUsername());
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findByUsername(LoginDTO.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", LoginDTO.getUsername());
                    return new UserNotFoundException(LoginDTO.getUsername());
                });
        user.setLastAccessedTime(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER));
        userRepository.save(user);
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(customUserDetails);
        List<DashboardPermission> permissions = dashboardPermissionRepository.findByRoleName(user.getRole().getName());
        List<UserDashboardPermission> userDashboardPermissions = permissions.stream()
                .map(p -> UserDashboardPermission.builder()
                        .dashboard(p.getDashboard())
                        .permissionType(p.getPermissionType())
                        .build())
                .collect(Collectors.toList());
        log.info("User '{}' logged in successfully", user.getUsername());

        return AuthResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .token(token)
                .message("Login successful")
                .dashboardPermissions(userDashboardPermissions)
                .build();
    }


    @Override
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Logout attempted without valid token");
            throw new TokenMissingException();
        }
        String token = authHeader.substring(7).trim();
        LocalDateTime expiry = jwtUtil.extractExpiration(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        BlackListedToken blackListedToken = BlackListedToken.builder()
                .token(token)
                .tokenExpiry(expiry)
                .blacklistedAt((LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER)))
                .build();
        blackListedTokenRepository.save(blackListedToken);
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully. Token blacklisted.");
        return "Logout Successfully...";
    }

    @Override
    public String publicKey() {

        if (cachedPublicKey != null) {
            return cachedPublicKey;
        }

        log.info("Fetching RSA public key from path: {}", PUBLIC_KEY_PATH);

        try (InputStream is = getClass().getResourceAsStream(PUBLIC_KEY_PATH)) {
            if (is == null) {
                log.error("Public key file not found at {}", PUBLIC_KEY_PATH);
                throw new KeyLoadingException("Public key file not found in " + PUBLIC_KEY_PATH);
            }

            cachedPublicKey = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return cachedPublicKey;

        } catch (IOException e) {
            log.error("IO error while reading the public key file", e);
            throw new KeyLoadingException("Failed to read public key", e);

        } catch (Exception e) {
            log.error("Unexpected error while loading public key", e);
            throw new RuntimeException("Unexpected error loading public key", e);
        }
    }
}

