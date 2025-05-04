package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.auth.AuthRequestDTO;
import com.cloudbalance.lens.dto.auth.AuthResponseDTO;
import com.cloudbalance.lens.service.auth.AuthService;
import com.cloudbalance.lens.utils.Constant;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin() throws Exception {
        AuthResponseDTO mockResponse =  AuthResponseDTO.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .build();

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(mockResponse);

        String json = """
            {
                "email": "user@example.com",
                "password": "password123"
            }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("refresh-token")));

        verify(authService, times(1)).login(any(AuthRequestDTO.class));
    }

    @Test
    void testLogout() throws Exception {
        GlobalMessageDTO message = GlobalMessageDTO.builder()
                .message("Logged out successfully")
                .build();
        when(authService.logout(any())).thenReturn(message);

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        verify(authService, times(1)).logout(any());
    }

    @Test
    void testPublicKey() throws Exception {
        when(authService.publicKey()).thenReturn("public-key");

        mockMvc.perform(get("/auth/public-key"))
                .andExpect(status().isOk())
                .andExpect(content().string("public-key"));

        verify(authService, times(1)).publicKey();
    }

    @Test
    void testRefreshTokenSuccess() throws Exception {
        Map<String, String> mockTokens = Map.of(
                Constant.REFRESH_TOKEN, "new-refresh-token",
                Constant.ACCESS_TOKEN, "new-access-token"
        );

        when(authService.refreshToken(any())).thenReturn(mockTokens);

        MockHttpServletResponse response = mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("new-access-token"))
                .andReturn()
                .getResponse();

        String cookieHeader = response.getHeader(HttpHeaders.SET_COOKIE);
        assert cookieHeader != null;
        assert cookieHeader.contains("new-refresh-token");

        verify(authService, times(1)).refreshToken(any());
    }

    @Test
    void testRefreshTokenExpired() throws Exception {
        when(authService.refreshToken(any())).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Refresh token expired"));
    }

    @Test
    void testRefreshTokenGenericError() throws Exception {
        when(authService.refreshToken(any())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unexpected error"));
    }
}
