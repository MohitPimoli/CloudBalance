package com.cloudbalance.lens.dto.auth;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AuthResponseDTO {
    private Long id;
    private  String username;
    private String token;
    private String email;
    private String role;
    private String message;
    private List<UserDashboardPermission> dashboardPermissions;
}
