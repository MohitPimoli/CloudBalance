package com.cloudbalance.lens.utils;

import com.cloudbalance.lens.dto.auth.UserDashboardPermission;
import com.cloudbalance.lens.entity.DashboardPermission;
import com.cloudbalance.lens.entity.Role;
import com.cloudbalance.lens.repository.DashboardPermissionRepository;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DashboardPermissions {

    private final DashboardPermissionRepository dashboardPermissionRepository;

    public DashboardPermissions(DashboardPermissionRepository dashboardPermissionRepository) {
        this.dashboardPermissionRepository = dashboardPermissionRepository;
    }

    public List<UserDashboardPermission> getDashboardPermissions(Role role) {

        List<DashboardPermission> permissions = dashboardPermissionRepository.findByRoleName(role.getName());
        return permissions.stream()
                .map(p -> UserDashboardPermission.builder()
                        .dashboard(p.getDashboard())
                        .permissionType(p.getPermissionType())
                        .build())
                .toList();
    }
}
