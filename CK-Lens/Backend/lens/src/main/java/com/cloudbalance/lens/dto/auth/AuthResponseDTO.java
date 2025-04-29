package com.cloudbalance.lens.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDTO {
    private Long id;
    private  String username;
    private String token;
    private String refreshToken;
    private String email;
    private String role;
    private String message;
    private List<UserDashboardPermission> dashboardPermissions;
}
