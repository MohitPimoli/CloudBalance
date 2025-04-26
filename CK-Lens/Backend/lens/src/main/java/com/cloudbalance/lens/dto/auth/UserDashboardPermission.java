package com.cloudbalance.lens.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDashboardPermission {
    private String dashboard;
    private String permissionType;
}
