package com.cloudbalance.lens.service.auth;

import com.cloudbalance.lens.dto.auth.AuthResponseDTO;
import com.cloudbalance.lens.dto.auth.AuthRequestDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponseDTO login(AuthRequestDTO loginDTO);
    String logout(HttpServletRequest request);
    String publicKey();
}
