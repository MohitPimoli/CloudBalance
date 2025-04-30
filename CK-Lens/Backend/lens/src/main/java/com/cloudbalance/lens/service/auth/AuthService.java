package com.cloudbalance.lens.service.auth;

import com.cloudbalance.lens.dto.GlobalMessageDTO;
import com.cloudbalance.lens.dto.auth.AuthRequestDTO;
import com.cloudbalance.lens.dto.auth.AuthResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {

    AuthResponseDTO login(AuthRequestDTO loginDTO);
    GlobalMessageDTO logout(HttpServletRequest request);
    String publicKey();
    Map<String,String> refreshToken(HttpServletRequest request);
}
