package com.cloudbalance.lens.dto.usermanagement;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserManagementDTO {
    private UserDTO userDTO;
    private List<Long> accountIds;
}
