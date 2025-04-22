package com.cloudbalance.lens.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDashboardPermission {
    private String dashboard;
    private String permissionType;
}
