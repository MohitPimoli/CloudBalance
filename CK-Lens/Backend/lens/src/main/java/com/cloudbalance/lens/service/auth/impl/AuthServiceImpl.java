package com.cloudbalance.lens.service.auth.impl;

import com.cloudbalance.lens.config.JwtUtil;
import com.cloudbalance.lens.dto.auth.AuthRequestDTO;
import com.cloudbalance.lens.dto.auth.AuthResponseDTO;
import com.cloudbalance.lens.dto.auth.CustomUserDetails;
import com.cloudbalance.lens.dto.auth.UserDashboardPermission;
import com.cloudbalance.lens.entity.DashboardPermission;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.exception.CustomException;
import com.cloudbalance.lens.exception.CustomException.InvalidCredentialsException;
import com.cloudbalance.lens.exception.CustomException.TokenMissingException;
import com.cloudbalance.lens.exception.GenericApplicationException;
import com.cloudbalance.lens.exception.KeyLoadingException;
import com.cloudbalance.lens.repository.DashboardPermissionRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.auth.AuthService;
import com.cloudbalance.lens.utils.Constant;
import com.cloudbalance.lens.utils.PasswordDecryptorUtil;
import com.cloudbalance.lens.utils.TokenBlacklistUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsServiceImpl customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DashboardPermissionRepository dashboardPermissionRepository;
    private final TokenBlacklistUtil tokenBlacklistUtil;
    private final PasswordDecryptorUtil passwordDecryptorUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           CustomUserDetailsServiceImpl customUserDetailsService,
                           JwtUtil jwtUtil,
                           DashboardPermissionRepository dashboardPermissionRepository,
                           TokenBlacklistUtil tokenBlacklistUtil,
                           PasswordDecryptorUtil passwordDecryptorUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.dashboardPermissionRepository = dashboardPermissionRepository;
        this.tokenBlacklistUtil = tokenBlacklistUtil;
        this.passwordDecryptorUtil = passwordDecryptorUtil;
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String PUBLIC_KEY_PATH = "/keys/public_key.pem";
    private String cachedPublicKey;

    @Override
    public AuthResponseDTO login(AuthRequestDTO loginDTO) {
        String decryptedPassword = passwordDecryptorUtil.decryptPassword(loginDTO.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), decryptedPassword)
            );
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials for user '{}'", loginDTO.getUsername());
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", loginDTO.getUsername());
                    return new CustomException.UserNotFoundException("User not found for username: "+loginDTO.getUsername());
                });
        user.setLastAccessedTime(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER));
        userRepository.save(user);
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService
                .loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(customUserDetails);
        String refreshToken = jwtUtil.generateRefreshToken(customUserDetails);
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
                .refreshToken(refreshToken)
                .message("Login successful")
                .dashboardPermissions(userDashboardPermissions)
                .build();
    }

    @Override
    public String logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Logout attempted without valid access token");
                throw new TokenMissingException();
            }

            String accessToken = authHeader.substring(7).trim();
            String refreshToken = null;

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Constant.REFRESH_TOKEN)) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null) {
                log.warn("Logout attempted without valid refresh token in cookies");
                throw new TokenMissingException();
            }

            tokenBlacklistUtil.blacklistToken(accessToken);
            tokenBlacklistUtil.blacklistToken(refreshToken);

            SecurityContextHolder.clearContext();
            log.info("User logged out successfully. Tokens blacklisted.");

            return "Logout Successfully...";
        } catch (TokenMissingException e) {
            log.error("Token missing during logout", e);
            return "Token missing during logout";
        } catch (Exception e) {
            log.error("Error during logout", e);
            return "Error during logout";
        }
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
            throw new GenericApplicationException("Unexpected error loading public key", e.getCause());
        }
    }

    @Override
    public Map<String, String> refreshToken(HttpServletRequest request) {
        String oldRefreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (Constant.REFRESH_TOKEN.equals(cookie.getName())) {
                    oldRefreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (oldRefreshToken == null) {
            throw new GenericApplicationException("Missing refresh token");
        }

        try {
            String username = jwtUtil.extractUsername(oldRefreshToken);
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenExpired(oldRefreshToken)) {
                tokenBlacklistUtil.blacklistToken(oldRefreshToken);
                throw new ExpiredJwtException(null, null, "Refresh token expired");
            }

            tokenBlacklistUtil.blacklistToken(oldRefreshToken);

            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            return Map.of(
                    Constant.ACCESS_TOKEN, newAccessToken,
                    Constant.REFRESH_TOKEN, newRefreshToken
            );

        } catch (ExpiredJwtException e) {
            tokenBlacklistUtil.blacklistToken(oldRefreshToken);
            throw e;
        } catch (Exception e) {
            throw new GenericApplicationException("Invalid refresh token");
        }
    }

}

