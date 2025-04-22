package com.cloudbalance.lens.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "dashboard_permissions")
public class DashboardPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dashboard", nullable = false)
    private String dashboard;

    @Column(name = "permission_type", nullable = false)
    private String permissionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
