package com.cloudbalance.lens.dto.usermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserManagementResponse {
    private String message;
    private List<UserDTO> users;
}
