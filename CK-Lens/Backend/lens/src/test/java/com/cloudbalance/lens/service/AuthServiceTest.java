package com.cloudbalance.lens.service;

import com.cloudbalance.lens.config.JwtUtil;
import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.auth.AuthRequestDTO;
import com.cloudbalance.lens.dto.auth.AuthResponseDTO;
import com.cloudbalance.lens.dto.auth.CustomUserDetails;
import com.cloudbalance.lens.entity.DashboardPermission;
import com.cloudbalance.lens.entity.Role;
import com.cloudbalance.lens.entity.User;
import com.cloudbalance.lens.exception.CustomException;
import com.cloudbalance.lens.repository.DashboardPermissionRepository;
import com.cloudbalance.lens.repository.UserRepository;
import com.cloudbalance.lens.service.auth.impl.AuthServiceImpl;
import com.cloudbalance.lens.service.auth.impl.CustomUserDetailsServiceImpl;
import com.cloudbalance.lens.utils.Constant;
import com.cloudbalance.lens.utils.PasswordDecryptorUtil;
import com.cloudbalance.lens.utils.TokenBlacklistUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsServiceImpl customUserDetailsService;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserRepository userRepository;
    @Mock private DashboardPermissionRepository dashboardPermissionRepository;
    @Mock private TokenBlacklistUtil tokenBlacklistUtil;
    @Mock private PasswordDecryptorUtil passwordDecryptorUtil;

    @InjectMocks private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_successful() {
        AuthRequestDTO loginDTO = AuthRequestDTO
                .builder()
                .password("encrypted")
                .username("john")
                .build();
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRole(Role.builder().id(1L).name("ADMIN").build());
        user.setLastAccessedTime(LocalDateTime.now());

        CustomUserDetails userDetails = new CustomUserDetails(user.getUsername(),user.getPassword(),user.getRole().getName(),user.isActive());

        when(passwordDecryptorUtil.decryptPassword("encrypted")).thenReturn("decrypted");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(customUserDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("token");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("refresh");
        when(dashboardPermissionRepository.findByRoleName("ADMIN")).thenReturn(List.of(
                DashboardPermission.builder().dashboard("Cost Explorer").permissionType("READ").build()
        ));

        AuthResponseDTO response = authService.login(loginDTO);

        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getToken()).isEqualTo("token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_invalidCredentials() {
        AuthRequestDTO loginDTO = AuthRequestDTO
                .builder()
                .username("john")
                .password("encrypted")
                .build();
        when(passwordDecryptorUtil.decryptPassword("encrypted")).thenReturn("decrypted");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginDTO))
                .isInstanceOf(CustomException.InvalidCredentialsException.class);
    }

    @Test
    void logout_validTokens() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie(Constant.REFRESH_TOKEN, "refreshToken");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer accessToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        GlobalMessageDTO response = authService.logout(request);

        assertThat(response.getMessage()).isEqualTo("Logout Successfully");
        verify(tokenBlacklistUtil).blacklistToken("accessToken");
        verify(tokenBlacklistUtil).blacklistToken("refreshToken");
    }

    @Test
    void refreshToken_successful() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie(Constant.REFRESH_TOKEN, "refreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        when(jwtUtil.extractUsername("refreshToken")).thenReturn("john");
        User user = new User();
        user.setUsername("john");
        CustomUserDetails userDetails = new CustomUserDetails(user.getUsername(),user.getPassword(),user.getRole().getName(),user.isActive());
        when(customUserDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtUtil.isTokenExpired("refreshToken")).thenReturn(false);
        when(jwtUtil.generateToken(userDetails)).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(userDetails)).thenReturn("newRefreshToken");

        Map<String, String> result = authService.refreshToken(request);

        assertThat(result).containsEntry(Constant.ACCESS_TOKEN, "newAccessToken")
                .containsEntry(Constant.REFRESH_TOKEN, "newRefreshToken");
        verify(tokenBlacklistUtil).blacklistToken("refreshToken");
    }

    @Test
    void refreshToken_missingToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Missing refresh token");
    }
}
