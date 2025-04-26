package com.cloudbalance.lens.dto.usermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserManagementDTO {
    private UserDTO userDTO;
    private List<Long> accountIds;
}
