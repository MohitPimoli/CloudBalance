package com.cloudbalance.lens.dto.usermanagement;

import com.cloudbalance.lens.validation.OnCreate;
import com.cloudbalance.lens.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @NotBlank(message = "id is required", groups = OnUpdate.class)
    private Long id;

    private String username;

    @NotBlank(message = "First name is required", groups = OnCreate.class)
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "First name must contain only letters and numbers",
            groups = { OnCreate.class, OnUpdate.class }
    )
    private String firstName;

    @NotBlank(message = "Last name is required", groups = OnCreate.class)
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Last name must contain only letters and numbers",
            groups = { OnCreate.class, OnUpdate.class }
    )
    private String lastName;

    @NotBlank(message = "Email is required", groups = OnCreate.class)
    @Email(message = "Invalid email format", groups = { OnCreate.class, OnUpdate.class })
    private String email;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    @Size(min = 8, message = "Password must be at least 8 characters long", groups = { OnCreate.class, OnUpdate.class })
    private String password;

    @NotBlank(message = "Role name is required", groups = OnCreate.class)
    @Pattern(
            regexp = "^(CUSTOMER|ADMIN|READ-ONLY)$",
            message = "Role must be one of: CUSTOMER, ADMIN, READ-ONLY",
            groups = { OnCreate.class, OnUpdate.class }
    )
    private String roleName;

    private Boolean active;
    private String lastLogin;
}
