package com.cloudbalance.lens.dto.usermanagement;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserManagementResponse {
    private String message;
    private List<UserDTO> users;
}
