package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.auth.AuthRequestDTO;
import com.cloudbalance.lens.dto.auth.AuthResponseDTO;
import com.cloudbalance.lens.service.auth.AuthService;
import com.cloudbalance.lens.utils.Constant;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO loginDTO, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.login(loginDTO);
        ResponseCookie refreshTokenCookie = ResponseCookie.from(Constant.REFRESH_TOKEN, authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(3 * 24 * 60 * 60L) // 3 days
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        authResponse.setRefreshToken(null);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * User logout
     */

    @PostMapping("/logout")
    public ResponseEntity<GlobalMessageDTO> logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(authService.logout(request));
    }

    /**
     * Fetch public key
     */

    @GetMapping("/public-key")
    public ResponseEntity<String> publicKey() {
        return ResponseEntity.ok(authService.publicKey());
    }

    /**
     * Refresh token
     */

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, String> tokens = authService.refreshToken(request);
            String refreshToken = tokens.get(Constant.REFRESH_TOKEN);
            String accessToken = tokens.get(Constant.ACCESS_TOKEN);
            ResponseCookie refreshTokenCookie = ResponseCookie.from(Constant.REFRESH_TOKEN, refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(3 * 24 * 60 * 60L) // 3 days
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return ResponseEntity.ok(Map.of(Constant.ACCESS_TOKEN,accessToken));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }


}
